package ru.hse.control_system_v2.connection

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.hardware.usb.UsbManager
import android.net.wifi.WifiManager
import ru.hse.control_system_v2.App

// Класс для создания и управления разными типами соединений
class ConnectionFactory private constructor(application: App) {
    var applicationContext: Context

    // Переменная для хранения объекта USBManager
    lateinit var usbManager: UsbManager
    // Переменная для хранения объекта BluetoothManager
    lateinit var bluetoothManager: BluetoothManager
        private set
    // Переменная для хранения объекта WifiManager
    private lateinit var wifiManager: WifiManager
    // Переменная для хранения состояния подключения сервиса
    var isServiceConnecting = false
    // Переменная для хранения состояния подключения активности
    var isActivityConnection = true
        private set
    // Свойство для проверки включен ли Bluetooth на устройстве
    val isBtEnabled: Boolean
        get() = (bluetoothManager.adapter.state == BluetoothAdapter.STATE_ON
                || bluetoothManager.adapter.state == BluetoothAdapter.STATE_TURNING_ON)
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

    // Метод для установки состояния подключения активности
    fun setActivityConnectionState(connecting: Boolean) {
        isActivityConnection = connecting
    }

    // Инициализация переменных контекста и менеджеров соединений

   init {
        applicationContext = application.applicationContext
        bluetoothManager = applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
       usbManager = App.context.getSystemService(Context.USB_SERVICE) as UsbManager
    }

    companion object {
        // Переменная для хранения единственного экземпляра класса ConnectionFactory
        private lateinit var instance: ConnectionFactory

        // Свойство для получения экземпляра класса ConnectionFactory
        val connectionFactory: ConnectionFactory
            get() {
                // Синхронизация по классу для избежания гонки потоков при создании экземпляра класса ConnectionFactory
                if (!::instance.isInitialized) {
                    synchronized(ConnectionFactory::class.java) {
                        instance = ConnectionFactory(App.instance)
                    }
                }
                return instance
            }
    }
}