package ru.hse.control_system_v2.model.entities.universal.scheme

/**
 * Schema for color_setting capability.
 *
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/color_setting.html
 */
enum class ColorSettingCapabilityInstance {
    BASE, RGB, HSV, TEMPERATURE_K, SCENE
}

enum class ColorScene {
    ALARM, ALICE, CANDLE, DINNER, FANTASY, GARLAND, JUNGLE, MOVIE, NEON, NIGHT, OCEAN, PARTY,
    READING, REST, ROMANCE, SIREN, SUNRISE, SUNSET
}

enum class CapabilityParameterColorModel {
    RGB, HSV
}

data class CapabilityParameterTemperatureK(
    val min: Int,
    val max: Int
)

data class CapabilityParameterColorScene(
    val scenes: List<Map<String, ColorScene>>
) {
    companion object {
        fun fromList(scenes: List<ColorScene>): CapabilityParameterColorScene {
            return CapabilityParameterColorScene(scenes.map { mapOf("id" to it) })
        }
    }
}

data class ColorSettingCapabilityParameters(
    val colorModel: CapabilityParameterColorModel? = null,
    val temperatureK: CapabilityParameterTemperatureK? = null,
    val colorScene: CapabilityParameterColorScene? = null
) {
    init {
        require(colorModel != null || temperatureK != null || colorScene != null) {
            "one of color_model, temperature_k or color_scene must have a value"
        }
    }
}

@kotlinx.serialization.Serializable
data class RGBInstanceActionState(
    val instance: ColorSettingCapabilityInstance = ColorSettingCapabilityInstance.RGB,
    val value: Int
)

@kotlinx.serialization.Serializable
data class TemperatureKInstanceActionState(
    val instance: ColorSettingCapabilityInstance = ColorSettingCapabilityInstance.TEMPERATURE_K,
    val value: Int
)

@kotlinx.serialization.Serializable
data class SceneInstanceActionState(
    val instance: ColorSettingCapabilityInstance = ColorSettingCapabilityInstance.SCENE,
    val value: ColorScene
)

/**
 * New value for an instance of color_setting capability.
 */
@kotlinx.serialization.Serializable
sealed class ColorSettingCapabilityInstanceActionState {
    abstract val instance: ColorSettingCapabilityInstance
}