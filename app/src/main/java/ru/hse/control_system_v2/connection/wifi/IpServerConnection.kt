package ru.hse.control_system_v2.connection.wifi

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.application.call

import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import ru.hse.control_system_v2.connection.ConnectionClass
import ru.hse.control_system_v2.data.classes.device.model.DeviceModel
import java.net.Socket
import java.nio.ByteBuffer

class IpServerConnection(deviceItemType: DeviceModel, connectionName: String?) :
    ConnectionClass<Socket?>(deviceItemType, connectionName) {
    override suspend fun sentData() {
        TODO("Not yet implemented")
    }

    override suspend fun closeConnection() {
        TODO("Not yet implemented")
    }

    override suspend fun openConnection() {
        embeddedServer(Netty, 8080) {
//            install(ContentNegotiation) {
//                gson {}
//            }
            routing {
                get("/") {
                    call.respond(mapOf("message" to "Hello world"))
                }
            }
        }.start(wait = true)
    }

    override suspend fun read(buffer: ByteBuffer): Int {
        TODO("Not yet implemented")
    }
}