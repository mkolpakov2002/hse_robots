package ru.hse.control_system_v2.domain.connection

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.hse.control_system_v2.model.entities.ConnectionDeviceModel

abstract class ConnectionClass<T>(val connectionDeviceModel: ConnectionDeviceModel) {
    enum class ConnectionState {
        DISCONNECTED,
        CONNECTING,
        ALIVE,
        DISABLED
    }

    private val _stateFlow = MutableStateFlow(ConnectionState.DISABLED)
    val stateFlow: StateFlow<ConnectionState> = _stateFlow.asStateFlow()

    protected var socket: T? = null
    protected var coroutine: Job? = null

    abstract suspend fun sendData(data: ByteArray)
    abstract suspend fun closeConnection()
    abstract suspend fun openConnection()
    abstract suspend fun read(buffer: ByteArray): Int

    fun updateState(newState: ConnectionState) {
        _stateFlow.value = newState
    }

    init {
        updateState(ConnectionState.DISABLED)
    }
}