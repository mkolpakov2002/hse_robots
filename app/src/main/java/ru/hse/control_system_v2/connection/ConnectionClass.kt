package ru.hse.control_system_v2.connection

import kotlinx.coroutines.Job
import ru.hse.control_system_v2.data.classes.device.model.DeviceModel

//класс обёртка сокета для одного устройства
abstract class ConnectionClass<T>(val deviceItemType: DeviceModel,
                                  val connectionName: String?) {
    var isNeedToReConnect = false

    var isActive = false

    var socket: T? = null

    var coroutine: Job? = null

    abstract suspend fun sentData(data: ByteArray)
    abstract suspend fun closeConnection()
    abstract suspend fun openConnection()
    abstract suspend fun read(buffer: ByteArray, bytesRead: Int): Int
}