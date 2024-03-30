package ru.hse.control_system_v2.model.entities.universal.scheme

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Schema for event property.
 *
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/event.html
 */

enum class EventPropertyInstance {
    @SerialName("vibration")
    VIBRATION,
    @SerialName("open")
    OPEN,
    @SerialName("button")
    BUTTON,
    @SerialName("motion")
    MOTION,
    @SerialName("smoke")
    SMOKE,
    @SerialName("gas")
    GAS,
    @SerialName("battery_level")
    BATTERY_LEVEL,
    @SerialName("food_level")
    FOOD_LEVEL,
    @SerialName("water_level")
    WATER_LEVEL,
    @SerialName("water_leak")
    WATER_LEAK
}

enum class VibrationInstanceEvent {
    @SerialName("tilt")
    TILT,
    @SerialName("fall")
    FALL,
    @SerialName("vibration")
    VIBRATION
}

enum class OpenInstanceEvent {
    @SerialName("opened")
    OPENED,
    @SerialName("closed")
    CLOSED
}

enum class ButtonInstanceEvent {
    @SerialName("click")
    CLICK,
    @SerialName("double_click")
    DOUBLE_CLICK,
    @SerialName("long_press")
    LONG_PRESS
}

enum class MotionInstanceEvent {
    @SerialName("detected")
    DETECTED,
    @SerialName("not_detected")
    NOT_DETECTED
}

enum class SmokeInstanceEvent {
    @SerialName("detected")
    DETECTED,
    @SerialName("not_detected")
    NOT_DETECTED,
    @SerialName("high")
    HIGH
}

enum class GasInstanceEvent {
    @SerialName("detected")
    DETECTED,
    @SerialName("not_detected")
    NOT_DETECTED,
    @SerialName("high")
    HIGH
}

enum class BatteryLevelInstanceEvent {
    @SerialName("low")
    LOW,
    @SerialName("normal")
    NORMAL,
    @SerialName("high")
    HIGH
}

enum class FoodLevelInstanceEvent {
    @SerialName("empty")
    EMPTY,
    @SerialName("low")
    LOW,
    @SerialName("normal")
    NORMAL
}

enum class WaterLevelInstanceEvent {
    @SerialName("empty")
    EMPTY,
    @SerialName("low")
    LOW,
    @SerialName("normal")
    NORMAL
}

enum class WaterLeakInstanceEvent {
    @SerialName("dry")
    DRY,
    @SerialName("leak")
    LEAK
}

@Serializable
data class EventPropertyParameters(
    @SerialName("instance") val instance: String,
    @SerialName("events") val events: List<EventValue>
)

@Serializable
data class VibrationEventPropertyParameters(
    @SerialName("instance") val instance: String = EventPropertyInstance.VIBRATION.toString(),
    @SerialName("events") val events: List<EventValue> =
        VibrationInstanceEvent.entries.map { EventValue(it.toString()) }
)

@Serializable
data class OpenEventPropertyParameters(
    @SerialName("instance") val instance: String = EventPropertyInstance.OPEN.toString(),
    @SerialName("events") val events: List<EventValue> =
        OpenInstanceEvent.entries.map { EventValue(it.toString()) }
)

@Serializable
data class ButtonEventPropertyParameters(
    @SerialName("instance") val instance: String = EventPropertyInstance.BUTTON.toString(),
    @SerialName("events") val events: List<EventValue> =
        ButtonInstanceEvent.entries.map { EventValue(it.toString()) }
)

@Serializable
data class MotionEventPropertyParameters(
    @SerialName("instance") val instance: String = EventPropertyInstance.MOTION.toString(),
    @SerialName("events") val events: List<EventValue> =
        MotionInstanceEvent.entries.map { EventValue(it.toString()) }
)

@Serializable
data class SmokeEventPropertyParameters(
    @SerialName("instance") val instance: String = EventPropertyInstance.SMOKE.toString(),
    @SerialName("events") val events: List<EventValue> =
        SmokeInstanceEvent.entries.map { EventValue(it.toString()) }
)

@Serializable
data class GasEventPropertyParameters(
    @SerialName("instance") val instance: String = EventPropertyInstance.GAS.toString(),
    @SerialName("events") val events: List<EventValue> =
        GasInstanceEvent.entries.map { EventValue(it.toString()) }
)

@Serializable
data class BatteryLevelEventPropertyParameters(
    @SerialName("instance") val instance: String = EventPropertyInstance.BATTERY_LEVEL.toString(),
    @SerialName("events") val events: List<EventValue> =
        BatteryLevelInstanceEvent.entries.map { EventValue(it.toString()) }
)

@Serializable
data class FoodLevelEventPropertyParameters(
    @SerialName("instance") val instance: String = EventPropertyInstance.FOOD_LEVEL.toString(),
    @SerialName("events") val events: List<EventValue> =
        FoodLevelInstanceEvent.entries.map { EventValue(it.toString()) }
)

@Serializable
data class WaterLevelEventPropertyParameters(
    @SerialName("instance") val instance: String = EventPropertyInstance.WATER_LEVEL.toString(),
    @SerialName("events") val events: List<EventValue> =
        WaterLevelInstanceEvent.entries.map { EventValue(it.toString()) }
)

@Serializable
data class WaterLeakEventPropertyParameters(
    @SerialName("instance") val instance: String = EventPropertyInstance.WATER_LEAK.toString(),
    @SerialName("events") val events: List<EventValue> =
        WaterLeakInstanceEvent.entries.map { EventValue(it.toString()) }
)