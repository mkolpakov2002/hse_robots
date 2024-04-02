package ru.hse.control_system_v2.model.entities.universal.scheme

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class DeviceType {
    @SerialName("devices.types.light")
    LIGHT,
    @SerialName("devices.types.socket")
    SOCKET,
    @SerialName("devices.types.switch")
    SWITCH,
    @SerialName("devices.types.thermostat")
    THERMOSTAT,
    @SerialName("devices.types.thermostat.ac")
    THERMOSTAT_AC,
    @SerialName("devices.types.media_device")
    MEDIA_DEVICE,
    @SerialName("devices.types.media_device.tv")
    MEDIA_DEVICE_TV,
    @SerialName("devices.types.media_device.tv_box")
    MEDIA_DEVICE_TV_BOX,
    @SerialName("devices.types.media_device.receiver")
    MEDIA_DEVICE_RECEIVER,
    @SerialName("devices.types.cooking")
    COOKING,
    @SerialName("devices.types.cooking.coffee_maker")
    COFFEE_MAKER,
    @SerialName("devices.types.cooking.kettle")
    KETTLE,
    @SerialName("devices.types.cooking.multicooker")
    MULTICOOKER,
    @SerialName("devices.types.openable")
    OPENABLE,
    @SerialName("devices.types.openable.curtain")
    OPENABLE_CURTAIN,
    @SerialName("devices.types.humidifier")
    HUMIDIFIER,
    @SerialName("devices.types.purifier")
    PURIFIER,
    @SerialName("devices.types.vacuum_cleaner")
    VACUUM_CLEANER,
    @SerialName("devices.types.washing_machine")
    WASHING_MACHINE,
    @SerialName("devices.types.dishwasher")
    DISHWASHER,
    @SerialName("devices.types.iron")
    IRON,
    @SerialName("devices.types.sensor")
    SENSOR,
    @SerialName("devices.types.sensor.motion")
    SENSOR_MOTION,
    @SerialName("devices.types.sensor.door")
    SENSOR_DOOR,
    @SerialName("devices.types.sensor.window")
    SENSOR_WINDOW,
    @SerialName("devices.types.sensor.water_leak")
    SENSOR_WATER_LEAK,
    @SerialName("devices.types.sensor.smoke")
    SENSOR_SMOKE,
    @SerialName("devices.types.sensor.gas")
    SENSOR_GAS,
    @SerialName("devices.types.sensor.vibration")
    SENSOR_VIBRATION,
    @SerialName("devices.types.sensor.button")
    SENSOR_BUTTON,
    @SerialName("devices.types.sensor.illumination")
    SENSOR_ILLUMINATION,
    @SerialName("devices.types.other")
    OTHER
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