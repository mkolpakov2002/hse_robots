package ru.hse.control_system_v2.connection.usb

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.hse.control_system_v2.connection.ConnectionClass
import ru.hse.control_system_v2.connection.ConnectionFactory
import ru.hse.control_system_v2.data.classes.device.model.DeviceModel
import java.nio.ByteBuffer

/**
 * Класс USB соединения
 */
class USBConnection(deviceItemType: DeviceModel, connectionName: String?) :
    ConnectionClass<UsbDeviceConnection?>(deviceItemType, connectionName) {

    // Объявляем переменные для хранения ссылок на USB менеджер, устройство и интерфейс
    private lateinit var usbManager: UsbManager
    private lateinit var usbDevice: UsbDevice
    private lateinit var usbInterface: UsbInterface

    // Объявляем переменную для хранения размера буфера для чтения и записи данных
    private val bufferSize = 1024

    // Переопределяем метод sentData для отправки данных по USB
    override suspend fun sentData(data: ByteArray) {
        // Получаем ссылку на конечную точку для записи данных
        val writeEndpoint = usbInterface.getEndpoint(1)
        // Отправляем данные по USB с помощью функции socket?.bulkTransfer
        socket?.bulkTransfer(writeEndpoint, data, data.size, 0)
    }

    // Переопределяем метод closeConnection для закрытия соединения по USB
    override suspend fun closeConnection() {
        // Отменяем корутину с помощью функции coroutine?.cancel
        coroutine?.cancel()
        // Освобождаем интерфейс USB с помощью функции socket?.releaseInterface
        socket?.releaseInterface(usbInterface)
        // Закрываем соединение по USB с помощью функции socket?.close
        socket?.close()
    }

    override suspend fun read(buffer: ByteArray, bytesRead: Int): Int {
        TODO("Not yet implemented")
    }

    // Переопределяем метод openConnection для открытия соединения по USB
    override suspend fun openConnection() {
        // Получаем ссылку на USB менеджер с помощью функции getSystemService
        usbManager = ConnectionFactory.connectionFactory.usbManager
        // Получаем список подключенных USB устройств с помощью функции usbManager.deviceList
        val deviceList = usbManager.deviceList
        // Находим нужное устройство по имени или идентификатору из deviceItemType
        usbDevice = deviceList[deviceItemType.name] ?: return
        // Получаем ссылку на первый интерфейс USB устройства с помощью функции usbDevice.getInterface
        usbInterface = usbDevice.getInterface(0)
        // Открываем соединение по USB с помощью функции usbManager.openDevice и присваиваем его socket
        socket = usbManager.openDevice(usbDevice)
        // Захватываем интерфейс USB с помощью функции socket?.claimInterface
        socket?.claimInterface(usbInterface, true)

        // Создаем объект скоупа для корутины с помощью функции CoroutineScope
        val scope = CoroutineScope(Dispatchers.IO)

        // Запускаем корутину с помощью функции scope.launch
        coroutine = scope.launch {
            // Внутри блока launch выполняем код для коммуникации с устройством
            // Пытаемся читать данные от устройства в цикле while, пока соединение активно и корутина не отменена
            while (socket != null && isActive) {
                connectionState = isAlive
                // Создаем буфер для хранения данных с помощью функции ByteBuffer.allocate
                val buffer = ByteBuffer.allocate(bufferSize)
                // Получаем ссылку на конечную точку для чтения данных
                val readEndpoint = usbInterface.getEndpoint(0)
                // Читаем данные по USB с помощью функции socket?.bulkTransfer и сохраняем их в буфере
                val bytesRead = socket?.bulkTransfer(readEndpoint, buffer.array(), bufferSize, 0)
                // Проверяем, что количество прочитанных байтов больше нуля
                if (bytesRead != null && bytesRead > 0) {
                    // Обрабатываем прочитанные данные как нужно
                    Log.d(
                        "MainActivity",
                        "Received data: ${buffer.array().sliceArray(0 until bytesRead)}"
                    )
                }
            }

        }
    }
}