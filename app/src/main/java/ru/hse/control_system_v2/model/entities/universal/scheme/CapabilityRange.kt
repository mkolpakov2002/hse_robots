package ru.hse.control_system_v2.model.entities.universal.scheme

/**
 * Schema for range capability.
 *
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/range.html
 */
enum class RangeCapabilityUnit {
    PERCENT, TEMPERATURE_CELSIUS
}

enum class RangeCapabilityInstance {
    BRIGHTNESS, CHANNEL, HUMIDITY, OPEN, TEMPERATURE, VOLUME
}

data class RangeCapabilityRange(
    val min: Float,
    val max: Float,
    val precision: Float
) {
    override fun toString(): String {
        return "[$min, $max]"
    }
}

data class RangeCapabilityParameters(
    val instance: RangeCapabilityInstance,
    var unit: RangeCapabilityUnit? = null,
    val randomAccess: Boolean,
    val range: RangeCapabilityRange? = null
) {
    init {
        when (instance) {
            RangeCapabilityInstance.BRIGHTNESS -> unit = RangeCapabilityUnit.PERCENT
            RangeCapabilityInstance.HUMIDITY -> unit = RangeCapabilityUnit.PERCENT
            RangeCapabilityInstance.OPEN -> unit = RangeCapabilityUnit.PERCENT
            RangeCapabilityInstance.TEMPERATURE -> unit = RangeCapabilityUnit.TEMPERATURE_CELSIUS
            else -> {}
        }
    }
}

@kotlinx.serialization.Serializable
data class RangeCapabilityInstanceActionState(
    val instance: RangeCapabilityInstance,
    val value: Float,
    val relative: Boolean = false
)