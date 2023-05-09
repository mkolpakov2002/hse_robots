package ru.hse.control_system_v2.connection

import kotlinx.coroutines.Job
import ru.hse.control_system_v2.connection.data.classes.ConnectionDeviceModel

//класс обёртка сокета для одного устройства
abstract class ConnectionClass<T>(val connectionDeviceModel: ConnectionDeviceModel) {
    enum class ConnectionState {
        ON_ERROR,
        CONNECTING,
        ALIVE,
        DISABLED
    }

    var connectionState = ConnectionState.CONNECTING

    protected var socket: T? = null

    protected var coroutine: Job? = null

    abstract suspend fun sentData(data: ByteArray)
    abstract suspend fun closeConnection()
    abstract suspend fun openConnection()
    abstract suspend fun read(buffer: ByteArray, bytesRead: Int): Int
}