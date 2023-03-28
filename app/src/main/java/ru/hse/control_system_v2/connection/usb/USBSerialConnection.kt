package ru.hse.control_system_v2.connection.usb

import android.content.Context.USB_SERVICE
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.util.Log
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.hse.control_system_v2.App
import ru.hse.control_system_v2.connection.ConnectionClass
import ru.hse.control_system_v2.data.classes.device.model.DeviceModel
import java.nio.ByteBuffer


class USBSerialConnection(deviceItemType: DeviceModel, connectionName: String?) :
    ConnectionClass<UsbSerialPort?>(deviceItemType, connectionName) {

    // Объявляем переменные для хранения ссылок на USB менеджер и драйвер
    private lateinit var usbManager: UsbManager
    private lateinit var usbDriver: UsbSerialDriver

    // Объявляем переменную для хранения размера буфера для чтения и записи данных
    private val bufferSize = 1024

    // Переопределяем метод sentData для отправки данных по USB
    override suspend fun sentData(data: ByteArray) {
        // Отправляем данные по USB с помощью функции socket?.write
        socket?.write(data, 0)
    }

    // Переопределяем метод closeConnection для закрытия соединения по USB
    override suspend fun closeConnection() {
        // Отменяем корутину с помощью функции coroutine?.cancel
        coroutine?.cancel()
        // Закрываем соединение по USB с помощью функции socket?.close
        socket?.close()
    }

    // Переопределяем метод openConnection для открытия соединения по USB
    override suspend fun openConnection() {
        // Получаем ссылку на USB менеджер с помощью функции getSystemService
        usbManager = App.context.getSystemService(USB_SERVICE) as UsbManager
        // Получаем список доступных USB драйверов с помощью функции UsbSerialProber.getDefaultProber().findAllDrivers
        val drivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        // Находим нужный драйвер по имени или идентификатору из deviceItemType
        usbDriver = drivers.find { it.device.manufacturerName == deviceItemType.manufacture
                && it.device.vendorId == deviceItemType.vendorId } ?: return
        // Получаем ссылку на первый порт USB драйвера с помощью функции usbDriver.ports[0]
        socket = usbDriver.ports[0]


        val connection: UsbDeviceConnection = usbManager.openDevice(usbDriver.device)
            ?: // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
            return

        //socket лучше понимать как port
        socket = usbDriver.ports[0] // Most devices have just one port (port 0)

        // Открываем соединение по USB с помощью функции socket?.open и передаем ему usbManager
        socket?.open(connection)
        // Настраиваем параметры соединения с помощью функции socket?.setParameters и connectionParams
//        socket?.setParameters(connectionParams.baudRate, connectionParams.dataBits, connectionParams.stopBits, connectionParams.parity)
        socket?.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        // Создаем объект скоупа для корутины с помощью функции CoroutineScope
        val scope = CoroutineScope(Dispatchers.IO)

        // Запускаем корутину с помощью функции scope.launch
        coroutine = scope.launch {
            // Внутри блока launch выполняем код для коммуникации с устройством

            // Пытаемся читать данные от устройства в цикле while, пока соединение активно и корутина не отменена
            while (socket != null && isActive) {
                // Создаем буфер для хранения данных с помощью функции ByteBuffer.allocate
                val buffer = ByteBuffer.allocate(bufferSize)
                // Читаем данные по USB с помощью функции socket?.read и сохраняем их в буфере
                val bytesRead = socket?.read(buffer.array(), 0)
                // Проверяем, что количество прочитанных байтов больше нуля
                if (bytesRead != null && bytesRead > 0) {
                    // Обрабатываем прочитанные данные как нужно
                    Log.d("MainActivity", "Received data: ${buffer.array().sliceArray(0 until bytesRead)}")
                }
            }
        }
    }

    override suspend fun read(buffer: ByteArray, bytesRead: Int): Int {
        TODO("Not yet implemented")
    }
}