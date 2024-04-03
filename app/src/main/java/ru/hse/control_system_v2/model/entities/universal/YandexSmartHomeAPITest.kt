package ru.hse.control_system_v2.model.entities.universal

import kotlinx.coroutines.runBlocking

class YandexSmartHomeAPITest {
    private val accessToken = "y0_AgAEA7qkJBRwAAtNHQAAAAD7NOpOAABZXzInfHtFAoIVc4SUjPlw0bda8g"

    fun testGetUserInfo() = runBlocking {
        val userInfo = YandexSmartHomeAPI.getUserInfo(accessToken)
        println("User Info: $userInfo")
    }

    fun testAll() {
        testGetUserInfo()
    }
}