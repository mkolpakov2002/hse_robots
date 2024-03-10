package ru.hse.control_system_v2.model.entities

import android.net.InetAddresses

/**
 * логика для управления по WiFi
 */
interface DeviceWiFiConnectable {
    var wifiAddress: String
    var port: Int
    val isWiFiSupported: Boolean
        get() = wifiAddress.let { InetAddresses.isNumericAddress(it) }
}