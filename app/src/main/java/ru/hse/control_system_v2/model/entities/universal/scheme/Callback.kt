package ru.hse.control_system_v2.model.entities.universal.scheme

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class CallbackStatesRequestPayload(
    @SerialName("user_id") val userId: String,
    @SerialName("devices") val devices: List<DeviceState>
)

@Serializable
data class CallbackStatesRequest(
    @SerialName("ts") val ts: Long = Instant.now().epochSecond,
    @SerialName("payload") val payload: CallbackStatesRequestPayload
)

@Serializable
data class CallbackDiscoveryRequestPayload(
    @SerialName("user_id") val userId: String
)

@Serializable
data class CallbackDiscoveryRequest(
    @SerialName("ts") val ts: Long = Instant.now().epochSecond,
    @SerialName("payload") val payload: CallbackDiscoveryRequestPayload
)

@Serializable
enum class CallbackResponseStatus {
    @SerialName("ok") OK,
    @SerialName("error") ERROR
}

@Serializable
data class CallbackResponse(
    @SerialName("status") val status: CallbackResponseStatus,
    @SerialName("error_code") val errorCode: String? = null,
    @SerialName("error_message") val errorMessage: String? = null
)

typealias CallbackRequest = CallbackDiscoveryRequest