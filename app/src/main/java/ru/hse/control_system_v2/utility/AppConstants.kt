package ru.hse.control_system_v2.utility

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.UUID

object AppConstants {
    const val DATABASE_NAME = "deviceOld"
    const val APP_LOG_TAG = "HSE_Robots"
    const val DEFAULT_DEVICE_NAME = ""
    const val DEFAULT_DEVICE_CLASS = "no_class"
    const val DEFAULT_DEVICE_TYPE = "no_type"
    const val DEFAULT_DEVICE_PROTOCOL = "arduino_default"
    const val DEFAULT_DEVICE_BLUETOOTH_ADDRESS = ""
    const val DEFAULT_DEVICE_WIFI_ADDRESS = ""
    const val DEFAULT_DEVICE_PORT = 0
    const val DEFAULT_DEVICE_MANUFACTURE = ""
    const val DEFAULT_DEVICE_MODEL = ""

    @RequiresApi(Build.VERSION_CODES.S)
    const val BLUETOOTH_CONNECT_PERMISSION = Manifest.permission.BLUETOOTH_CONNECT
    const val WRITE_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
    const val BLUETOOTH_ADMIN_PERMISSION = Manifest.permission.BLUETOOTH_ADMIN
    const val CHANGE_WIFI_STATE = Manifest.permission.CHANGE_WIFI_STATE
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    const val MANAGE_WIFI_NETWORK_SELECTION = Manifest.permission.MANAGE_WIFI_NETWORK_SELECTION
}