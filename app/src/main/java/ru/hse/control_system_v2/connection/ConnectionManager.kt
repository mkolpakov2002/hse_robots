package ru.hse.control_system_v2.connection

import android.content.Context
import ru.hse.control_system_v2.AppConstants
import ru.hse.control_system_v2.connection.bluetooth.BluetoothConnection
import ru.hse.control_system_v2.connection.wifi.IpClientConnection
import ru.hse.control_system_v2.data.classes.device.model.DeviceModel

// Класс для управления разными типами соединений с устройствами
class ConnectionManager(deviceItemTypeList: Map<DeviceModel, String>?) {

    // Переменная для хранения типа соединения
    private lateinit var connectionType: String

    // Список объектов, представляющих разные соединения
    private var connectionList: ArrayList<ConnectionClass<*>> = ArrayList()

    // Проверка на наличие активных соединений
    public val isActive: Boolean
        get() {
            connectionList.forEach {
                if (it.isActive){
                    return true
                }
            }
            return false
        }

    // Метод для создания списка соединений в зависимости от типа устройства
    public fun createConnectionList(){
        deviceItemTypeList?.forEach {
            if (it.value == AppConstants.CONNECTION_LIST[0]) {
                // Создание объекта для Bluetooth-соединения с устройством
                val connectionItem = BluetoothConnection(it.key, it.key.name)
                connectionList.add(connectionItem)
            } else if(it.value == AppConstants.CONNECTION_LIST[1]){
                // Создание объекта для IP-соединения с устройством
                val connectionItem = IpClientConnection(it.key, it.key.name)
                connectionList.add(connectionItem)
            }
        }
    }

    companion object {
        // Переменная для хранения карты устройств и их типов соединений
        private var deviceItemTypeList: Map<DeviceModel, String>? = null

        // Переменная для хранения единственного экземпляра класса ConnectionManager
        private var INSTANCE: ConnectionManager? = null

        // Метод для получения экземпляра класса ConnectionManager
        fun getConnectionManager(context: Context): ConnectionManager? {
            if (INSTANCE == null){
                // Создание экземпляра класса ConnectionManager с переданной картой устройств и их типов соединений
                INSTANCE = ConnectionManager(deviceItemTypeList)
            }
            return INSTANCE
        }

        // Метод для закрытия всех соединений и удаления экземпляра класса ConnectionManager
        suspend fun destroyConnectionManager(){
            INSTANCE?.connectionList?.forEach {
                // Закрытие каждого соединения в списке
                it.closeConnection()
            }
            INSTANCE = null
            deviceItemTypeList = null
        }

        // Метод для установки карты устройств и их типов соединений
        fun setDevicesForConnection(devices: Map<DeviceModel, String>){
            deviceItemTypeList = devices
        }

    }

}