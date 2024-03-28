package ru.hse.control_system_v2.model.entities.universal.classes.connection_desc.yandex

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ru.hse.control_system_v2.model.entities.universal.classes.device_desc.api.Capability
import ru.hse.control_system_v2.model.entities.universal.classes.device_desc.api.Device
import ru.hse.control_system_v2.model.entities.universal.classes.device_desc.api.UserInfo

class RemoteDeviceDataSource(private val client: HttpClient) : DeviceDataSource {
    override suspend fun getDevices(): List<Device> {
        return try {
            val response: UserInfo = client.get("https://api.iot.yandex.net/v1.0/user/info").body()
            Log.i("MIEM", "Response: $response")
            response.devices
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getDeviceById(deviceId: String): Device? {
        return try {
            val response: Device = client.get("https://api.iot.yandex.net/v1.0/devices/$deviceId").body()
            Log.i("MIEM", "Response: $response")
            response
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun handleDeviceAction(deviceId: String, capability: Capability): DeviceActionsResponse? {
        val action = createCapabilityAction(deviceId, capability)
        val requestBody = DeviceActionsRequest(listOf(DeviceAction(deviceId, listOf(action))))
        Log.i("MIEM", "Request: $requestBody")
        return try {
            val response: DeviceActionsResponse = client.post("https://api.iot.yandex.net/v1.0/devices/actions") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()
            response
        } catch (e: Exception) {
            null
        }
    }

    private fun createCapabilityAction(deviceId: String, capability: Capability): CapabilityAction {
        val state = when (capability.type) {
            "devices.capabilities.on_off" -> OnOffState(value = capability.state?.get("value")?.toString()?.toBoolean() ?: false)
            "devices.capabilities.color_setting" -> ColorSettingState(value = capability.state?.get("value")?.toString() ?: "")
            "devices.capabilities.mode" -> ModeState(value = capability.state?.get("value")?.toString() ?: "")
            "devices.capabilities.range" -> RangeState(value = capability.state?.get("value")?.toString()?.toInt() ?: 0)
            "devices.capabilities.toggle" -> ToggleState(value = capability.state?.get("value")?.toString()?.toBoolean() ?: false)
            else -> throw IllegalArgumentException("Unsupported capability type: ${capability.type}")
        }
        return CapabilityAction(capability.type, state)
    }
}