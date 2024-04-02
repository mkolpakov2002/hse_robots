package ru.hse.control_system_v2.model.entities.universal.scheme

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Экземпляр возможности включения/выключения.
 */
@Serializable
enum class OnOffCapabilityInstance: CapabilityInstance {
    @SerialName("on") ON
}

/**
 * Параметры возможности включения/выключения.
 */
@Serializable
data class OnOffCapabilityParameters(
    val split: Boolean
): CapabilityParameters

/**
 * Новое значение для возможности включения/выключения.
 */
@Serializable
data class OnOffCapabilityInstanceActionState(
    val instance: OnOffCapabilityInstance,
    val value: Boolean
): APIModel