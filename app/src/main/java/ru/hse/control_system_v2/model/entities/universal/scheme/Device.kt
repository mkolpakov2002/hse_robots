package ru.hse.control_system_v2.model.entities.universal.scheme

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * User device type.
 *
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/device-types.html
 */
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
    @SerialName("devices.types.camera")
    CAMERA,
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
    @SerialName("devices.types.fan")
    FAN,
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
    @SerialName("devices.types.sensor.vibration")
    SENSOR_VIBRATION,
    @SerialName("devices.types.sensor.illumination")
    SENSOR_ILLUMINATION,
    @SerialName("devices.types.sensor.open")
    SENSOR_OPEN,
    @SerialName("devices.types.sensor.climate")
    SENSOR_CLIMATE,
    @SerialName("devices.types.sensor.water_leak")
    SENSOR_WATER_LEAK,
    @SerialName("devices.types.sensor.button")
    SENSOR_BUTTON,
    @SerialName("devices.types.sensor.gas")
    SENSOR_GAS,
    @SerialName("devices.types.sensor.smoke")
    SENSOR_SMOKE,
    @SerialName("devices.types.smart_meter")
    SMART_METER,
    @SerialName("devices.types.smart_meter.cold_water")
    SMART_METER_COLD_WATER,
    @SerialName("devices.types.smart_meter.electricity")
    SMART_METER_ELECTRICITY,
    @SerialName("devices.types.smart_meter.gas")
    SMART_METER_GAS,
    @SerialName("devices.types.smart_meter.heat")
    SMART_METER_HEAT,
    @SerialName("devices.types.smart_meter.hot_water")
    SMART_METER_HOT_WATER,
    @SerialName("devices.types.pet_drinking_fountain")
    PET_DRINKING_FOUNTAIN,
    @SerialName("devices.types.pet_feeder")
    PET_FEEDER,
    @SerialName("devices.types.other")
    OTHER
}

/**
 * Extended device info.
 */
@Serializable
data class DeviceInfo(
    val manufacturer: String? = null,
    val model: String? = null,
    @SerialName("hw_version")
    val hwVersion: String? = null,
    @SerialName("sw_version")
    val swVersion: String? = null
)

/**
 * Device description for a device list request.
 */
@Serializable
data class DeviceDescription(
    val id: String,
    val name: String,
    val description: String? = null,
    val room: String? = null,
    val type: DeviceType,
    val capabilities: List<CapabilityDescription>? = null,
    val properties: List<PropertyDescription>? = null,
    @SerialName("device_info")
    val deviceInfo: DeviceInfo? = null
)

/**
 * Device state for a state query request.
 */
@Serializable
data class DeviceState(
    val id: String,
    val capabilities: List<CapabilityInstanceState>? = null,
    val properties: List<PropertyInstanceState>? = null,
    @SerialName("error_code")
    val errorCode: ResponseCode? = null,
    @SerialName("error_message")
    val errorMessage: String? = null
)

/**
 * Response payload for a device list request.
 */
@Serializable
data class DeviceList(
    @SerialName("user_id")
    val userId: String,
    val devices: List<DeviceDescription>
) : ResponsePayload()

/**
 * Response payload for a state query request.
 */
@Serializable
data class DeviceStates(
    val devices: List<DeviceState>
) : ResponsePayload()

/**
 * Device for a state query request.
 */
//TODO: Serializable у Any
//@Serializable
//data class StatesRequestDevice(
//    val id: String,
//    @SerialName("custom_data")
//    val customData: Map<String, Any>? = null
//)
//
///**
// * Request body for a state query request.
// */
//@Serializable
//data class StatesRequest(
//    val devices: List<StatesRequestDevice>
//)

/**
 * Device for a state change request.
 */
@Serializable
data class ActionRequestDevice(
    val id: String,
    val capabilities: List<CapabilityInstanceAction>
)

/**
 * Request payload for state change request.
 */
@Serializable
data class ActionRequestPayload(
    val devices: List<ActionRequestDevice>
)

/**
 * Request body for a state change request.
 */
@Serializable
data class ActionRequest(
    val payload: ActionRequestPayload
)

/**
 * Success device action result.
 */
@Serializable
data class SuccessActionResult(
    val status: String = "DONE"
)

/**
 * Failed device action result.
 */
@Serializable
data class FailedActionResult(
    val status: String = "ERROR",
    @SerialName("error_code")
    val errorCode: ResponseCode
)

/**
 * Result of capability instance state change.
 */
@Serializable
data class ActionResultCapabilityState(
    val instance: CapabilityInstance,
    val value: CapabilityInstanceActionResultValue? = null,
    @SerialName("action_result")
    val actionResult: SuccessActionResult? = null
)

/**
 * Result of capability state change.
 */
@Serializable
data class ActionResultCapability(
    val type: CapabilityType,
    val state: ActionResultCapabilityState
)

/**
 * Device for a state change response.
 */
@Serializable
data class ActionResultDevice(
    val id: String,
    val capabilities: List<ActionResultCapability>? = null,
    @SerialName("action_result")
    val actionResult: FailedActionResult? = null
)

/**
 * Response for a device state change.
 */
@Serializable
data class ActionResult(
    val devices: List<ActionResultDevice>
) : ResponsePayload()