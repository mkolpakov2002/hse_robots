package ru.hse.control_system_v2.model.entities.universal

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import ru.hse.control_system_v2.model.entities.universal.scheme.UserHomeInfoApiResponse
import ru.hse.control_system_v2.model.entities.universal.scheme.UserInfoModel

class YandexSmartHomeAPITest {
    private val accessToken = "y0_AgAEA7qkJBRwAAtNHQAAAAD7NOpOAABZXzInfHtFAoIVc4SUjPlw0bda8g"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
        encodeDefaults = true
//        classDiscriminator = "#class"
    }

    fun testGetUserInfo() = runBlocking {
        val userInfo = YandexSmartHomeAPI.getUserInfo(accessToken)
        println("User Info: $userInfo")
        userInfo?.let {
            val dataUserInfo = json.encodeToJsonElement(UserHomeInfoApiResponse.serializer(), it)
            println("User Info in data object: $dataUserInfo")
        }
    }

    fun testAll() {
        testGetUserInfo()
    }
}