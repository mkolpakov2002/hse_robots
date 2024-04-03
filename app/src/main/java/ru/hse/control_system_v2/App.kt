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

class App : Application() {

    val token = "y0_AgAEA7qkJBRwAAtNHQAAAAD7NOpOAABZXzInfHtFAoIVc4SUjPlw0bda8g"

    override fun onCreate() {
        super.onCreate()
        instance = this
        mSettings = PreferenceManager.getDefaultSharedPreferences(
            instance
        )
    }

    companion object {
        lateinit var instance: App
            private set
        var mSettings: SharedPreferences? = null
        @JvmStatic
        val context: Context
            get() = instance.applicationContext
    }
}