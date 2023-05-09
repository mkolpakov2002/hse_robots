package ru.hse.control_system_v2.connection

import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.hamcrest.core.AnyOf
import ru.hse.control_system_v2.AppConstants
import ru.hse.control_system_v2.connection.data.classes.ConnectionDeviceModel
import ru.hse.control_system_v2.connection.protocols.bluetooth.BluetoothConnection
import ru.hse.control_system_v2.connection.protocols.wifi.IpClientConnection

// Класс для управления разными типами соединений с устройствами
object ConnectionManager {

    // Переменная для хранения типа соединения
    private lateinit var connectionType: String

    // Список объектов, представляющих разные соединения
    private var connectionList: MutableList<ConnectionClass<*>> = ArrayList()
    // Создаем Flow для эмитирования списка соединений
    private val connectionListFlow = MutableStateFlow(connectionList)
    // Создаем функцию для получения Flow извне
    fun getConnectionListFlow(): Flow<List<ConnectionClass<*>>> {
        return connectionListFlow
    }

    // Метод для создания списка соединений в зависимости от типа устройства
    public suspend fun openConnection(connectionDeviceModelList: List<ConnectionDeviceModel>):
            MutableList<ConnectionClass<*>> {
        connectionDeviceModelList.forEach {
            if (it.connectionType.connectionProtocol == AppConstants.CONNECTION_LIST[0]) {
                // Создание объекта для Bluetooth-соединения с устройством
                val connectionItem = BluetoothConnection(it)
                connectionItem.openConnection()
                connectionList.add(connectionItem)
            } else if(it.connectionType.connectionProtocol == AppConstants.CONNECTION_LIST[1]){
                // Создание объекта для IP-соединения с устройством
                val connectionItem = IpClientConnection(it)
                connectionItem.openConnection()
                connectionList.add(connectionItem)
            }
        }
        return connectionList
    }



    // Переменная для хранения карты устройств и их типов соединений
    private var deviceItemTypeList: ArrayList<ConnectionDeviceModel>? = null

    // Метод для получения экземпляра класса ConnectionManager

    // Метод для закрытия всех соединений и удаления экземпляра класса ConnectionManager
    suspend fun destroyConnectionManager(){
        this.connectionList.forEach {
            // Закрытие каждого соединения в списке
            it.closeConnection()
        }
        deviceItemTypeList = null
    }

    // Метод для установки карты устройств и их типов соединений
    fun setDevicesForConnection(connectionDeviceModel: ArrayList<ConnectionDeviceModel>){
        deviceItemTypeList = connectionDeviceModel
    }

}