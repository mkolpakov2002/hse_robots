package ru.hse.control_system_v2.data.classes.device.model

import android.net.InetAddresses

/**
 * логика для управления по WiFi
 */
interface WiFiConnectable {
    var wifiAddress: String
    var port: Int
    val isWiFiSupported: Boolean
        get() = wifiAddress.let { InetAddresses.isNumericAddress(it) }
}