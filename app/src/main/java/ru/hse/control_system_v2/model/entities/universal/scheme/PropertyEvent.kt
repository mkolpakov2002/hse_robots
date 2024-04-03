package ru.hse.control_system_v2.model.entities.universal.scheme

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class EventPropertyInstance {
    @SerialName("vibration") VIBRATION,
    @SerialName("open") OPEN,
    @SerialName("button") BUTTON,
    @SerialName("motion") MOTION,
    @SerialName("smoke") SMOKE,
    @SerialName("gas") GAS,
    @SerialName("battery_level") BATTERY_LEVEL,
    @SerialName("food_level") FOOD_LEVEL,
    @SerialName("water_level") WATER_LEVEL,
    @SerialName("water_leak") WATER_LEAK,
    @SerialName("voice_activity") VOICE_ACTIVITY
}

@Serializable
sealed interface EventInstanceEvent

@Serializable
enum class VibrationInstanceEvent : EventInstanceEvent {
    @SerialName("tilt") TILT,
    @SerialName("fall") FALL,
    @SerialName("vibration") VIBRATION
}

@Serializable
enum class OpenInstanceEvent : EventInstanceEvent {
    @SerialName("opened") OPENED,
    @SerialName("closed") CLOSED
}

@Serializable
enum class ButtonInstanceEvent : EventInstanceEvent {
    @SerialName("click") CLICK,
    @SerialName("double_click") DOUBLE_CLICK,
    @SerialName("long_press") LONG_PRESS
}

@Serializable
enum class MotionInstanceEvent : EventInstanceEvent {
    @SerialName("detected") DETECTED,
    @SerialName("not_detected") NOT_DETECTED
}

@Serializable
enum class SmokeInstanceEvent : EventInstanceEvent {
    @SerialName("detected") DETECTED,
    @SerialName("not_detected") NOT_DETECTED,
    @SerialName("high") HIGH
}

@Serializable
enum class GasInstanceEvent : EventInstanceEvent {
    @SerialName("detected") DETECTED,
    @SerialName("not_detected") NOT_DETECTED,
    @SerialName("high") HIGH
}

@Serializable
enum class BatteryLevelInstanceEvent : EventInstanceEvent {
    @SerialName("low") LOW,
    @SerialName("normal") NORMAL,
    @SerialName("high") HIGH
}

@Serializable
enum class FoodLevelInstanceEvent : EventInstanceEvent {
    @SerialName("empty") EMPTY,
    @SerialName("low") LOW,
    @SerialName("normal") NORMAL
}

@Serializable
enum class WaterLevelInstanceEvent : EventInstanceEvent {
    @SerialName("empty") EMPTY,
    @SerialName("low") LOW,
    @SerialName("normal") NORMAL
}

@Serializable
enum class WaterLeakInstanceEvent : EventInstanceEvent {
    @SerialName("dry") DRY,
    @SerialName("leak") LEAK
}

@Serializable
enum class VoiceActivityInstanceEvent : EventInstanceEvent {
    @SerialName("speech_finished") SPEECH_FINISHED,
}

@Serializable
sealed interface EventPropertyParameters<T : EventInstanceEvent>: PropertyParameters {
    val instance: EventPropertyInstance
    val events: List<Map<String, T>>
}

@Serializable
@SerialName("motion")
data class MotionEventPropertyParameters(
    override val instance: EventPropertyInstance = EventPropertyInstance.MOTION,
    override val events: List<Map<String, MotionInstanceEvent>> = MotionInstanceEvent.entries.map { mapOf("value" to it) }
) : EventPropertyParameters<MotionInstanceEvent>

@Serializable
@SerialName("smoke")
data class SmokeEventPropertyParameters(
    override val instance: EventPropertyInstance = EventPropertyInstance.SMOKE,
    override val events: List<Map<String, SmokeInstanceEvent>> = SmokeInstanceEvent.entries.map { mapOf("value" to it) }
) : EventPropertyParameters<SmokeInstanceEvent>

@Serializable
@SerialName("gas")
data class GasEventPropertyParameters(
    override val instance: EventPropertyInstance = EventPropertyInstance.GAS,
    override val events: List<Map<String, GasInstanceEvent>> = GasInstanceEvent.entries.map { mapOf("value" to it) }
) : EventPropertyParameters<GasInstanceEvent>

@Serializable
@SerialName("battery_level")
data class BatteryLevelEventPropertyParameters(
    override val instance: EventPropertyInstance = EventPropertyInstance.BATTERY_LEVEL,
    override val events: List<Map<String, BatteryLevelInstanceEvent>> = BatteryLevelInstanceEvent.entries.map { mapOf("value" to it) }
) : EventPropertyParameters<BatteryLevelInstanceEvent>

@Serializable
@SerialName("food_level")
data class FoodLevelEventPropertyParameters(
    override val instance: EventPropertyInstance = EventPropertyInstance.FOOD_LEVEL,
    override val events: List<Map<String, FoodLevelInstanceEvent>> = FoodLevelInstanceEvent.entries.map { mapOf("value" to it) }
) : EventPropertyParameters<FoodLevelInstanceEvent>

@Serializable
@SerialName("water_level")
data class WaterLevelEventPropertyParameters(
    override val instance: EventPropertyInstance = EventPropertyInstance.WATER_LEVEL,
    override val events: List<Map<String, WaterLevelInstanceEvent>> = WaterLevelInstanceEvent.entries.map { mapOf("value" to it) }
) : EventPropertyParameters<WaterLevelInstanceEvent>

@Serializable
@SerialName("water_leak")
data class WaterLeakEventPropertyParameters(
    override val instance: EventPropertyInstance = EventPropertyInstance.WATER_LEAK,
    override val events: List<Map<String, WaterLeakInstanceEvent>> = WaterLeakInstanceEvent.entries.map { mapOf("value" to it) }
) : EventPropertyParameters<WaterLeakInstanceEvent>

@Serializable
@SerialName("vibration")
data class VibrationEventPropertyParameters(
    override val instance: EventPropertyInstance = EventPropertyInstance.VIBRATION,
    override val events: List<Map<String, VibrationInstanceEvent>> = VibrationInstanceEvent.entries.map { mapOf("value" to it) }
) : EventPropertyParameters<VibrationInstanceEvent>

@Serializable
@SerialName("open")
data class OpenEventPropertyParameters(
    override val instance: EventPropertyInstance = EventPropertyInstance.OPEN,
    override val events: List<Map<String, OpenInstanceEvent>> = OpenInstanceEvent.entries.map { mapOf("value" to it) }
) : EventPropertyParameters<OpenInstanceEvent>

@Serializable
@SerialName("button")
data class ButtonEventPropertyParameters(
    override val instance: EventPropertyInstance = EventPropertyInstance.BUTTON,
    override val events: List<Map<String, ButtonInstanceEvent>> = ButtonInstanceEvent.entries.map { mapOf("value" to it) }
) : EventPropertyParameters<ButtonInstanceEvent>