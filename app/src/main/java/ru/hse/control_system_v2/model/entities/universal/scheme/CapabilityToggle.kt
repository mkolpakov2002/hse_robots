package ru.hse.control_system_v2.model.entities.universal.scheme

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Экземпляр возможности переключения.
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/toggle-instance.html
 */
@Serializable
enum class ToggleCapabilityInstance: CapabilityInstance {
    @SerialName("backlight") BACKLIGHT,
    @SerialName("controls_locked") CONTROLS_LOCKED,
    @SerialName("ionization") IONIZATION,
    @SerialName("keep_warm") KEEP_WARM,
    @SerialName("mute") MUTE,
    @SerialName("oscillation") OSCILLATION,
    @SerialName("pause") PAUSE
}

/**
 * Параметры возможности переключения.
 */
@Serializable
@SerialName("toggle")
data class ToggleCapabilityParameters(
    val instance: ToggleCapabilityInstance
): CapabilityParameters()

/**
 * Новое значение для возможности переключения.
 */
@Serializable
data class ToggleCapabilityInstanceActionState(
    val instance: ToggleCapabilityInstance,
    val value: Boolean
)