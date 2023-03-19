package ru.hse.control_system_v2.data.classes.device.model

import android.bluetooth.BluetoothAdapter

/**
 * логика для управления по Bluetooth
 */
interface BluetoothConnectable {
    var bluetoothAddress: String
    val isBluetoothSupported: Boolean
        get() = BluetoothAdapter.checkBluetoothAddress(bluetoothAddress)
}