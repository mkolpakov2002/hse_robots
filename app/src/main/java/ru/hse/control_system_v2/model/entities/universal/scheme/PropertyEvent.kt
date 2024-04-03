package ru.hse.control_system_v2.model.entities.universal.scheme

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.slf4j.MDC.put

@Serializable
sealed class EventPropertyInstance {
    @Serializable
    @SerialName("vibration")
    data object Vibration : EventPropertyInstance()

    @Serializable
    @SerialName("open")
    data object Open : EventPropertyInstance()

    @Serializable
    @SerialName("button")
    data object Button : EventPropertyInstance()

    @Serializable
    @SerialName("motion")
    data object Motion : EventPropertyInstance()

    @Serializable
    @SerialName("smoke")
    data object Smoke : EventPropertyInstance()

    @Serializable
    @SerialName("gas")
    data object Gas : EventPropertyInstance()

    @Serializable
    @SerialName("battery_level")
    data object BatteryLevel : EventPropertyInstance()

    @Serializable
    @SerialName("food_level")
    data object FoodLevel : EventPropertyInstance()

    @Serializable
    @SerialName("water_level")
    data object WaterLevel : EventPropertyInstance()

    @Serializable
    @SerialName("water_leak")
    data object WaterLeak : EventPropertyInstance()
}

@Serializable
data class CustomEventPropertyInstance(
    @SerialName(value = "serialName") val serialName: String
) : EventPropertyInstance()

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
data class CustomEventInstanceEvent(val value: String) : EventInstanceEvent

@Serializable(with = EventPropertyParametersSerializer::class)
interface EventPropertyParameters<T : EventInstanceEvent> : PropertyParameters {
    val instance: EventPropertyInstance
    val events: List<Map<String, T>>
}

object EventPropertyParametersSerializer : KSerializer<EventPropertyParameters<*>> {
    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildSerialDescriptor("EventPropertyParameters", PolymorphicKind.OPEN) {
        element("instance", buildSerialDescriptor("EventPropertyInstance", PolymorphicKind.OPEN))
        element("events", buildSerialDescriptor("Events", StructureKind.LIST))
    }

    override fun deserialize(decoder: Decoder): EventPropertyParameters<*> {
        val jsonDecoder = decoder as? JsonDecoder ?: error("This class can be loaded only by Json")
        val json = jsonDecoder.decodeJsonElement().jsonObject

        val instance = when (val instanceJson = json["instance"]?.jsonPrimitive?.content) {
            "motion" -> EventPropertyInstance.Motion
            "smoke" -> EventPropertyInstance.Smoke
            "gas" -> EventPropertyInstance.Gas
            "battery_level" -> EventPropertyInstance.BatteryLevel
            "food_level" -> EventPropertyInstance.FoodLevel
            "water_level" -> EventPropertyInstance.WaterLevel
            "water_leak" -> EventPropertyInstance.WaterLeak
            "vibration" -> EventPropertyInstance.Vibration
            "open" -> EventPropertyInstance.Open
            "button" -> EventPropertyInstance.Button
            else -> CustomEventPropertyInstance(instanceJson ?: error("Instance field is required"))
        }

        val eventsJson = json["events"]?.jsonArray ?: error("Events field is required")
        val events = eventsJson.map { it ->
            it.jsonObject.mapValues {
                CustomEventInstanceEvent(it.value.jsonPrimitive.content)
            }
        }

        return when (instance) {
            is EventPropertyInstance.Motion -> MotionEventPropertyParameters(events = events.map { it.mapValues { MotionInstanceEvent.valueOf(it.value.value) } })
            is EventPropertyInstance.Smoke -> SmokeEventPropertyParameters(events = events.map { it.mapValues { SmokeInstanceEvent.valueOf(it.value.value) } })
            is EventPropertyInstance.Gas -> GasEventPropertyParameters(events = events.map { it.mapValues { GasInstanceEvent.valueOf(it.value.value) } })
            is EventPropertyInstance.BatteryLevel -> BatteryLevelEventPropertyParameters(events = events.map { it.mapValues { BatteryLevelInstanceEvent.valueOf(it.value.value) } })
            is EventPropertyInstance.FoodLevel -> FoodLevelEventPropertyParameters(events = events.map { it.mapValues { FoodLevelInstanceEvent.valueOf(it.value.value) } })
            is EventPropertyInstance.WaterLevel -> WaterLevelEventPropertyParameters(events = events.map { it.mapValues { WaterLevelInstanceEvent.valueOf(it.value.value) } })
            is EventPropertyInstance.WaterLeak -> WaterLeakEventPropertyParameters(events = events.map { it.mapValues { WaterLeakInstanceEvent.valueOf(it.value.value) } })
            is EventPropertyInstance.Vibration -> VibrationEventPropertyParameters(events = events.map { it.mapValues { VibrationInstanceEvent.valueOf(it.value.value) } })
            is EventPropertyInstance.Open -> OpenEventPropertyParameters(events = events.map { it.mapValues { OpenInstanceEvent.valueOf(it.value.value) } })
            is EventPropertyInstance.Button -> ButtonEventPropertyParameters(events = events.map { it.mapValues { ButtonInstanceEvent.valueOf(it.value.value) } })
            is CustomEventPropertyInstance -> CustomEventPropertyParameters(instance, events)
        }
    }

