package ru.hse.control_system_v2

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.HttpResponseValidator
import io.ktor.client.features.auth.Auth
import io.ktor.client.features.auth.providers.BearerTokens
import io.ktor.client.features.auth.providers.bearer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import kotlinx.serialization.json.Json
import ru.hse.control_system_v2.model.db.AppDatabase
import ru.hse.control_system_v2.model.entities.universal.classes.connection_desc.yandex.RemoteDeviceDataSource
import ru.hse.control_system_v2.model.entities.universal.classes.device_desc.room.DeviceRepository

class App : Application() {

    val token = "y0_AgAEA7qkJBRwAAtNHQAAAAD7NOpOAABZXzInfHtFAoIVc4SUjPlw0bda8g"

    override fun onCreate() {
        super.onCreate()
        instance = this
        mSettings = PreferenceManager.getDefaultSharedPreferences(
            instance
        )
        httpClient = HttpClient(Android) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                level = LogLevel.ALL
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(token, token)
                    }
                }
            }
            HttpResponseValidator {
                handleResponseException {
                    Log.e("MIEM", "Error occurred: ${it.message}")
                }
            }
        }
        val deviceDao = AppDatabase.getInstance(this).deviceDao()
        val remoteDeviceDataSource = RemoteDeviceDataSource(client = httpClient)
        deviceRepository = DeviceRepository(remoteDeviceDataSource)
    }

    companion object {
        lateinit var deviceRepository: DeviceRepository
            private set
        lateinit var httpClient: HttpClient
            private set
        lateinit var instance: App
            private set
        var mSettings: SharedPreferences? = null
        @JvmStatic
        val context: Context
            get() = instance.applicationContext
    }
}