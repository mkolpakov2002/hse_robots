package ru.hse.control_system_v2.connection.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.*
import ru.hse.control_system_v2.AppConstants
import ru.hse.control_system_v2.connection.ConnectionClass
import ru.hse.control_system_v2.connection.ConnectionFactory
import ru.hse.control_system_v2.data.classes.device.model.DeviceModel
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

//реализация соединения по bluetooth
class BluetoothConnection constructor(deviceItemType: DeviceModel, connectionName: String?)
    : ConnectionClass<BluetoothSocket?>(deviceItemType, connectionName) {
    // SPP UUID сервиса согласно документации Android:
    /*
    Hint: If you are connecting to a Bluetooth serial board,
     then try using the well-known SPP UUID 00001101-0000-1000-8000-00805F9B34FB
     https://developer.android.com/reference/android/bluetooth/BluetoothDevice.html
     */
    private val MY_UUID = AppConstants.APP_BLUETOOTH_UUID

    // Объявляем переменные для хранения ссылок на Bluetooth адаптер и устройство
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothDevice: BluetoothDevice

    // Объявляем переменные для хранения потоков ввода и вывода данных
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    // Объявляем переменную для хранения размера буфера для чтения и записи данных
    private val bufferSize = 1024

    // Переопределяем метод sentData для отправки данных по Bluetooth
    override suspend fun sentData(data: ByteArray) {
        // Отправляем данные по Bluetooth с помощью функции outputStream?.write
        withContext(Dispatchers.IO) {
            outputStream?.write(data)
        }
    }

    // Переопределяем метод closeConnection для закрытия соединения по Bluetooth
    override suspend fun closeConnection() {
        // Отменяем корутину с помощью функции coroutine?.cancel
        coroutine?.cancel()
        // Закрываем потоки ввода и вывода с помощью функций inputStream?.close и outputStream?.close
        withContext(Dispatchers.IO) {
            inputStream?.close()
            outputStream?.close()
        }
        // Закрываем соединение по Bluetooth с помощью функции socket?.close
        socket?.close()
    }

    // Переопределяем метод openConnection для открытия соединения по Bluetooth
    @SuppressLint("MissingPermission")
    override suspend fun openConnection() {
        // Получаем ссылку на Bluetooth адаптер с помощью функции BluetoothAdapter.getDefaultAdapter
        bluetoothAdapter = ConnectionFactory.connectionFactory.bluetoothManager.adapter
        //ещё одна проверка на состояние Bluetooth
        //устройство с выбранным MAC как объект
        val device = bluetoothAdapter.getRemoteDevice(deviceItemType.bluetoothAddress)
        // Попытка подключиться к устройству
        try {
            socket = device.javaClass
                .getMethod("createRfcommSocketToServiceRecord", UUID::class.java)
                .invoke(device, MY_UUID) as BluetoothSocket
        } catch (e: Exception) {
            e.message?.let { Log.d(AppConstants.TAG, it) }
            //подключение неуспешно
            connectionState = isOnError
        }
        try {
            connectionState = if (socket != null) {
                socket?.connect()
                Log.d(
                    AppConstants.TAG,
                    "...Соединение установлено и готово к передачи данных..."
                )
                //соединение успешно
                isAlive
            } else {
                //подключение неуспешно
                isOnError
            }
        } catch (e: Exception) {
            //подключение неуспешно
            connectionState = isOnError
            try {
                // В случае ошибки пытаемся закрыть соединение
                socket?.close()
            } catch (closeException: IOException) {
                //запись логов ошибки
                e.message?.let { Log.d(AppConstants.TAG, it) }
            }
            //запись логов ошибки
            e.message?.let { Log.d(AppConstants.TAG, it) }
        }
        // Получаем потоки ввода и вывода данных с помощью функций socket?.inputStream и socket?.outputStream и присваиваем их inputStream и outputStream
        inputStream = socket?.inputStream
        outputStream = socket?.outputStream

        // Создаем объект скоупа для корутины с помощью функции CoroutineScope
        val scope = CoroutineScope(Dispatchers.IO)

        // Запускаем корутину с помощью функции scope.launch
        coroutine = scope.launch {
            // Внутри блока launch выполняем код для коммуникации с устройством

            // Пытаемся читать данные от устройства в цикле while, пока соединение активно и корутина не отменена
            while (socket != null && isActive) {
                // Создаем буфер для хранения данных с помощью функции ByteArray
                val buffer = ByteArray(bufferSize)
                // Читаем данные по Bluetooth с помощью функции inputStream?.read и сохраняем их в буфере
                withContext(Dispatchers.IO) {
                    val bytesRead = inputStream?.read(buffer)
                    // Проверяем, что количество прочитанных байтов больше нуля
                    if (bytesRead != null && bytesRead > 0) {
                        // Обрабатываем прочитанные данные как нужно
                        Log.d(
                            "MainActivity",
                            "Received data: ${buffer.sliceArray(0 until bytesRead)}"
                        )
                        read(buffer, bytesRead)
                    }
                }
            }
        }
    }

    override suspend fun read(buffer: ByteArray, bytesRead: Int): Int {
        TODO("Not yet implemented")

    }


}