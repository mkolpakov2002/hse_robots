package ru.hse.control_system_v2.model.entities.universal.scheme

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Единица измерения, используемая в возможности диапазона.
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/range.html
 */
@Serializable
enum class RangeCapabilityUnit {
    @SerialName("unit.percent") PERCENT,
    @SerialName("unit.temperature.celsius") TEMPERATURE_CELSIUS
}

/**
 * Экземпляр возможности диапазона.
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/range-instance.html
 */
@Serializable
enum class RangeCapabilityInstance: CapabilityInstance {
    @SerialName("brightness") BRIGHTNESS,
    @SerialName("channel") CHANNEL,
    @SerialName("humidity") HUMIDITY,
    @SerialName("open") OPEN,
    @SerialName("temperature") TEMPERATURE,
    @SerialName("volume") VOLUME
}

/**
 * Диапазон значений возможности диапазона.
 */
@Serializable
data class RangeCapabilityRange(
    val min: Float,
    val max: Float,
    val precision: Float
): APIModel {
    override fun toString() = "[$min, $max]"
}

/**
 * Параметры возможности диапазона.
 */
@Serializable
data class RangeCapabilityParameters(
    val instance: RangeCapabilityInstance,
    var unit: RangeCapabilityUnit? = null,
    @SerialName("random_access") val randomAccess: Boolean,
    val range: RangeCapabilityRange? = null
): CapabilityParameters {
    init {
        unit = when (instance) {
            RangeCapabilityInstance.BRIGHTNESS,
            RangeCapabilityInstance.HUMIDITY,
            RangeCapabilityInstance.OPEN -> RangeCapabilityUnit.PERCENT
            else -> {
                //RangeCapabilityInstance.TEMPERATURE
                RangeCapabilityUnit.TEMPERATURE_CELSIUS
            }
        }
    }
}

/**
 * Новое значение для возможности диапазона.
 */
@Serializable
data class RangeCapabilityInstanceActionState(
    val instance: RangeCapabilityInstance,
    val value: Float,
    val relative: Boolean = false
    ): APIModel