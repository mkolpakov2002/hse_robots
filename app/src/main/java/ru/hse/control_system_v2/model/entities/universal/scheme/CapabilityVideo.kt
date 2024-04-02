package ru.hse.control_system_v2.model.entities.universal.scheme

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias StreamProtocols = List<String>

/**
 * Экземпляр возможности видеопотока.
 */
@Serializable
enum class VideoStreamCapabilityInstance: CapabilityInstance {
    @SerialName("get_stream") GET_STREAM
}

/**
 * Параметры возможности видеопотока.
 */
@Serializable
data class VideoStreamCapabilityParameters(
    val protocols: StreamProtocols
): CapabilityParameters

/**
 * Новое значение состояния для экземпляра get_stream.
 */
@Serializable
data class GetStreamInstanceActionStateValue(
    val protocols: StreamProtocols
): APIModel

/**
 * Новое значение для экземпляра get_stream.
 */
@Serializable
data class GetStreamInstanceActionState(
    val instance: VideoStreamCapabilityInstance,
    val value: GetStreamInstanceActionStateValue
): APIModel

/**
 * Новое значение после изменения состояния экземпляра get_stream.
 */
@Serializable
data class GetStreamInstanceActionResultValue(
    @SerialName("stream_url") val streamUrl: String,
    val protocol: String
): APIModel