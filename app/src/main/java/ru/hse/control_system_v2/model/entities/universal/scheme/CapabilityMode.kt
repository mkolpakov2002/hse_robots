package ru.hse.control_system_v2.model.entities.universal.scheme

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Экземпляр возможности режима.
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/mode-instance.html
 */
@Serializable
enum class ModeCapabilityInstance {
    @SerialName("cleanup_mode") CLEANUP_MODE,
    @SerialName("coffee_mode") COFFEE_MODE,
    @SerialName("dishwashing") DISHWASHING,
    @SerialName("fan_speed") FAN_SPEED,
    @SerialName("heat") HEAT,
    @SerialName("input_source") INPUT_SOURCE,
    @SerialName("program") PROGRAM,
    @SerialName("swing") SWING,
    @SerialName("tea_mode") TEA_MODE,
    @SerialName("thermostat") THERMOSTAT,
    @SerialName("work_speed") WORK_SPEED
}

/**
 * Значение режима возможности режима.
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/mode-instance-modes.html
 */
/**
 * Значение режима возможности режима.
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/mode-instance-modes.html
 */
@Serializable
enum class ModeCapabilityMode {
    @SerialName("auto") AUTO,
    @SerialName("eco") ECO,
    @SerialName("smart") SMART,
    @SerialName("turbo") TURBO,
    @SerialName("cool") COOL,
    @SerialName("dry") DRY,
    @SerialName("fan_only") FAN_ONLY,
    @SerialName("heat") HEAT,
    @SerialName("preheat") PREHEAT,
    @SerialName("high") HIGH,
    @SerialName("low") LOW,
    @SerialName("medium") MEDIUM,
    @SerialName("max") MAX,
    @SerialName("min") MIN,
    @SerialName("fast") FAST,
    @SerialName("slow") SLOW,
    @SerialName("express") EXPRESS,
    @SerialName("normal") NORMAL,
    @SerialName("quiet") QUIET,
    @SerialName("horizontal") HORIZONTAL,
    @SerialName("stationary") STATIONARY,
    @SerialName("vertical") VERTICAL,
    @SerialName("one") ONE,
    @SerialName("two") TWO,
    @SerialName("three") THREE,
    @SerialName("four") FOUR,
    @SerialName("five") FIVE,
    @SerialName("six") SIX,
    @SerialName("seven") SEVEN,
    @SerialName("eight") EIGHT,
    @SerialName("nine") NINE,
    @SerialName("ten") TEN,
    @SerialName("americano") AMERICANO,
    @SerialName("cappuccino") CAPPUCCINO,
    @SerialName("double") DOUBLE,
    @SerialName("espresso") ESPRESSO,
    @SerialName("double_espresso") DOUBLE_ESPRESSO,
    @SerialName("latte") LATTE,
    @SerialName("black_tea") BLACK_TEA,
    @SerialName("flower_tea") FLOWER_TEA,
    @SerialName("green_tea") GREEN_TEA,
    @SerialName("herbal_tea") HERBAL_TEA,
    @SerialName("oolong_tea") OOLONG_TEA,
    @SerialName("puerh_tea") PUERH_TEA,
    @SerialName("red_tea") RED_TEA,
    @SerialName("white_tea") WHITE_TEA,
    @SerialName("glass") GLASS,
    @SerialName("intensive") INTENSIVE,
    @SerialName("pre_rinse") PRE_RINSE,
    @SerialName("aspic") ASPIC,
    @SerialName("baby_food") BABY_FOOD,
    @SerialName("baking") BAKING,
    @SerialName("bread") BREAD,
    @SerialName("boiling") BOILING,
    @SerialName("cereals") CEREALS,
    @SerialName("cheesecake") CHEESECAKE,
    @SerialName("deep_fryer") DEEP_FRYER,
    @SerialName("dessert") DESSERT,
    @SerialName("fowl") FOWL,
    @SerialName("frying") FRYING,
    @SerialName("macaroni") MACARONI,
    @SerialName("milk_porridge") MILK_PORRIDGE,
    @SerialName("multicooker") MULTICOOKER,
    @SerialName("pasta") PASTA,
    @SerialName("pilaf") PILAF,
    @SerialName("pizza") PIZZA,
    @SerialName("sauce") SAUCE,
    @SerialName("slow_cook") SLOW_COOK,
    @SerialName("soup") SOUP,
    @SerialName("steam") STEAM,
    @SerialName("stewing") STEWING,
    @SerialName("vacuum") VACUUM,
    @SerialName("yogurt") YOGURT
}

/**
 * Параметры возможности режима.
 */
@Serializable
data class ModeCapabilityParameters(
    val instance: ModeCapabilityInstance,
    val modes: List<Map<String, ModeCapabilityMode>>
): CapabilityParameters() {
    companion object {
        fun fromList(instance: ModeCapabilityInstance, modes: List<ModeCapabilityMode>) =
            ModeCapabilityParameters(instance, modes.map { mapOf("value" to it) })
    }
}

/**
 * Новое значение для возможности режима.
 */
@Serializable
data class ModeCapabilityInstanceActionState(
    val instance: ModeCapabilityInstance,
    val value: ModeCapabilityMode
): APIModel