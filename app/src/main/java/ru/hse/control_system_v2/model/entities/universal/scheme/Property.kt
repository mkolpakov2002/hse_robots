package ru.hse.control_system_v2.model.entities.universal.scheme

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor

/**
 * Schema for device property.
 *
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/properties-types.html
 */

@Serializable
enum class PropertyType(private val value: String) {
    @SerialName("devices.properties.float")
    FLOAT("devices.properties.float"),
    @SerialName("devices.properties.event")
    EVENT("devices.properties.event");

    val short: String
        get() = value.replace("devices.properties.", "")
}

@Serializable
sealed class PropertyDescription {
    abstract val type: PropertyType
    abstract val retrievable: Boolean
    abstract val reportable: Boolean
}

@Serializable
@SerialName("devices.properties.float")
data class FloatPropertyDescription(
    override val type: PropertyType = PropertyType.FLOAT,
    @SerialName("retrievable") override val retrievable: Boolean,
    @SerialName("reportable") override val reportable: Boolean,
    @SerialName("parameters") val parameters: FloatPropertyParameters
) : PropertyDescription()

@Serializable
@SerialName("devices.properties.event")
data class EventPropertyDescription(
    override val type: PropertyType = PropertyType.EVENT,
    @SerialName("retrievable") override val retrievable: Boolean,
    @SerialName("reportable") override val reportable: Boolean,
    @SerialName("parameters") val parameters: EventPropertyParameters
) : PropertyDescription()

@Serializable
sealed class PropertyInstance

@Serializable
data class EventValue(
    @SerialName("value") val value: String
)

@Serializable
data class PropertyInstanceStateValue(
    @SerialName("instance") val instance: PropertyInstance,
    @SerialName("value") val value: String
)

@Serializable
data class PropertyInstanceState(
    @SerialName("type") val type: PropertyType,
    @SerialName("state") val state: PropertyInstanceStateValue
)