package ru.hse.control_system_v2.model.entities.universal.scheme

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = DeviceTypeSerializer::class)
sealed class DeviceType {
    @Serializable
    @SerialName("devices.types.light")
    object Light : DeviceType()

    @Serializable
    @SerialName("devices.types.socket")
    object Socket : DeviceType()

    @Serializable
    @SerialName("devices.types.switch")
    object Switch : DeviceType()

    @Serializable
    @SerialName("devices.types.thermostat")
    object Thermostat : DeviceType()

    @Serializable
    @SerialName("devices.types.thermostat.ac")
    object ThermostatAC : DeviceType()

    @Serializable
    @SerialName("devices.types.media_device")
    object MediaDevice : DeviceType()

    @Serializable
    @SerialName("devices.types.media_device.tv")
    object MediaDeviceTV : DeviceType()

    @Serializable
    @SerialName("devices.types.media_device.tv_box")
    object MediaDeviceTVBox : DeviceType()

    @Serializable
    @SerialName("devices.types.media_device.receiver")
    object MediaDeviceReceiver : DeviceType()

    @Serializable
    @SerialName("devices.types.cooking")
    object Cooking : DeviceType()

    @Serializable
    @SerialName("devices.types.cooking.coffee_maker")
    object CoffeeMaker : DeviceType()

    @Serializable
    @SerialName("devices.types.cooking.kettle")
    object Kettle : DeviceType()

    @Serializable
    @SerialName("devices.types.cooking.multicooker")
    object Multicooker : DeviceType()

    @Serializable
    @SerialName("devices.types.openable")
    object Openable : DeviceType()

    @Serializable
    @SerialName("devices.types.openable.curtain")
    object OpenableCurtain : DeviceType()

    @Serializable
    @SerialName("devices.types.humidifier")
    object Humidifier : DeviceType()

    @Serializable
    @SerialName("devices.types.purifier")
    object Purifier : DeviceType()

    @Serializable
    @SerialName("devices.types.vacuum_cleaner")
    object VacuumCleaner : DeviceType()

    @Serializable
    @SerialName("devices.types.washing_machine")
    object WashingMachine : DeviceType()

    @Serializable
    @SerialName("devices.types.dishwasher")
    object Dishwasher : DeviceType()

    @Serializable
    @SerialName("devices.types.iron")
    object Iron : DeviceType()

    @Serializable
    @SerialName("devices.types.sensor")
    object Sensor : DeviceType()

    @Serializable
    @SerialName("devices.types.sensor.motion")
    object SensorMotion : DeviceType()

    @Serializable
    @SerialName("devices.types.sensor.door")
    object SensorDoor : DeviceType()

    @Serializable
    @SerialName("devices.types.sensor.window")
    object SensorWindow : DeviceType()

    @Serializable
    @SerialName("devices.types.sensor.water_leak")
    object SensorWaterLeak : DeviceType()

    @Serializable
    @SerialName("devices.types.sensor.smoke")
    object SensorSmoke : DeviceType()

    @Serializable
    @SerialName("devices.types.sensor.gas")
    object SensorGas : DeviceType()

    @Serializable
    @SerialName("devices.types.sensor.vibration")
    object SensorVibration : DeviceType()

    @Serializable
    @SerialName("devices.types.sensor.button")
    object SensorButton : DeviceType()

    @Serializable
    @SerialName("devices.types.sensor.illumination")
    object SensorIllumination : DeviceType()

    @Serializable
    @SerialName("devices.types.other")
    object Other : DeviceType()
}

@Serializable
data class CustomDeviceType(
    @SerialName(value = "serialName") val serialName: String
) : DeviceType()

object DeviceTypeSerializer : KSerializer<DeviceType> {
    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildSerialDescriptor("DeviceType", PolymorphicKind.OPEN)

