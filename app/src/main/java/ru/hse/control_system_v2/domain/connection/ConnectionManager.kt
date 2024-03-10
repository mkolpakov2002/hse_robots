package ru.hse.control_system_v2.domain.connection

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.hse.control_system_v2.utility.AppConstants
import ru.hse.control_system_v2.model.entities.ConnectionDeviceModel
import ru.hse.control_system_v2.domain.connection.protocols.bluetooth.BluetoothConnection
import ru.hse.control_system_v2.domain.connection.protocols.wifi.IpClientSocketConnection
import java.util.Collections
object ConnectionManager {
    private val connectionList = Collections.synchronizedList(mutableListOf<ConnectionClass<*>>())
    private val _connectionListFlow = MutableStateFlow<List<ConnectionClass<*>>>(emptyList())
    val connectionListFlow: StateFlow<List<ConnectionClass<*>>> = _connectionListFlow.asStateFlow()

    private val pendingConnections = mutableListOf<ConnectionDeviceModel>()

    fun prepareConnections(connectionDeviceModelList: List<ConnectionDeviceModel>) {
        pendingConnections.clear()
        pendingConnections.addAll(connectionDeviceModelList)
    }

    suspend fun openPendingConnections(): List<ConnectionClass<*>> {
        pendingConnections.forEach { model ->
            val connection = createConnection(model)
            connection?.let {
                it.openConnection()
                addConnection(it)
            }
        }
        pendingConnections.clear()
        return connectionList
    }

    private fun createConnection(model: ConnectionDeviceModel): ConnectionClass<*>? {
        return when (model.connectionType.connectionProtocol) {
            AppConstants.CONNECTION_LIST[0] -> BluetoothConnection(model)
            AppConstants.CONNECTION_LIST[1] -> IpClientSocketConnection(model)
            else -> null
        }
    }

    suspend fun destroyConnectionManager() {
        connectionList.forEach {
            it.closeConnection()
        }
        connectionList.clear()
        _connectionListFlow.value = connectionList
    }

    private fun addConnection(connection: ConnectionClass<*>) {
        synchronized(connectionList) {
            connectionList.add(connection)
            _connectionListFlow.value = connectionList
        }
    }

    suspend fun removeConnection(connection: ConnectionClass<*>) {
        synchronized(connectionList) {
            connectionList.remove(connection)
            _connectionListFlow.value = connectionList
        }
    }
}