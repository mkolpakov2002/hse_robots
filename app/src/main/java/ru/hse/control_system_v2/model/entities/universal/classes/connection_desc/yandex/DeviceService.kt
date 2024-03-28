package ru.hse.control_system_v2.model.entities.universal.classes.connection_desc.yandex

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import ru.hse.control_system_v2.App
import ru.hse.control_system_v2.model.entities.universal.classes.device_desc.api.Capability
import ru.hse.control_system_v2.model.entities.universal.classes.device_desc.api.Device
import ru.hse.control_system_v2.model.entities.universal.classes.device_desc.api.DeviceInfo
import ru.hse.control_system_v2.model.entities.universal.classes.device_desc.api.Property
import ru.hse.control_system_v2.model.entities.universal.classes.device_desc.room.CapabilityEntity
import ru.hse.control_system_v2.model.entities.universal.classes.device_desc.room.DeviceEntity
import ru.hse.control_system_v2.model.entities.universal.classes.device_desc.room.DeviceInfoEntity
import ru.hse.control_system_v2.model.entities.universal.classes.device_desc.room.PropertyEntity

class DeviceService() {
    suspend fun handleDeviceAction(deviceId: String, capability: Capability): DeviceActionResult? {
        val action = when (capability.type) {
            "devices.capabilities.on_off" -> createCapabilityAction(deviceId, capability)
            "devices.capabilities.color_setting" -> createCapabilityAction(deviceId, capability)
            "devices.capabilities.mode" -> createCapabilityAction(deviceId, capability)
            "devices.capabilities.range" -> createCapabilityAction(deviceId, capability)
            "devices.capabilities.toggle" -> createCapabilityAction(deviceId, capability)
            else -> throw IllegalArgumentException("Unsupported capability type: ${capability.type}")
        }

        val requestBody = DeviceActionsRequest(listOf(DeviceAction(deviceId, listOf(action))))

        // Логгирование запроса в JSON
        val requestBodyJson: String = Json.encodeToString(requestBody)
        Log.i("MIEM", "Request: $requestBodyJson")

        try {
            val response: HttpResponse
            App.httpClient.use { client ->
                try {
                    response = client.post("https://api.iot.yandex.net/v1.0/devices/actions") {
                        contentType(ContentType.Application.Json)
                        setBody(requestBody)
                    }

                    // Логгирование ответа
                    response.bodyAsText().also { responseBody ->
                        Log.i("MIEM", "Response: $responseBody")
                    }
                } catch (e: Exception) {
                    Log.e("MIEM", "Error occurred: ${e.message}")
                    e.message ?: "Error occurred"
                }
            }
        } catch (e: Exception) {
            Log.e("Failed to handle device action", e.message.orEmpty())
            throw e
        }
        return null
    }

    private fun createCapabilityAction(deviceId: String, capability: Capability): CapabilityAction {
        val state = when (capability.type) {
            "devices.capabilities.on_off" -> OnOffState(
                value = capability.state?.get("value")?.toString()?.toBoolean() ?: false
            )

            "devices.capabilities.color_setting" -> ColorSettingState(
                value = capability.state?.get(
                    "value"
                )?.toString() ?: ""
            )

            "devices.capabilities.mode" -> ModeState(
                value = capability.state?.get("value")?.toString() ?: ""
            )

            "devices.capabilities.range" -> RangeState(
                value = capability.state?.get("value")?.toString()?.toInt() ?: 0
            )

            "devices.capabilities.toggle" -> ToggleState(
                value = capability.state?.get("value")?.toString()?.toBoolean() ?: false
            )

            else -> throw IllegalArgumentException("Unsupported capability type: ${capability.type}")
        }
        return CapabilityAction(capability.type, state)
    }
}

@Serializable
data class DeviceActionsRequest(
    val devices: List<DeviceAction>
)

@Serializable
data class DeviceAction(
    val id: String,
    val actions: List<CapabilityAction>
)

@Serializable
data class CapabilityAction(
    val type: String,
    @Serializable(with = CapabilityStateSerializer::class)
    val state: CapabilityState
)

