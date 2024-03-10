package ru.hse.control_system_v2.domain.connection.protocols.wifi

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.application.call
import io.ktor.server.netty.NettyApplicationEngine

import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import ru.hse.control_system_v2.domain.connection.ConnectionClass
import ru.hse.control_system_v2.model.entities.ConnectionDeviceModel
import java.net.Socket

/**
 * Класс серверного типа соединения
 */
class IpServerConnection(connectionDeviceModel: ConnectionDeviceModel) :
    ConnectionClass<Socket?>(connectionDeviceModel) {

    private lateinit var server: NettyApplicationEngine
    override suspend fun sendData(data: ByteArray) {
        TODO("Not yet implemented")
    }

    override suspend fun closeConnection() {
        TODO("Not yet implemented")
    }

    override suspend fun openConnection() {
        server = embeddedServer(Netty, 8080) {
//            install(ContentNegotiation) {
//                gson {}
//            }
            routing {
                get("/") {
                    call.respond(mapOf("message" to "Hello world"))
                }
            }
            //calling server.start(false) doesn't block the main thread
        }.start(wait = false)

    }

    override suspend fun read(buffer: ByteArray): Int {
        TODO("Not yet implemented")
    }
}