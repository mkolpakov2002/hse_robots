package ru.hse.control_system_v2.connection

import ru.hse.control_system_v2.data.DeviceItemType
import xyz.urbanmatrix.mavlink.api.MavBitmaskValue
import xyz.urbanmatrix.mavlink.api.MavEnumValue
import xyz.urbanmatrix.mavlink.api.MavMessage
import xyz.urbanmatrix.mavlink.connection.tcp.TcpClientMavConnection
import xyz.urbanmatrix.mavlink.definitions.common.CommonDialect
import xyz.urbanmatrix.mavlink.definitions.minimal.Heartbeat
import xyz.urbanmatrix.mavlink.definitions.minimal.MavAutopilot
import xyz.urbanmatrix.mavlink.definitions.minimal.MavModeFlag
import xyz.urbanmatrix.mavlink.definitions.minimal.MavType
import xyz.urbanmatrix.mavlink.wrap
import java.net.Socket
import java.nio.ByteBuffer
import kotlin.properties.Delegates

open class MavlinkConnection(deviceItemType: DeviceItemType, connectionName: String?, var connectionVersion: String) :
    ConnectionClass<Socket?>(deviceItemType, connectionName) {

    private lateinit var connection: TcpClientMavConnection

    private var linkId by Delegates.notNull<Int>()

    private lateinit var heartbeat: Heartbeat

    private lateinit var secretKey: ByteArray

    private var timestamp by Delegates.notNull<Long>()

    init {

        val connectionVersionNumber: Int = when (connectionVersion) {
            "1" -> 1
            else -> {
                secretKey = createSecretKey()
                2
            }
        }

        val builder = Heartbeat.Builder()
        builder.type = MavType.FIXED_WING.wrap()
        builder.autopilot = MavAutopilot.PX4.wrap()
        builder.mavlinkVersion = connectionVersionNumber
        heartbeat = builder.build()
    }
    override suspend fun sentData() {
        when (connectionVersion) {
            "1" -> sentDataV1()
            "2" -> sentDataUnsignedV2()
            else -> sentDataSignedV2()
        }
    }

    // MAVLink v1 packet
    private fun sentDataV1(){
        val completable = connection.sendV1(
            systemId = 250,
            componentId = 1,
            payload = heartbeat
        )
    }

    // Unsigned MAVLink v2 packet
    private fun sentDataUnsignedV2(){
        val completable = connection.sendUnsignedV2(
            systemId = 250,
            componentId = 1,
            payload = heartbeat
        )
    }

    // Signed MAVLink v2 packet
    private fun sentDataSignedV2(){
        val completable = connection.sendSignedV2(
            systemId = 250,
            componentId = 1,
            payload = heartbeat,
            linkId = linkId,       // Integer link ID
            timestamp = timestamp, // Long microseconds
            secretKey = secretKey  // ByteArray passcode
        )
    }

    override suspend fun closeConnection() {
        // Non-blocking
        connection.close()
    }

    override suspend fun openConnection() {
        connection = TcpClientMavConnection(deviceItemType.devIp,
            deviceItemType.devPort, CommonDialect)
        // Non-blocking
        connection.connect()
    }

    override suspend fun read(buffer: ByteBuffer): Int {
        TODO("Not yet implemented")
    }

    private fun createSecretKey() : ByteArray {
        //TODO
        return byteArrayOf(0x2E, 0x38)
    }

}