package ru.hse.control_system_v2.connection

import android.os.Build
import androidx.annotation.RequiresApi
import io.ktor.client.*
import io.ktor.client.request.*
import ru.hse.control_system_v2.data.DeviceItemType
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.CompletionHandler
import kotlin.coroutines.Continuation

open class IpConnection(deviceItemType: DeviceItemType, connectionName: String?) :
    ConnectionClass<Socket?>(deviceItemType, connectionName) {

    private lateinit var client: HttpClient

    override suspend fun sentData() {
        TODO("Not yet implemented")
    }

    override suspend fun closeConnection() {
        client.close()
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun openConnection() {
        client = HttpClient()
        val baseURL = deviceItemType.devIp
        val uuid = deviceItemType.devPort.toString()
        val data = client.get<String>((baseURL + uuid))
    }

    override suspend fun read(buffer: ByteBuffer): Int {
        TODO("Not yet implemented")
    }


}