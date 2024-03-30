package ru.hse.control_system_v2.model.entities.universal.scheme

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Schema for float property.
 *
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/float.html
 */

@Serializable
enum class FloatPropertyInstance {
    @SerialName("amperage")
    AMPERAGE,
    @SerialName("battery_level")
    BATTERY_LEVEL,
    @SerialName("co2_level")
    CO2_LEVEL,
    @SerialName("electricity_meter")
    ELECTRICITY_METER,
    @SerialName("food_level")
    FOOD_LEVEL,
    @SerialName("gas_meter")
    GAS_METER,
    @SerialName("heat_meter")
    HEAT_METER,
    @SerialName("humidity")
    HUMIDITY,
    @SerialName("illumination")
    ILLUMINATION,
    @SerialName("meter")
    METER,
    @SerialName("pm10_density")
    PM10_DENSITY,
    @SerialName("pm1_density")
    PM1_DENSITY,
    @SerialName("pm2.5_density")
    PM2_5_DENSITY,
    @SerialName("power")
    POWER,
    @SerialName("pressure")
    PRESSURE,
    @SerialName("temperature")
    TEMPERATURE,
    @SerialName("tvoc")
    TVOC,
    @SerialName("voltage")
    VOLTAGE,
    @SerialName("water_level")
    WATER_LEVEL,
    @SerialName("water_meter")
    WATER_METER
}

@Serializable
enum class FloatUnit {
    @SerialName("unit.ampere")
    AMPERE,
    @SerialName("unit.cubic_meter")
    CUBIC_METER,
    @SerialName("unit.gigacalorie")
    GIGACALORIE,
    @SerialName("unit.kilowatt_hour")
    KILOWATT_HOUR,
    @SerialName("unit.illumination.lux")
    LUX,
    @SerialName("unit.density.mcg_m3")
    MCG_M3,
    @SerialName("unit.percent")
    PERCENT,
    @SerialName("unit.ppm")
    PPM,
    @SerialName("unit.volt")
    VOLT,
    @SerialName("unit.watt")
    WATT
}

@Serializable
enum class PressureUnit {
    @SerialName("unit.pressure.pascal")
    PASCAL,
    @SerialName("unit.pressure.mmhg")
    MMHG,
    @SerialName("unit.pressure.atm")
    ATM,
    @SerialName("unit.pressure.bar")
    BAR
}

@Serializable
enum class TemperatureUnit {
    @SerialName("unit.temperature.celsius")
    CELSIUS,
    @SerialName("unit.temperature.kelvin")
    KELVIN
}

@Serializable
open class FloatPropertyParameters(
    @SerialName("instance") open val instance: FloatPropertyInstance,
    @SerialName("unit") open val unit: FloatUnit? = null
) {
    open val range: Pair<Int?, Int?> get() = Pair(null, null)
}

interface FloatPropertyAboveZeroMixin {
    val range: Pair<Int?, Int?>
        get() = Pair(0, null)
}

//TODO: add another classes from github