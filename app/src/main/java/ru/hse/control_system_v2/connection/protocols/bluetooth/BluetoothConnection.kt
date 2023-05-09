package ru.hse.control_system_v2.connection.protocols.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.hse.control_system_v2.AppConstants
import ru.hse.control_system_v2.connection.ConnectionClass
import ru.hse.control_system_v2.connection.ConnectionFactory
import ru.hse.control_system_v2.connection.data.classes.ConnectionDeviceModel
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

//реализация соединения по bluetooth
class BluetoothConnection constructor(connectionDeviceModel: ConnectionDeviceModel) : ConnectionClass<BluetoothSocket?>(connectionDeviceModel) {
    // SPP UUID сервиса согласно документации Android:
    /*
    Hint: If you are connecting to a Bluetooth serial board,
     then try using the well-known SPP UUID 00001101-0000-1000-8000-00805F9B34FB
     https://developer.android.com/reference/android/bluetooth/BluetoothDevice.html
     */
    private val MY_UUID = AppConstants.APP_BLUETOOTH_UUID

    // Объявляем переменные для хранения ссылок на Bluetooth адаптер и устройство
    private val bluetoothAdapter: BluetoothAdapter = ConnectionFactory.bluetoothManager.adapter
    private val bluetoothDevice: BluetoothDevice = bluetoothAdapter.getRemoteDevice(
        connectionDeviceModel.deviceItemType.bluetoothAddress)

    // Объявляем переменные для хранения потоков ввода и вывода данных
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    // Объявляем переменную для хранения размера буфера для чтения и записи данных
    private val bufferSize = 1024

    // Переопределяем метод sentData для отправки данных по Bluetooth
    override suspend fun sentData(data: ByteArray) {
        // Отправляем данные по Bluetooth с помощью функции outputStream?.write
        withContext(Dispatchers.IO) {
            outputStream?.write(data) ?: throw IOException("Output stream is null")
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

    // Определяем интерфейс для разных алгоритмов чтения данных
    interface DataReader {
        fun read(buffer: ByteArray, bytesRead: Int)
    }

    // Реализуем конкретные алгоритмы для разных форматов данных
    class TextDataReader : DataReader {
        override fun read(buffer: ByteArray, bytesRead: Int) {
            // Преобразуем байты в текст
            val text = String(buffer, 0, bytesRead)
            Log.d("MainActivity", "Received text: $text")
        }
    }

    class BinaryDataReader : DataReader {
        override fun read(buffer: ByteArray, bytesRead: Int) {
            // Оставляем байты без изменений
            Log.d("MainActivity", "Received bytes: ${buffer.size}")
        }
    }

    // Создаем класс контекста, который хранит ссылку на текущий алгоритм
    class DataContext(var reader: DataReader) {
        // Вызываем метод чтения данных у текущего алгоритма
        fun processData(buffer: ByteArray, bytesRead: Int) {
            reader.read(buffer, bytesRead)
        }
    }

    // Переопределяем метод openConnection для открытия соединения по Bluetooth
    override suspend fun openConnection() {
        connectionState = ConnectionState.CONNECTING
        val device = ConnectionFactory.bluetoothManager.adapter
            .getRemoteDevice(connectionDeviceModel.deviceItemType.bluetoothAddress)
        val result = runCatching {
            socket = device.javaClass
                .getMethod("createRfcommSocketToServiceRecord", UUID::class.java)
                .invoke(device, MY_UUID) as BluetoothSocket
            socket?.connect()
            Log.d(
                AppConstants.TAG,
                "...Соединение установлено"
            )
            ConnectionState.ALIVE
        }
        connectionState = result.getOrElse {
            Log.d(AppConstants.TAG, it.message ?: "Unknown error")
            socket?.close()
            ConnectionState.ON_ERROR
        }
        inputStream = socket?.inputStream
        outputStream = socket?.outputStream
        coroutine = CoroutineScope(Dispatchers.IO).launch {
            val flow = flow {
                while (socket != null && isActive) {
                    val buffer = ByteArray(bufferSize)
                    val bytesRead = runInterruptible(Dispatchers.IO) {
                        inputStream?.read(buffer) ?: 0
                    }
                    if (bytesRead > 0) {
                        emit(buffer to bytesRead)
                    }
                }
            }
            flow.collect { (buffer, bytesRead) ->
                // Создаем объекты для чтения текстовых и бинарных данных
                val textReader = TextDataReader()
                val binaryReader = BinaryDataReader()
                // Создаем контекст и устанавливаем начальный алгоритм
                val context = DataContext(textReader)
                context.reader = binaryReader
                context.processData(buffer, bytesRead)
                // Определяем формат данных по первому байту в буфере
//                when (buffer[0]) {
//                    0x01 -> { // Текстовый формат
//                        // Используем алгоритм для текстовых данных
//                        context.reader = textReader
//                        context.processData(buffer, bytesRead)
//                    }
//                    0x02 -> { // Бинарный формат
//                        // Используем алгоритм для бинарных данных
//                        context.reader = binaryReader
//                        context.processData(buffer, bytesRead)
//                    }
//                    else -> { // Неизвестный формат или ошибка
//                        Log.e("MainActivity", "Invalid data format")
//                    }
//                }
            }
        }
    }

    override suspend fun read(buffer: ByteArray, bytesRead: Int): Int {
        TODO("Not yet implemented")

    }


}