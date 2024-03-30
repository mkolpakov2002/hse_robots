package ru.hse.control_system_v2.model.entities.universal.scheme

/**
 * Schema for mode capability.
 *
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/mode.html
 */
enum class ModeCapabilityInstance {
    CLEANUP_MODE, COFFEE_MODE, DISHWASHING, FAN_SPEED, HEAT, INPUT_SOURCE, PROGRAM, SWING,
    TEA_MODE, THERMOSTAT, WORK_SPEED
}

enum class ModeCapabilityMode {
    AUTO, ECO, SMART, TURBO, COOL, DRY, FAN_ONLY, HEAT, PREHEAT, HIGH, LOW, MEDIUM, MAX, MIN,
    FAST, SLOW, EXPRESS, NORMAL, QUIET, HORIZONTAL, STATIONARY, VERTICAL, ONE, TWO, THREE, FOUR,
    FIVE, SIX, SEVEN, EIGHT, NINE, TEN, AMERICANO, CAPPUCCINO, DOUBLE, ESPRESSO, DOUBLE_ESPRESSO,
    LATTE, BLACK_TEA, FLOWER_TEA, GREEN_TEA, HERBAL_TEA, OOLONG_TEA, PUERH_TEA, RED_TEA, WHITE_TEA,
    GLASS, INTENSIVE, PRE_RINSE, ASPIC, BABY_FOOD, BAKING, BREAD, BOILING, CEREALS, CHEESECAKE,
    DEEP_FRYER, DESSERT, FOWL, FRYING, MACARONI, MILK_PORRIDGE, MULTICOOKER, PASTA, PILAF, PIZZA,
    SAUCE, SLOW_COOK, SOUP, STEAM, STEWING, VACUUM, YOGURT
}

data class ModeCapabilityParameters(
    val instance: ModeCapabilityInstance,
    val modes: List<Map<String, ModeCapabilityMode>>
) {
    companion object {
        fun fromList(instance: ModeCapabilityInstance, modes: List<ModeCapabilityMode>): ModeCapabilityParameters {
            return ModeCapabilityParameters(instance, modes.map { mapOf("value" to it) })
        }
    }
}

@kotlinx.serialization.Serializable
data class ModeCapabilityInstanceActionState(
    val instance: ModeCapabilityInstance,
    val value: ModeCapabilityMode
)