    override fun deserialize(decoder: Decoder): DeviceType {
        val jsonDecoder = decoder as? JsonDecoder ?: error("This class can be loaded only by Json")
        val json = jsonDecoder.decodeJsonElement().jsonPrimitive.content

        return when (json) {
            "devices.types.light" -> DeviceType.Light
            "devices.types.socket" -> DeviceType.Socket
            "devices.types.switch" -> DeviceType.Switch
            "devices.types.thermostat" -> DeviceType.Thermostat
            "devices.types.thermostat.ac" -> DeviceType.ThermostatAC
            "devices.types.media_device" -> DeviceType.MediaDevice
            "devices.types.media_device.tv" -> DeviceType.MediaDeviceTV
            "devices.types.media_device.tv_box" -> DeviceType.MediaDeviceTVBox
            "devices.types.media_device.receiver" -> DeviceType.MediaDeviceReceiver
            "devices.types.cooking" -> DeviceType.Cooking
            "devices.types.cooking.coffee_maker" -> DeviceType.CoffeeMaker
            "devices.types.cooking.kettle" -> DeviceType.Kettle
            "devices.types.cooking.multicooker" -> DeviceType.Multicooker
            "devices.types.openable" -> DeviceType.Openable
            "devices.types.openable.curtain" -> DeviceType.OpenableCurtain
            "devices.types.humidifier" -> DeviceType.Humidifier
            "devices.types.purifier" -> DeviceType.Purifier
            "devices.types.vacuum_cleaner" -> DeviceType.VacuumCleaner
            "devices.types.washing_machine" -> DeviceType.WashingMachine
            "devices.types.dishwasher" -> DeviceType.Dishwasher
            "devices.types.iron" -> DeviceType.Iron
            "devices.types.sensor" -> DeviceType.Sensor
            "devices.types.sensor.motion" -> DeviceType.SensorMotion
            "devices.types.sensor.door" -> DeviceType.SensorDoor
            "devices.types.sensor.window" -> DeviceType.SensorWindow
            "devices.types.sensor.water_leak" -> DeviceType.SensorWaterLeak
            "devices.types.sensor.smoke" -> DeviceType.SensorSmoke
            "devices.types.sensor.gas" -> DeviceType.SensorGas
            "devices.types.sensor.vibration" -> DeviceType.SensorVibration
            "devices.types.sensor.button" -> DeviceType.SensorButton
            "devices.types.sensor.illumination" -> DeviceType.SensorIllumination
            "devices.types.other" -> DeviceType.Other
            else -> CustomDeviceType(json)
        }
    }

    override fun serialize(encoder: Encoder, value: DeviceType) {
        val jsonEncoder = encoder as? JsonEncoder ?: error("This class can be saved only by Json")
        jsonEncoder.encodeJsonElement(
            JsonPrimitive(
            when (value) {
                is DeviceType.Light -> "devices.types.light"
                is DeviceType.Socket -> "devices.types.socket"
                is DeviceType.Switch -> "devices.types.switch"
                is DeviceType.Thermostat -> "devices.types.thermostat"
                is DeviceType.ThermostatAC -> "devices.types.thermostat.ac"
                is DeviceType.MediaDevice -> "devices.types.media_device"
                is DeviceType.MediaDeviceTV -> "devices.types.media_device.tv"
                is DeviceType.MediaDeviceTVBox -> "devices.types.media_device.tv_box"
                is DeviceType.MediaDeviceReceiver -> "devices.types.media_device.receiver"
                is DeviceType.Cooking -> "devices.types.cooking"
                is DeviceType.CoffeeMaker -> "devices.types.cooking.coffee_maker"
                is DeviceType.Kettle -> "devices.types.cooking.kettle"
                is DeviceType.Multicooker -> "devices.types.cooking.multicooker"
                is DeviceType.Openable -> "devices.types.openable"
                is DeviceType.OpenableCurtain -> "devices.types.openable.curtain"
                is DeviceType.Humidifier -> "devices.types.humidifier"
                is DeviceType.Purifier -> "devices.types.purifier"
                is DeviceType.VacuumCleaner -> "devices.types.vacuum_cleaner"
                is DeviceType.WashingMachine -> "devices.types.washing_machine"
                is DeviceType.Dishwasher -> "devices.types.dishwasher"
                is DeviceType.Iron -> "devices.types.iron"
                is DeviceType.Sensor -> "devices.types.sensor"
                is DeviceType.SensorMotion -> "devices.types.sensor.motion"
                is DeviceType.SensorDoor -> "devices.types.sensor.door"
                is DeviceType.SensorWindow -> "devices.types.sensor.window"
                is DeviceType.SensorWaterLeak -> "devices.types.sensor.water_leak"
                is DeviceType.SensorSmoke -> "devices.types.sensor.smoke"
                is DeviceType.SensorGas -> "devices.types.sensor.gas"
                is DeviceType.SensorVibration -> "devices.types.sensor.vibration"
                is DeviceType.SensorButton -> "devices.types.sensor.button"
                is DeviceType.SensorIllumination -> "devices.types.sensor.illumination"
                is DeviceType.Other -> "devices.types.other"
                is CustomDeviceType -> value.serialName
            }
        )
        )
    }
}

