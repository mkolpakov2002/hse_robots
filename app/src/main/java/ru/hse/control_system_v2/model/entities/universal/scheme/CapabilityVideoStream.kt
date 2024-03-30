package ru.hse.control_system_v2.model.entities.universal.scheme

/**
 * Schema for video_stream capability.
 *
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/video_stream.html
 */
typealias StreamProtocols = List<String>

enum class VideoStreamCapabilityInstance {
    GET_STREAM
}

data class VideoStreamCapabilityParameters(
    val protocols: StreamProtocols
)

@kotlinx.serialization.Serializable
data class GetStreamInstanceActionStateValue(
    val protocols: StreamProtocols
)

@kotlinx.serialization.Serializable
data class GetStreamInstanceActionState(
    val instance: VideoStreamCapabilityInstance = VideoStreamCapabilityInstance.GET_STREAM,
    val value: GetStreamInstanceActionStateValue
)

data class GetStreamInstanceActionResultValue(
    val streamUrl: String,
    val protocol: String
)