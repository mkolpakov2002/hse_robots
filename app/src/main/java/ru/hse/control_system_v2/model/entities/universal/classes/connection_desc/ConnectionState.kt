package ru.hse.control_system_v2.model.entities.universal.classes.connection_desc

data class ConnectionState(
    val status: ConnectionStatus,
    val lastConnectedAt: Long?,
    val lastDisconnectedAt: Long?
)

enum class ConnectionStatus {
    CONNECTED, DISCONNECTED, CONNECTING, DISCONNECTING, FAILED
}