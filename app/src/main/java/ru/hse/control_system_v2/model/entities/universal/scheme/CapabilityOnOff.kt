package ru.hse.control_system_v2.model.entities.universal.scheme

/**
 * Schema for on_off capability.
 *
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/on_off.html
 */
enum class OnOffCapabilityInstance {
    ON
}

data class OnOffCapabilityParameters(
    val split: Boolean
)

@kotlinx.serialization.Serializable
data class OnOffCapabilityInstanceActionState(
    val instance: OnOffCapabilityInstance,
    val value: Boolean
)