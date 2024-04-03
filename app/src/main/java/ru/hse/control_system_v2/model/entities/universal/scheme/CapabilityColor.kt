package ru.hse.control_system_v2.model.entities.universal.scheme

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Экземпляр возможности настройки цвета.
 */
@Serializable
enum class ColorSettingCapabilityInstance: CapabilityInstance {
    @SerialName("base") BASE,
    @SerialName("rgb") RGB,
    @SerialName("hsv") HSV,
    @SerialName("temperature_k") TEMPERATURE_K,
    @SerialName("scene") SCENE
}

/**
 * Цветовая сцена.
 */
@Serializable
enum class ColorScene {
    @SerialName("alarm") ALARM,
    @SerialName("alice") ALICE,
    @SerialName("candle") CANDLE,
    @SerialName("dinner") DINNER,
    @SerialName("fantasy") FANTASY,
    @SerialName("garland") GARLAND,
    @SerialName("jungle") JUNGLE,
    @SerialName("movie") MOVIE,
    @SerialName("neon") NEON,
    @SerialName("night") NIGHT,
    @SerialName("ocean") OCEAN,
    @SerialName("party") PARTY,
    @SerialName("reading") READING,
    @SerialName("rest") REST,
    @SerialName("romance") ROMANCE,
    @SerialName("siren") SIREN,
    @SerialName("sunrise") SUNRISE,
    @SerialName("sunset") SUNSET
}

/**
 * Цветовая модель.
 */
@Serializable
enum class CapabilityParameterColorModel {
    @SerialName("rgb") RGB,
    @SerialName("hsv") HSV
}

/**
 * Диапазон цветовой температуры.
 */
@Serializable
data class CapabilityParameterTemperatureK(
    val min: Int,
    val max: Int
)

/**
 * Параметр экземпляра сцены.
 */
@Serializable
data class CapabilityParameterColorScene(
    val scenes: List<Map<String, ColorScene>>
): APIModel {
    companion object {
        fun fromList(scenes: List<ColorScene>) =
            CapabilityParameterColorScene(scenes.map { mapOf("id" to it) })
    }
}

/**
 * Параметры возможности настройки цвета.
 */
@Serializable
data class ColorSettingCapabilityParameters(
    @SerialName("color_model") val colorModel: CapabilityParameterColorModel? = null,
    @SerialName("temperature_k") val temperatureK: CapabilityParameterTemperatureK? = null,
    @SerialName("color_scene") val colorScene: CapabilityParameterColorScene? = null
): CapabilityParameters() {
    init {
        require(colorModel != null || temperatureK != null || colorScene != null) {
            "one of color_model, temperature_k or color_scene must have a value"
        }
    }
}

/**
 * Новое значение для экземпляра rgb.
 */
@Serializable
data class RGBInstanceActionState(
    val instance: ColorSettingCapabilityInstance = ColorSettingCapabilityInstance.RGB,
    val value: Int
): APIModel

/**
 * Новое значение для экземпляра temperature_k.
 */
@Serializable
data class TemperatureKInstanceActionState(
    val instance: ColorSettingCapabilityInstance = ColorSettingCapabilityInstance.TEMPERATURE_K,
    val value: Int
): APIModel

/**
 * Новое значение для экземпляра scene.
 */
@Serializable
data class SceneInstanceActionState(
    val instance: ColorSettingCapabilityInstance = ColorSettingCapabilityInstance.SCENE,
    val value: ColorScene
): APIModel

/**
 * Новое значение для экземпляра возможности настройки цвета.
 */

//TODO: проверить, что за discriminator в Python
@Serializable
sealed class ColorSettingCapabilityInstanceActionState {
    @Serializable
    @SerialName("rgb")
    data class RGB(val value: RGBInstanceActionState) : ColorSettingCapabilityInstanceActionState()

    @Serializable
    @SerialName("temperature_k")
    data class TemperatureK(val value: TemperatureKInstanceActionState) : ColorSettingCapabilityInstanceActionState()

    @Serializable
    @SerialName("scene")
    data class Scene(val value: SceneInstanceActionState) : ColorSettingCapabilityInstanceActionState()
}