package ru.hse.control_system_v2.domain.connection.protocols.wifi

import ru.hse.control_system_v2.domain.connection.ConnectionClass

import com.divpundir.mavlink.connection.tcp.TcpClientMavConnection
import ru.hse.control_system_v2.model.entities.ConnectionDeviceModel
import java.net.Socket
import kotlin.properties.Delegates

open class MavlinkConnection(connectionDeviceModel: ConnectionDeviceModel,
                             var connectionVersion: String) :
    ConnectionClass<Socket?>(connectionDeviceModel) {

    // Переменная для хранения объекта TcpClientMavConnection
    private lateinit var connection: TcpClientMavConnection

    // Переменная для хранения идентификатора соединения
    private var linkId by Delegates.notNull<Int>()

    // Переменная для хранения объекта Heartbeat
//    private lateinit var heartbeat: Heartbeat

    // Переменная для хранения секретного ключа для подписи сообщений
    private lateinit var secretKey: ByteArray

    // Переменная для хранения временной метки для подписи сообщений
    private var timestamp by Delegates.notNull<Long>()

    // Инициализация переменных heartbeat и secretKey в зависимости от версии соединения
    init {
        val connectionVersionNumber: Int = when (connectionVersion) {
            "1" -> 1
            else -> {
                // Для версии 2 соединения генерируется секретный ключ
                secretKey = createSecretKey()
                2
            }
        }

//        val builder = Heartbeat.Builder()
//        builder.type = MavType.FIXED_WING.wrap()
//        builder.autopilot = MavAutopilot.PX4.wrap()
//        builder.mavlinkVersion = connectionVersionNumber
//        heartbeat = builder.build()
    }

    // Метод для отправки данных по MAVLink-соединению в зависимости от версии соединения
    override suspend fun sendData(data: ByteArray) {
        when (connectionVersion) {
            "1" -> sentDataV1()
            "2" -> sentDataUnsignedV2()
            else -> sentDataSignedV2()
        }
    }

    // Метод для отправки данных по MAVLink-соединению версии 1
    private fun sentDataV1(){
//        val completable = connection.sendV1(
//            systemId = 6u,
//            componentId = 1u,
//            payload = heartbeat
//        )
    }

    // Метод для отправки данных по MAVLink-соединению версии 2 без подписи
    private fun sentDataUnsignedV2(){
//        val completable = connection.sendUnsignedV2(
//            systemId = 6u,
//            componentId = 1u,
//            payload = heartbeat
//        )
    }

    private fun sentDataSignedV2(){
//        val completable = connection.sendSignedV2(
//            systemId = 6u,
//            componentId = 1u,
//            payload = heartbeat,
//            linkId = linkId,
//            timestamp = timestamp,
//            secretKey = secretKey
//        )
    }

    override suspend fun closeConnection() {
        connection.close()
    }

    override suspend fun openConnection() {
//        val dialect: MavDialect
//        connection = TcpClientMavConnection(
//            deviceItemType.wifiAddress,
//            deviceItemType.port, dialect)
//        connection.connect()
    }

    override suspend fun read(buffer: ByteArray): Int {
        TODO("Not yet implemented")
    }

    private fun createSecretKey() : ByteArray {
        //TODO("Not yet implemented")
        return byteArrayOf(0x2E, 0x38)
    }

}