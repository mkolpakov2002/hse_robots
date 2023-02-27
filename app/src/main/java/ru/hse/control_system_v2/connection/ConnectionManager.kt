package ru.hse.control_system_v2.connection

import android.content.Context
import ru.hse.control_system_v2.AppConstants
import ru.hse.control_system_v2.data.DeviceItemType

//Single instance менеджер всех соединений
class ConnectionManager(deviceItemTypeList: Map<DeviceItemType, String>?) {

    private lateinit var connectionType: String

    private var connectionList: ArrayList<ConnectionClass<*>> = ArrayList()

    init {
        createConnectionList()
    }

    private fun createConnectionList(){
        deviceItemTypeList?.forEach {
            if (it.value == AppConstants.CONNECTION_LIST[0]) {
                val connectionItem = BluetoothConnection(it.key, it.key.name)
                connectionList.add(connectionItem)
            } else if(it.value == AppConstants.CONNECTION_LIST[1]){
                val connectionItem = IpClientConnection(it.key, it.key.name)
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

        suspend fun destroyConnectionManager(){
            INSTANCE?.connectionList?.forEach {
                it.closeConnection()
            }
            INSTANCE = null
            deviceItemTypeList = null
        }

        fun setDevicesForConnection(devices: Map<DeviceItemType, String>){
            deviceItemTypeList = devices
        }
    }

}