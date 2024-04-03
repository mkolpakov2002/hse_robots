package ru.hse.control_system_v2.model.entities.universal

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder

import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import ru.hse.control_system_v2.model.entities.universal.scheme.UserHomeInfoApiResponse
import ru.hse.control_system_v2.model.entities.universal.scheme.UserInfoErrorModel
import ru.hse.control_system_v2.model.entities.universal.scheme.UserInfoModel

object YandexSmartHomeAPI {
    private const val BASE_URL = "https://api.iot.yandex.net/v1.0"
    private val client = HttpClient {
        expectSuccess = true
        install(HttpTimeout) {
            requestTimeoutMillis = 10000
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
        encodeDefaults = true
//        classDiscriminator = "#class"
    }

    suspend fun getUserInfo(accessToken: String): UserHomeInfoApiResponse? {
        return try {
            val request = HttpRequestBuilder().apply {
                method = HttpMethod.Get
                url("$BASE_URL/user/info")
                header("Authorization", "Bearer $accessToken")
            }
            Log.d("YandexSmartHomeAPI", "Outgoing request: $request")
            val response = client.request(request)
            val responseBody = response.bodyAsText()
            Log.d("YandexSmartHomeAPI", "User info response: $responseBody")
            when (response.status) {
                HttpStatusCode.OK -> {
                    val successResponse = json.decodeFromString<UserInfoModel>(responseBody)
                    UserHomeInfoApiResponse.Success(successResponse)
                }
                else -> {
                    val errorResponse = json.decodeFromString<UserInfoErrorModel>(responseBody)
                    UserHomeInfoApiResponse.Error(errorResponse)
                }
            }
        } catch (e: Exception) {
            Log.e("YandexSmartHomeAPI", "Error getting user info: $e")
            null
        }
    }

}