    override fun serialize(encoder: Encoder, value: EventPropertyParameters<*>) {
        val jsonEncoder = encoder as? JsonEncoder ?: error("This class can be saved only by Json")
        jsonEncoder.encodeJsonElement(buildJsonObject {
            put("instance", when (value.instance) {
                is EventPropertyInstance.Motion -> "motion"
                is EventPropertyInstance.Smoke -> "smoke"
                is EventPropertyInstance.Gas -> "gas"
                is EventPropertyInstance.BatteryLevel -> "battery_level"
                is EventPropertyInstance.FoodLevel -> "food_level"
                is EventPropertyInstance.WaterLevel -> "water_level"
                is EventPropertyInstance.WaterLeak -> "water_leak"
                is EventPropertyInstance.Vibration -> "vibration"
                is EventPropertyInstance.Open -> "open"
                is EventPropertyInstance.Button -> "button"
                is CustomEventPropertyInstance -> (value.instance as CustomEventPropertyInstance).serialName
            })
            put("events", buildJsonArray {
                value.events.forEach { event ->
                    add(buildJsonObject {
                        event.forEach { (key, eventValue) ->
                            put(key, (eventValue as CustomEventInstanceEvent).value)
                        }
                    })
                }
            })
        })
    }
}

@Serializable
data class CustomEventPropertyParameters(
    override val instance: CustomEventPropertyInstance,
    override val events: List<Map<String, CustomEventInstanceEvent>>
) : EventPropertyParameters<CustomEventInstanceEvent>

@Serializable
@SerialName("motion")
data class MotionEventPropertyParameters(
    override val instance: EventPropertyInstance = EventPropertyInstance.Motion,
    override val events: List<Map<String, MotionInstanceEvent>> = MotionInstanceEvent.entries.map { mapOf("value" to it) }
) : EventPropertyParameters<MotionInstanceEvent>

@Serializable
@SerialName("smoke")
data class SmokeEventPropertyParameters(
    override val instance: EventPropertyInstance = EventPropertyInstance.Smoke,
    override val events: List<Map<String, SmokeInstanceEvent>> = SmokeInstanceEvent.entries.map { mapOf("value" to it) }
) : EventPropertyParameters<SmokeInstanceEvent>

@Serializable
@SerialName("gas")
data class GasEventPropertyParameters(
    override val instance: EventPropertyInstance = EventPropertyInstance.Gas,
    override val events: List<Map<String, GasInstanceEvent>> = GasInstanceEvent.entries.map { mapOf("value" to it) }
) : EventPropertyParameters<GasInstanceEvent>

@Serializable
@SerialName("battery_level")
data class BatteryLevelEventPropertyParameters(
    override val instance: EventPropertyInstance = EventPropertyInstance.BatteryLevel,
    override val events: List<Map<String, BatteryLevelInstanceEvent>> = BatteryLevelInstanceEvent.entries.map { mapOf("value" to it) }
) : EventPropertyParameters<BatteryLevelInstanceEvent>

@Serializable
@SerialName("food_level")
data class FoodLevelEventPropertyParameters(
    override val instance: EventPropertyInstance = EventPropertyInstance.FoodLevel,
    override val events: List<Map<String, FoodLevelInstanceEvent>> = FoodLevelInstanceEvent.entries.map { mapOf("value" to it) }
) : EventPropertyParameters<FoodLevelInstanceEvent>

@Serializable
@SerialName("water_level")
data class WaterLevelEventPropertyParameters(
    override val instance: EventPropertyInstance = EventPropertyInstance.WaterLevel,
    override val events: List<Map<String, WaterLevelInstanceEvent>> = WaterLevelInstanceEvent.entries.map { mapOf("value" to it) }
) : EventPropertyParameters<WaterLevelInstanceEvent>

@Serializable
@SerialName("water_leak")
data class WaterLeakEventPropertyParameters(
    override val instance: EventPropertyInstance = EventPropertyInstance.WaterLeak,
    override val events: List<Map<String, WaterLeakInstanceEvent>> = WaterLeakInstanceEvent.entries.map { mapOf("value" to it) }
) : EventPropertyParameters<WaterLeakInstanceEvent>

@Serializable
@SerialName("vibration")
data class VibrationEventPropertyParameters(
    override val instance: EventPropertyInstance = EventPropertyInstance.Vibration,
    override val events: List<Map<String, VibrationInstanceEvent>> = VibrationInstanceEvent.entries.map { mapOf("value" to it) }
) : EventPropertyParameters<VibrationInstanceEvent>

@Serializable
@SerialName("open")
data class OpenEventPropertyParameters(
    override val instance: EventPropertyInstance = EventPropertyInstance.Open,
    override val events: List<Map<String, OpenInstanceEvent>> = OpenInstanceEvent.entries.map { mapOf("value" to it) }
) : EventPropertyParameters<OpenInstanceEvent>

@Serializable
@SerialName("button")
data class ButtonEventPropertyParameters(
    override val instance: EventPropertyInstance = EventPropertyInstance.Button,
    override val events: List<Map<String, ButtonInstanceEvent>> = ButtonInstanceEvent.entries.map { mapOf("value" to it) }
) : EventPropertyParameters<ButtonInstanceEvent>