object CapabilityStateSerializer : KSerializer<CapabilityState> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CapabilityState")

    override fun serialize(encoder: Encoder, value: CapabilityState) {
        val compositeOutput = encoder.beginStructure(descriptor)
        when (value) {
            is OnOffState -> {
                compositeOutput.encodeStringElement(buildClassSerialDescriptor("OnOffState") {
                    element("instance", String.serializer().descriptor)
                    element("relative", Boolean.serializer().descriptor)
                    element("value", Boolean.serializer().descriptor)
                }, 0, value.instance)
                compositeOutput.encodeBooleanElement(buildClassSerialDescriptor("OnOffState") {
                    element("instance", String.serializer().descriptor)
                    element("relative", Boolean.serializer().descriptor)
                    element("value", Boolean.serializer().descriptor)
                }, 1, value.relative)
                compositeOutput.encodeBooleanElement(buildClassSerialDescriptor("OnOffState") {
                    element("instance", String.serializer().descriptor)
                    element("relative", Boolean.serializer().descriptor)
                    element("value", Boolean.serializer().descriptor)
                }, 2, value.value)
            }
            is ColorSettingState -> {
                compositeOutput.encodeStringElement(buildClassSerialDescriptor("ColorSettingState") {
                    element("instance", String.serializer().descriptor)
                    element("value", String.serializer().descriptor)
                }, 0, value.instance)
                compositeOutput.encodeStringElement(buildClassSerialDescriptor("ColorSettingState") {
                    element("instance", String.serializer().descriptor)
                    element("value", String.serializer().descriptor)
                }, 1, value.value)
            }
            is ModeState -> {
                compositeOutput.encodeStringElement(buildClassSerialDescriptor("ModeState") {
                    element("instance", String.serializer().descriptor)
                    element("value", String.serializer().descriptor)
                }, 0, value.instance)
                compositeOutput.encodeStringElement(buildClassSerialDescriptor("ModeState") {
                    element("instance", String.serializer().descriptor)
                    element("value", String.serializer().descriptor)
                }, 1, value.value)
            }
            is RangeState -> {
                compositeOutput.encodeStringElement(buildClassSerialDescriptor("RangeState") {
                    element("instance", String.serializer().descriptor)
                    element("value", Int.serializer().descriptor)
                }, 0, value.instance)
                compositeOutput.encodeIntElement(buildClassSerialDescriptor("RangeState") {
                    element("instance", String.serializer().descriptor)
                    element("value", Int.serializer().descriptor)
                }, 1, value.value)
            }
            is ToggleState -> {
                compositeOutput.encodeStringElement(buildClassSerialDescriptor("ToggleState") {
                    element("instance", String.serializer().descriptor)
                    element("value", Boolean.serializer().descriptor)
                }, 0, value.instance)
                compositeOutput.encodeBooleanElement(buildClassSerialDescriptor("ToggleState") {
                    element("instance", String.serializer().descriptor)
                    element("value", Boolean.serializer().descriptor)
                }, 1, value.value)
            }
        }
        compositeOutput.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): CapabilityState {
        // реализация десериализации, если необходимо
        throw NotImplementedError("Deserialization is not supported")
    }
}

@Serializable
sealed class CapabilityState

@Serializable
data class OnOffState(
    @SerialName("instance") val instance: String = "on",
    @SerialName("relative") val relative: Boolean = false,
    @SerialName("value") val value: Boolean
) : CapabilityState()

@Serializable
data class ColorSettingState(
    @SerialName("instance") val instance: String = "color",
    @SerialName("value") val value: String
) : CapabilityState()

@Serializable
data class ModeState(
    @SerialName("instance") val instance: String = "mode",
    @SerialName("value") val value: String
) : CapabilityState()

@Serializable
data class RangeState(
    @SerialName("instance") val instance: String = "range",
    @SerialName("value") val value: Int
) : CapabilityState()

@Serializable
data class ToggleState(
    @SerialName("instance") val instance: String = "toggle",
    @SerialName("value") val value: Boolean
) : CapabilityState()

@Serializable
data class DeviceActionsResponse(
    val status: String,
    val requestId: String? = null,
    val devices: List<DeviceActionResult>
)

@Serializable
data class DeviceActionResult(
    val id: String,
    val capabilities: List<CapabilityActionResult>
)

@Serializable
data class CapabilityActionResult(
    val type: String,
    val state: StateResult
)

@Serializable
sealed class StateValue

@Serializable
data class StringValue(val value: String) : StateValue()

@Serializable
data class IntValue(val value: Int) : StateValue()

@Serializable
data class BooleanValue(val value: Boolean) : StateValue()

@Serializable
data class StateResult(
    @SerialName("type")
    val type: String? = null,
    @SerialName("state")
    val state: Map<String, StateValue>? = null,
    @SerialName("actionResult")
    val actionResult: ActionResult? = null
)

@Serializable
data class ActionResult(
    val status: String,
    val errorCode: String? = null,
    val errorMessage: String? = null
)

private fun DeviceEntity.toDevice(
    capabilities: List<CapabilityEntity>,
    properties: List<PropertyEntity>,
    deviceInfo: DeviceInfo
): Device {
    return Device(
        id = id,
        name = name,
        aliases = aliases,
        type = type,
        state = state,
        groups = groups,
        room = room,
        externalId = externalId,
        skillId = skillId,
        capabilities = capabilities.map { it.toCapability() },
        properties = properties.map { it.toProperty() },
        deviceInfo = deviceInfo,
        householdId = householdId
    )
}

private fun DeviceInfoEntity.toDeviceInfo(): DeviceInfo {
    return DeviceInfo(
        manufacturer = manufacturer,
        model = model,
        hwVersion = hwVersion,
        swVersion = swVersion
    )
}

private fun CapabilityEntity.toCapability(): Capability {
    return Capability(
        type = type,
        retrievable = retrievable,
        parameters = parameters.parseJson(),
        state = state?.parseJson(),
        lastUpdated = lastUpdated
    )
}

private fun PropertyEntity.toProperty(): Property {
    return Property(
        type = type,
        retrievable = retrievable,
        parameters = parameters.parseJson(),
        state = state?.parseJson(),
        lastUpdated = lastUpdated
    )
}

private fun String.parseJson(): Map<String, Any> {
    return try {
        val jsonObject = Json.decodeFromString<JsonObject>(this)
        jsonObject.toMap()
    } catch (e: Exception) {
        emptyMap()
    }
}

private fun JsonObject.toMap(): Map<String, Any> {
    return this.jsonObject.entries.associate { (key, value) ->
        key to when (value) {
            is JsonObject -> value.toMap()
            else -> value.toString()
        }
    }
}