@Serializable
data class DeviceInfo(
    val manufacturer: String? = null,
    val model: String? = null,
    @SerialName("hw_version") val hwVersion: String? = null,
    @SerialName("sw_version") val swVersion: String? = null
) : APIModel

@Serializable
data class DeviceDescription(
    val id: String,
    val name: String,
    val description: String? = null,
    val room: String? = null,
    val type: DeviceType,
    val capabilities: List<CapabilityDescription>? = null,
    val properties: List<PropertyDescription>? = null,
    @SerialName("device_info") val deviceInfo: DeviceInfo? = null
) : APIModel

@Serializable
data class UserDeviceDescription(
    val id: String,
    val name: String,
    val aliases: List<String> = listOf(),
    val room: String? = null,
    @SerialName("external_id") val externalId: String,
    @SerialName("skill_id") val skillId: String,
    val type: DeviceType,
    val groups: List<String>? = null,
    val capabilities: List<CapabilityDescription>? = null,
    val properties: List<PropertyDescription>? = null,
    @SerialName("household_id") val householdId: String
) : APIModel

@Serializable
data class DeviceState(
    val id: String,
    val capabilities: List<CapabilityInstanceState>? = null,
    val properties: List<PropertyInstanceState>? = null,
    @SerialName("error_code") val errorCode: ResponseCode? = null,
    @SerialName("error_message") val errorMessage: String? = null
) : APIModel

@Serializable
data class DeviceList(
    @SerialName("user_id") val userId: String,
    val devices: List<DeviceDescription>
) : ResponsePayload()

@Serializable
data class DeviceStates(
    val devices: List<DeviceState>
) : ResponsePayload()

@Serializable
data class StatesRequestDevice(
    val id: String,
    @SerialName("custom_data") val customData: CustomData? = null
) : APIModel

@Serializable
sealed class CustomData : APIModel

@Serializable
data class StringCustomData(val value: String) : CustomData()

@Serializable
data class IntCustomData(val value: Int) : CustomData()

@Serializable
data class BooleanCustomData(val value: Boolean) : CustomData()

@Serializable
data class DoubleCustomData(val value: Double) : CustomData()

@Serializable
data class StatesRequest(
    val devices: List<StatesRequestDevice>
) : APIModel

@Serializable
data class ActionRequestDevice(
    val id: String,
    val capabilities: List<CapabilityInstanceAction>
) : APIModel

@Serializable
data class ActionRequestPayload(
    val devices: List<ActionRequestDevice>
) : APIModel

@Serializable
data class ActionRequest(
    val payload: ActionRequestPayload
) : APIModel

@Serializable
data class SuccessActionResult(
    val status: String = "DONE"
) : APIModel

@Serializable
data class FailedActionResult(
    val status: String = "ERROR",
    @SerialName("error_code") val errorCode: ResponseCode
) : APIModel

@Serializable
data class ActionResultCapabilityState(
    val instance: CapabilityInstance,
    val value: CapabilityInstanceActionResultValue? = null,
    @SerialName("action_result") val actionResult: SuccessActionResult? = null
) : APIModel

@Serializable
data class ActionResultCapability(
    val type: CapabilityType,
    val state: ActionResultCapabilityState
) : APIModel

@Serializable
data class ActionResultDevice(
    val id: String,
    val capabilities: List<ActionResultCapability>? = null,
    @SerialName("action_result") val actionResult: SuccessActionResult? = null
) : APIModel

@Serializable
data class ActionResult(
    val devices: List<ActionResultDevice>
) : ResponsePayload()