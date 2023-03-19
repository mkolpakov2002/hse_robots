package ru.hse.control_system_v2.connection

import ru.hse.control_system_v2.data.classes.device.model.DeviceModel
import java.nio.ByteBuffer

//класс обёртка сокета для одного устройства
abstract class ConnectionClass<T>(val deviceItemType: DeviceModel,
                                  val connectionName: String?) {
    var isNeedToConnect = false

    var socket: T? = null

    abstract suspend fun sentData()
    abstract suspend fun closeConnection()
    abstract suspend fun openConnection()
    abstract suspend fun read(buffer: ByteBuffer): Int
}