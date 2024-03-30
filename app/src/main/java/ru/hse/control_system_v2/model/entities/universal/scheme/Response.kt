package ru.hse.control_system_v2.model.entities.universal.scheme

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ResponseCode {
    @SerialName("DOOR_OPEN") DOOR_OPEN,
    @SerialName("LID_OPEN") LID_OPEN,
    @SerialName("REMOTE_CONTROL_DISABLED") REMOTE_CONTROL_DISABLED,
    @SerialName("NOT_ENOUGH_WATER") NOT_ENOUGH_WATER,
    @SerialName("LOW_CHARGE_LEVEL") LOW_CHARGE_LEVEL,
    @SerialName("CONTAINER_FULL") CONTAINER_FULL,
    @SerialName("CONTAINER_EMPTY") CONTAINER_EMPTY,
    @SerialName("DRIP_TRAY_FULL") DRIP_TRAY_FULL,
    @SerialName("DEVICE_STUCK") DEVICE_STUCK,
    @SerialName("DEVICE_OFF") DEVICE_OFF,
    @SerialName("FIRMWARE_OUT_OF_DATE") FIRMWARE_OUT_OF_DATE,
    @SerialName("NOT_ENOUGH_DETERGENT") NOT_ENOUGH_DETERGENT,
    @SerialName("HUMAN_INVOLVEMENT_NEEDED") HUMAN_INVOLVEMENT_NEEDED,
    @SerialName("DEVICE_UNREACHABLE") DEVICE_UNREACHABLE,
    @SerialName("DEVICE_BUSY") DEVICE_BUSY,
    @SerialName("INTERNAL_ERROR") INTERNAL_ERROR,
    @SerialName("INVALID_ACTION") INVALID_ACTION,
    @SerialName("INVALID_VALUE") INVALID_VALUE,
    @SerialName("NOT_SUPPORTED_IN_CURRENT_MODE") NOT_SUPPORTED_IN_CURRENT_MODE,
    @SerialName("ACCOUNT_LINKING_ERROR") ACCOUNT_LINKING_ERROR,
    @SerialName("DEVICE_NOT_FOUND") DEVICE_NOT_FOUND
}

@Serializable
abstract class ResponsePayload : APIModel()

@Serializable
data class Error(
    @SerialName("error_code") val errorCode: ResponseCode,
    @SerialName("error_message") val errorMessage: String? = null
) : ResponsePayload()

@Serializable
data class Response(
    @SerialName("request_id") val requestId: String? = null,
    @SerialName("payload") val payload: ResponsePayload? = null
)