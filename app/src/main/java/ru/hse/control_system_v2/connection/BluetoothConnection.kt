package ru.hse.control_system_v2.connection

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.*
import ru.hse.control_system_v2.AppConstants
import ru.hse.control_system_v2.data.DeviceItemType
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.nio.ByteBuffer
import java.util.*

//реализация соединения по bluetooth
class BluetoothConnection constructor(deviceItemType: DeviceItemType, connectionName: String?)
    : ConnectionClass<BluetoothSocket?>(deviceItemType, connectionName) {
    // SPP UUID сервиса согласно документации Android:
    /*
    Hint: If you are connecting to a Bluetooth serial board,
     then try using the well-known SPP UUID 00001101-0000-1000-8000-00805F9B34FB
     https://developer.android.com/reference/android/bluetooth/BluetoothDevice.html
     */
    private val MY_UUID = AppConstants.APP_BLUETOOTH_UUID
    var btAdapter: BluetoothAdapter? = null

    init {

    }

    override suspend fun sentData() {

    }

    override suspend fun closeConnection() {

    }

    @SuppressLint("MissingPermission")
    override suspend fun openConnection() {
        //TODO("Not yet implemented")
        var clientSocket: BluetoothSocket? = null
        //получаем локальный Bluetooth адаптер устройства
        btAdapter = BluetoothAdapter.getDefaultAdapter()
        //ещё одна проверка на состояние Bluetooth
        //устройство с выбранным MAC как объект
        val device = btAdapter!!.getRemoteDevice(deviceItemType.deviceMAC)
        // Попытка подключиться к устройству
        try {
            clientSocket = device.javaClass
                .getMethod("createRfcommSocketToServiceRecord", UUID::class.java)
                .invoke(device, MY_UUID) as BluetoothSocket
        } catch (e: NoSuchMethodException) {
            Log.d(AppConstants.TAG, e.message!!)
            //подключение неуспешно
            isNeedToConnect = true
        } catch (e: IllegalAccessException) {
            Log.d(AppConstants.TAG, e.message!!)
            isNeedToConnect = true
        } catch (e: InvocationTargetException) {
            Log.d(AppConstants.TAG, e.message!!)
            isNeedToConnect = true
        }
        try {
            isNeedToConnect = if (clientSocket != null) {
                clientSocket.connect()
                Log.d(
                    AppConstants.TAG,
                    "...Соединение установлено и готово к передачи данных..."
                )
                //соединение успешно
                false
            } else {
                //подключение неуспешно
                true
            }
        } catch (e: IOException) {
            //подключение неуспешно
            isNeedToConnect = true
            try {
                // В случае ошибки пытаемся закрыть соединение
                clientSocket!!.close()
            } catch (closeException: IOException) {
                //запись логов ошибки
                Log.d(AppConstants.TAG, e.message!!)
            }
            //запись логов ошибки
            Log.d(AppConstants.TAG, e.message!!)
        }
    }

    override suspend fun read(buffer: ByteBuffer): Int {
        TODO("Not yet implemented")
        //return socket.inputStream(buffer)
    }


}