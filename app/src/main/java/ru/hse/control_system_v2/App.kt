package ru.hse.control_system_v2

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
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
            install(ContentNegotiation) {
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                }
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
            expectSuccess = true
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