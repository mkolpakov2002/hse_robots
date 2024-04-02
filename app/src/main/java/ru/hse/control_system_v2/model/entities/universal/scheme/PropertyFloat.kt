package ru.hse.control_system_v2.model.entities.universal.scheme

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Instance of an event property.
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/float-instance.html
 */
@Serializable
enum class FloatPropertyInstance {
    @SerialName("amperage") AMPERAGE,
    @SerialName("battery_level") BATTERY_LEVEL,
    @SerialName("co2_level") CO2_LEVEL,
    @SerialName("electricity_meter") ELECTRICITY_METER,
    @SerialName("food_level") FOOD_LEVEL,
    @SerialName("gas_meter") GAS_METER,
    @SerialName("heat_meter") HEAT_METER,
    @SerialName("humidity") HUMIDITY,
    @SerialName("illumination") ILLUMINATION,
    @SerialName("meter") METER,
    @SerialName("pm10_density") PM10_DENSITY,
    @SerialName("pm1_density") PM1_DENSITY,
    @SerialName("pm2.5_density") PM2_5_DENSITY,
    @SerialName("power") POWER,
    @SerialName("pressure") PRESSURE,
    @SerialName("temperature") TEMPERATURE,
    @SerialName("tvoc") TVOC,
    @SerialName("voltage") VOLTAGE,
    @SerialName("water_level") WATER_LEVEL,
    @SerialName("water_meter") WATER_METER
}

interface MeasurementUnit

@Serializable
enum class FloatUnit: MeasurementUnit {
    @SerialName("unit.ampere") AMPERE,
    @SerialName("unit.cubic_meter") CUBIC_METER,
    @SerialName("unit.gigacalorie") GIGACALORIE,
    @SerialName("unit.kilowatt_hour") KILOWATT_HOUR,
    @SerialName("unit.illumination.lux") LUX,
    @SerialName("unit.density.mcg_m3") MCG_M3,
    @SerialName("unit.percent") PERCENT,
    @SerialName("unit.ppm") PPM,
    @SerialName("unit.volt") VOLT,
    @SerialName("unit.watt") WATT
}

@Serializable
enum class PressureUnit: MeasurementUnit {
    @SerialName("unit.pressure.pascal") PASCAL,
    @SerialName("unit.pressure.mmhg") MMHG,
    @SerialName("unit.pressure.atm") ATM,
    @SerialName("unit.pressure.bar") BAR
}

@Serializable
enum class TemperatureUnit: MeasurementUnit {
    @SerialName("unit.temperature.celsius") CELSIUS,
    @SerialName("unit.temperature.kelvin") KELVIN
}

@Serializable
sealed class FloatPropertyParameters<T: MeasurementUnit>: PropertyParameters {
    abstract val instance: FloatPropertyInstance
    abstract val unit: T
    open val range: Pair<Int?, Int?>
        get() = Pair(null, null)
}

@Serializable
sealed class PressurePropertyParameters: FloatPropertyParameters<PressureUnit>()

@Serializable
sealed class TemperaturePropertyParameters : FloatPropertyParameters<TemperatureUnit>()


@Serializable
sealed class FloatPropertyAboveZeroMixin : FloatPropertyParameters<FloatUnit>() {
    override val range: Pair<Int?, Int?>
        get() = Pair(0, null)
}

@Serializable
sealed class PercentFloatPropertyParameters : FloatPropertyParameters<FloatUnit>()  {
    override val range: Pair<Int?, Int?>
        get() = Pair(0, 100)
}

@Serializable
data class DensityFloatPropertyParameters(
    override val instance: FloatPropertyInstance,
    override val unit: FloatUnit = FloatUnit.MCG_M3
) : FloatPropertyAboveZeroMixin()

@Serializable
data class AmperageFloatPropertyParameters(
    override val instance: FloatPropertyInstance = FloatPropertyInstance.AMPERAGE,
    override val unit: FloatUnit = FloatUnit.AMPERE
) : FloatPropertyAboveZeroMixin()

@Serializable
data class BatteryLevelFloatPropertyParameters(
    override val instance: FloatPropertyInstance = FloatPropertyInstance.BATTERY_LEVEL,
    override val unit: FloatUnit = FloatUnit.PERCENT
) : PercentFloatPropertyParameters()

@Serializable
data class CO2LevelFloatPropertyParameters(
    override val instance: FloatPropertyInstance = FloatPropertyInstance.CO2_LEVEL,
    override val unit: FloatUnit = FloatUnit.PPM
) : FloatPropertyAboveZeroMixin()

@Serializable
data class HumidityFloatPropertyParameters(
    override val instance: FloatPropertyInstance = FloatPropertyInstance.HUMIDITY,
    override val unit: FloatUnit = FloatUnit.PERCENT
) : PercentFloatPropertyParameters()

@Serializable
data class IlluminanceFloatPropertyParameters(
    override val instance: FloatPropertyInstance = FloatPropertyInstance.ILLUMINATION,
    override val unit: FloatUnit = FloatUnit.LUX
) : FloatPropertyAboveZeroMixin()

@Serializable
data class PMFloatPropertyParameters(
    override val instance: FloatPropertyInstance,
    override val unit: FloatUnit = FloatUnit.MCG_M3
) : FloatPropertyAboveZeroMixin()

@Serializable
data class PowerFloatPropertyParameters(
    override val instance: FloatPropertyInstance = FloatPropertyInstance.POWER,
    override val unit: FloatUnit = FloatUnit.WATT
) : FloatPropertyAboveZeroMixin()

@Serializable
data class PressureFloatPropertyParameters(
    override val instance: FloatPropertyInstance = FloatPropertyInstance.PRESSURE,
    override val unit: PressureUnit = PressureUnit.PASCAL
) : PressurePropertyParameters()

@Serializable
data class TemperatureFloatPropertyParameters(
    override val instance: FloatPropertyInstance = FloatPropertyInstance.TEMPERATURE,
    override val unit: TemperatureUnit = TemperatureUnit.CELSIUS
) : TemperaturePropertyParameters()

@Serializable
data class TVOCFloatPropertyParameters(
    override val instance: FloatPropertyInstance = FloatPropertyInstance.TVOC,
    override val unit: FloatUnit = FloatUnit.PPM
) : FloatPropertyAboveZeroMixin()

@Serializable
data class VoltageFloatPropertyParameters(
    override val instance: FloatPropertyInstance = FloatPropertyInstance.VOLTAGE,
    override val unit: FloatUnit = FloatUnit.VOLT
) : FloatPropertyAboveZeroMixin()

@Serializable
data class WaterLevelFloatPropertyParameters(
    override val instance: FloatPropertyInstance = FloatPropertyInstance.WATER_LEVEL,
    override val unit: FloatUnit = FloatUnit.PERCENT
) : PercentFloatPropertyParameters()

@Serializable
data class WaterMeterFloatPropertyParameters(
    override val instance: FloatPropertyInstance = FloatPropertyInstance.WATER_METER,
    override val unit: FloatUnit = FloatUnit.CUBIC_METER
) : FloatPropertyAboveZeroMixin()
