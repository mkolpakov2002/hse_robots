package ru.hse.control_system_v2

import java.util.UUID

object AppConstants {
    const val DATABASE_NAME = "device"
    const val PROTO_DATABASE_NAME = "protocols"
    const val BUTTON_ITEM_TYPE = 0
    const val DEVICE_ITEM_TYPE = 1
    const val TAG = "HSE_GCS"
    val APP_BLUETOOTH_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    const val REPO_LINK = "https://github.com/mkolpakov2002/hse_robots"
    const val APP_LOG_TAG = "HSE_Robots"
    val THEMES_LIST = arrayOf("Light", "Dark")
    val THEMES_LIST_ANDROID_S = arrayOf("System", "Light", "Dark")
    val CONNECTION_LIST = arrayOf("Bluetooth", "IP")

    const val DEFAULT_DEVICE_NAME = ""
    val DEVICE_UI_TYPE_LIST = arrayListOf(
        "type_sphere",
        "type_anthropomorphic",
        "type_cubbi",
        "type_computer",
        "no_type"
    )
    val DEVICE_UI_CLASS_LIST = arrayListOf(
        "class_android",
        "class_computer",
        "class_arduino",
        "no_class"
    )
    const val DEFAULT_DEVICE_CLASS = "no_class"
    const val DEFAULT_DEVICE_TYPE = "no_type"
    const val DEFAULT_DEVICE_PROTOCOL = "arduino_default"
    val DEVICE_PROTOCOL_ENCRYPTION_LIST = arrayListOf(
        "AES128",
        "AES256",
        "Blowfish",
        "ChaCha20",
        "Salsa20",
        "Kuznechik"
    )
    const val DEFAULT_DEVICE_BLUETOOTH_ADDRESS = ""
    const val DEFAULT_DEVICE_WIFI_ADDRESS = ""
    const val DEFAULT_DEVICE_PORT = 0
    const val DEFAULT_DEVICE_MANUFACTURE = ""
    const val DEFAULT_DEVICE_MODEL = ""
}