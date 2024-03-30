package ru.hse.control_system_v2.model.entities.universal.scheme

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull

@Serializable
enum class CapabilityType(val shortName: String) {
    @SerialName("devices.capabilities.on_off")
    ON_OFF("on_off"),
    @SerialName("devices.capabilities.color_setting")
    COLOR_SETTING("color_setting"),
    @SerialName("devices.capabilities.mode")
    MODE("mode"),
    @SerialName("devices.capabilities.range")
    RANGE("range"),
    @SerialName("devices.capabilities.toggle")
    TOGGLE("toggle"),
    @SerialName("devices.capabilities.video_stream")
    VIDEO_STREAM("video_stream");
}

@Serializable
sealed class CapabilityParameters

@Serializable
data class CapabilityDescription(
    val type: CapabilityType,
    val retrievable: Boolean,
    val reportable: Boolean,
    val parameters: CapabilityParameters? = null
)

@Serializable
sealed class CapabilityInstance

@Serializable
data class CapabilityInstanceStateValue(
    val instance: CapabilityInstance,
    val value: @Serializable(with = DynamicSerializer::class) Any
)

@Serializable
data class CapabilityInstanceState(
    val type: CapabilityType,
    val state: CapabilityInstanceStateValue
)

@Serializable
@SerialName("devices.capabilities.on_off")
data class OnOffCapabilityInstanceAction(
    val type: CapabilityType = CapabilityType.ON_OFF,
    val state: OnOffCapabilityInstanceActionState
)

@Serializable
@SerialName("devices.capabilities.color_setting")
data class ColorSettingCapabilityInstanceAction(
    val type: CapabilityType = CapabilityType.COLOR_SETTING,
    val state: ColorSettingCapabilityInstanceActionState
)

@Serializable
@SerialName("devices.capabilities.mode")
data class ModeCapabilityInstanceAction(
    val type: CapabilityType = CapabilityType.MODE,
    val state: ModeCapabilityInstanceActionState
)

@Serializable
@SerialName("devices.capabilities.range")
data class RangeCapabilityInstanceAction(
    val type: CapabilityType = CapabilityType.RANGE,
    val state: RangeCapabilityInstanceActionState
)

@Serializable
@SerialName("devices.capabilities.toggle")
data class ToggleCapabilityInstanceAction(
    val type: CapabilityType = CapabilityType.TOGGLE,
    val state: ToggleCapabilityInstanceActionState
)

@Serializable
@SerialName("devices.capabilities.video_stream")
data class VideoStreamCapabilityInstanceAction(
    val type: CapabilityType = CapabilityType.VIDEO_STREAM,
    val state: GetStreamInstanceActionState
)

@Serializable
sealed class CapabilityInstanceAction

@Serializable
sealed class CapabilityInstanceActionState

@Serializable
sealed class CapabilityInstanceActionResultValue

object DynamicSerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("DynamicSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Any) {
        val jsonEncoder = encoder as? JsonEncoder ?: throw IllegalArgumentException("DynamicSerializer can be used only with Json format.")
        jsonEncoder.encodeJsonElement(serializeToJsonElement(value))
    }

    override fun deserialize(decoder: Decoder): Any {
        val jsonDecoder = decoder as? JsonDecoder ?: throw IllegalArgumentException("DynamicSerializer can be used only with Json format.")
        val element = jsonDecoder.decodeJsonElement()
        return deserializeFromJsonElement(element)
    }

    private fun serializeToJsonElement(value: Any): JsonElement = when (value) {
        is Int -> JsonPrimitive(value)
        is String -> JsonPrimitive(value)
        is Boolean -> JsonPrimitive(value)
        is ColorScene -> ColorSceneSerializer.serializeToJsonElement(value)
        else -> throw IllegalArgumentException("Unsupported type for DynamicSerializer: ${value.javaClass}")
    }

    private fun deserializeFromJsonElement(element: JsonElement): Any = when {
        element is JsonPrimitive -> {
            when {
                element.isString -> element.content
                element.intOrNull != null -> element.int
                element.booleanOrNull != null -> element.boolean
                else -> throw IllegalArgumentException("Unsupported JsonPrimitive for DynamicSerializer: $element")
            }
        }
        else -> ColorSceneSerializer.deserializeFromJsonElement(element)
    }
}

object ColorSceneSerializer {
    fun serializeToJsonElement(value: ColorScene): JsonElement {
        return JsonPrimitive(value.name.lowercase())
    }

    fun deserializeFromJsonElement(element: JsonElement): ColorScene {
        if (element !is JsonPrimitive || !element.isString) {
            throw IllegalArgumentException("Invalid JSON element for ColorScene deserialization: $element")
        }
        return ColorScene.valueOf(element.content.uppercase())
    }
}