package ru.hse.control_system_v2.connection.wifi

import io.ktor.client.*
import io.ktor.client.request.*
import ru.hse.control_system_v2.connection.ConnectionClass
import ru.hse.control_system_v2.data.classes.device.model.DeviceModel
import java.net.Socket
import java.nio.ByteBuffer

open class IpClientConnection(deviceItemType: DeviceModel, connectionName: String?) :
    ConnectionClass<Socket?>(deviceItemType, connectionName) {

    private lateinit var client: HttpClient

    override suspend fun sentData() {
        TODO("Not yet implemented")
    }

    override suspend fun closeConnection() {
        client.close()
    }

    override suspend fun openConnection() {
        client = HttpClient()
        val baseURL = deviceItemType.wifiAddress
        val uuid = deviceItemType.port.toString()
        val data = client.get<String>((baseURL + uuid))
    }

    override suspend fun read(buffer: ByteBuffer): Int {
        TODO("Not yet implemented")
    }


}