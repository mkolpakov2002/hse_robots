package ru.hse.control_system_v2.connection

import android.bluetooth.BluetoothSocket
import android.content.Context
import androidx.room.Room
import ru.hse.control_system_v2.App
import ru.hse.control_system_v2.AppConstants
import ru.hse.control_system_v2.data.AppDatabase
import ru.hse.control_system_v2.data.DeviceItemType
import ru.hse.control_system_v2.data.DeviceItemTypeDao
import ru.hse.control_system_v2.data.ProtocolDBHelper

//Single instance менеджер всех соединений
class ConnectionManager(deviceItemTypeList: Map<DeviceItemType, String>?) {

    private lateinit var connectionType: String

    private var connectionList: ArrayList<ConnectionClass<*>>

    init {
        connectionList = ArrayList()
        createConnectionList()
    }

    private fun createConnectionList(){
        deviceItemTypeList?.forEach {
            if (it.value == AppConstants.CONNECTION_LIST[0]) {
                val connectionItem = BluetoothConnection(it.key, it.key.name)
                connectionList.add(connectionItem)
            } else if(it.value == AppConstants.CONNECTION_LIST[1]){
                val connectionItem = IpConnection(it.key, it.key.name)
                connectionList.add(connectionItem)
            }
        }
    }

    companion object {
        private var deviceItemTypeList: Map<DeviceItemType, String>? = null

        private var INSTANCE: ConnectionManager? = null

        fun getConnectionManager(context: Context): ConnectionManager? {
            if (INSTANCE == null){
                INSTANCE = ConnectionManager(deviceItemTypeList)
            }
            return INSTANCE
        }

        fun destroyConnectionManager(){
            INSTANCE = null
            deviceItemTypeList = null
        }

        fun setDevicesForConnection(devices: Map<DeviceItemType, String>){
            deviceItemTypeList = devices
        }
    }

}