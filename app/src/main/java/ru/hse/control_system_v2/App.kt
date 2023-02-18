package ru.hse.control_system_v2

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class App : Application() {
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