package ru.hse.control_system_v2.model.entities.universal.scheme

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed class UserHomeInfoApiResponse {
    data class Success(val data: UserInfoModel) : UserHomeInfoApiResponse()
    data class Error(val error: UserInfoErrorModel) : UserHomeInfoApiResponse()
}

@Serializable
data class UserInfoModel(
    @SerialName("status")
    val status: String,
    @SerialName("request_id")
    val requestId: String,
    @SerialName("rooms")
    val roomList: List<RoomModel>,
    @SerialName("groups")
    val groupList: List<GroupModel>,
    @SerialName("devices")
    val deviceList: List<UserDeviceDescription>,
    @SerialName("scenarios")
    val scenarioList: List<ScenarioModel>,
    @SerialName("households")
    val householdList: List<HouseholdModel>
)

data class UserInfoErrorModel(
    val status: String,
    @SerialName("request_id")
    val requestId: String,
    val error: String
)

@Serializable
data class RoomModel(
    val id: String,
    val name: String,
    @SerialName("household_id")
    val householdId: String,
    @SerialName("devices")
    val deviceIdList: List<String>
)

@Serializable
data class GroupModel(
    val id: String,
    val name: String,
    @SerialName("aliases")
    val aliasesList: List<String>,
    @SerialName("household_id")
    val householdId: String,
    val type: String,
    @SerialName("devices")
    val deviceIdList: List<String>,
    @SerialName("capabilities")
    val capabilityList: List<CapabilityDescription>
)

@Serializable
data class ScenarioModel(
    val id: String,
    val name: String,
    @SerialName("is_active")
    val isActive: Boolean
)

@Serializable
data class HouseholdModel(
    val id: String,
    val name: String
)