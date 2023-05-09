package ru.hse.control_system_v2.connection

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.hardware.usb.UsbManager
import android.net.wifi.WifiManager
import ru.hse.control_system_v2.App

// Класс для создания и управления разными типами соединений
object ConnectionFactory {
    private val applicationContext: Context = App.instance.applicationContext
    // Переменная для хранения объекта USBManager
    val usbManager: UsbManager = applicationContext
        .getSystemService(Context.USB_SERVICE) as UsbManager
    // Переменная для хранения объекта BluetoothManager
    val bluetoothManager: BluetoothManager = applicationContext
        .getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    // Переменная для хранения объекта WifiManager
    private val wifiManager: WifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    // Свойство для проверки включен ли Bluetooth на устройстве
    val isBtEnabled: Boolean
        get() = (bluetoothManager.adapter.state == BluetoothAdapter.STATE_ON
                || bluetoothManager.adapter.state == BluetoothAdapter.STATE_TURNING_ON)
    val isNotEmptyBluetoothBounded: Boolean
        get() = bluetoothBounded.isNotEmpty()
    val bluetoothBounded: Set<BluetoothDevice>
        //noinspection MissingPermission
        get() = bluetoothManager.adapter.bondedDevices
    // Свойство для проверки включен ли WiFi на устройстве
    val isWiFiEnabled: Boolean
        get() = (wifiManager.wifiState == WifiManager.WIFI_STATE_ENABLED
                || wifiManager.wifiState == WifiManager.WIFI_STATE_ENABLING)
    // Свойство для проверки поддерживает ли устройство Bluetooth и WiFi
    val isBtWiFiSupported: Boolean
        get() = isBtSupported && isWiFiSupported
    // Свойство для проверки поддерживает ли устройство Bluetooth
    val isBtSupported: Boolean
        get() = bluetoothManager.adapter != null
    // Свойство для проверки поддерживает ли устройство WiFi
    val isWiFiSupported: Boolean
        get() = true

}