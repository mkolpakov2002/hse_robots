package ru.hse.control_system_v2.model.entities.universal.scheme

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class CapabilityType {
    @SerialName("devices.capabilities.on_off")
    ON_OFF,
    @SerialName("devices.capabilities.color_setting")
    COLOR_SETTING,
    @SerialName("devices.capabilities.mode")
    MODE,
    @SerialName("devices.capabilities.range")
    RANGE,
    @SerialName("devices.capabilities.toggle")
    TOGGLE,
    @SerialName("devices.capabilities.video_stream")
    VIDEO_STREAM
}

@Serializable
sealed interface CapabilityParameters : APIModel

@Serializable
sealed interface CapabilityInstance : APIModel

@Serializable
data class CapabilityDescription(
    val type: CapabilityType,
    val retrievable: Boolean,
    val reportable: Boolean,
    val parameters: CapabilityParameters? = null
) : APIModel


//TODO: проверить список Capability классов
@Serializable
sealed class CapabilityStateValue

@Serializable
data class OnOffCapabilityStateValue(val value: Boolean) : CapabilityStateValue()

@Serializable
data class ColorSettingCapabilityStateValue(val value: ColorSettingCapabilityInstanceActionState) : CapabilityStateValue()

@Serializable
data class RGBCapabilityStateValue(val value: RGBInstanceActionState) : CapabilityStateValue()

@Serializable
data class TemperatureKCapabilityStateValue(val value: TemperatureKInstanceActionState) : CapabilityStateValue()

@Serializable
data class SceneCapabilityStateValue(val value: SceneInstanceActionState) : CapabilityStateValue()

@Serializable
data class ModeCapabilityStateValue(val value: ModeCapabilityInstanceActionState) : CapabilityStateValue()

@Serializable
data class RangeCapabilityStateValue(val value: RangeCapabilityInstanceActionState) : CapabilityStateValue()

@Serializable
data class ToggleCapabilityStateValue(val value: ToggleCapabilityInstanceActionState) : CapabilityStateValue()

@Serializable
data class VideoStreamCapabilityStateValue(val value: GetStreamInstanceActionState) : CapabilityStateValue()

@Serializable
data class CapabilityInstanceStateValue(
    val instance: CapabilityInstance,
    val value: CapabilityStateValue
) : APIModel

@Serializable
data class CapabilityInstanceState(
    val type: CapabilityType,
    val state: CapabilityInstanceStateValue
) : APIModel

@Serializable
sealed interface CapabilityInstanceAction : APIModel {
    val type: CapabilityType
}

@Serializable
@SerialName("devices.capabilities.on_off")
data class OnOffCapabilityInstanceAction(
    override val type: CapabilityType = CapabilityType.ON_OFF,
    val state: OnOffCapabilityInstanceActionState
) : CapabilityInstanceAction

@Serializable
@SerialName("devices.capabilities.color_setting")
data class ColorSettingCapabilityInstanceAction(
    override val type: CapabilityType = CapabilityType.COLOR_SETTING,
    val state: ColorSettingCapabilityInstanceActionState
) : CapabilityInstanceAction

@Serializable
@SerialName("devices.capabilities.mode")
data class ModeCapabilityInstanceAction(
    override val type: CapabilityType = CapabilityType.MODE,
    val state: ModeCapabilityInstanceActionState
) : CapabilityInstanceAction

@Serializable
@SerialName("devices.capabilities.range")
data class RangeCapabilityInstanceAction(
    override val type: CapabilityType = CapabilityType.RANGE,
    val state: RangeCapabilityInstanceActionState
) : CapabilityInstanceAction

@Serializable
@SerialName("devices.capabilities.toggle")
data class ToggleCapabilityInstanceAction(
    override val type: CapabilityType = CapabilityType.TOGGLE,
    val state: ToggleCapabilityInstanceActionState
) : CapabilityInstanceAction

@Serializable
@SerialName("devices.capabilities.video_stream")
data class VideoStreamCapabilityInstanceAction(
    override val type: CapabilityType = CapabilityType.VIDEO_STREAM,
    val state: GetStreamInstanceActionState
) : CapabilityInstanceAction

@Serializable
sealed class CapabilityInstanceActionState : APIModel

@Serializable
sealed class CapabilityInstanceActionResultValue : APIModel