package ru.hse.control_system_v2.model.entities.universal.scheme

/**
 * Schema for toggle capability.
 *
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/toggle.html
 */
enum class ToggleCapabilityInstance {
    BACKLIGHT, CONTROLS_LOCKED, IONIZATION, KEEP_WARM, MUTE, OSCILLATION, PAUSE
}

data class ToggleCapabilityParameters(
    val instance: ToggleCapabilityInstance
)

@kotlinx.serialization.Serializable
data class ToggleCapabilityInstanceActionState(
    val instance: ToggleCapabilityInstance,
    val value: Boolean
)