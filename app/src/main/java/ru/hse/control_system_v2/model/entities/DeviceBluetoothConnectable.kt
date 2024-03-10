package ru.hse.control_system_v2.model.entities

import android.bluetooth.BluetoothAdapter

/**
 * логика для управления по Bluetooth
 */
interface DeviceBluetoothConnectable {
    var bluetoothAddress: String
    val isBluetoothSupported: Boolean
        get() = BluetoothAdapter.checkBluetoothAddress(bluetoothAddress)
}