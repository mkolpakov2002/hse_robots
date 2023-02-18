package ru.hse.control_system_v2.connection

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.net.wifi.WifiManager
import ru.hse.control_system_v2.App

class ConnectionFactory private constructor(application: App) {
    var applicationContext: Context

    lateinit var btManager: BluetoothManager
        private set
    private lateinit var wifiManager: WifiManager
    var isServiceConnecting = false
    var isActivityConnection = true
        private set
    val isBtEnabled: Boolean
        get() = (btManager.adapter.state == BluetoothAdapter.STATE_ON
                || btManager.adapter.state == BluetoothAdapter.STATE_TURNING_ON)
    val isWiFiEnabled: Boolean
        get() = (wifiManager.wifiState == WifiManager.WIFI_STATE_ENABLED
                || wifiManager.wifiState == WifiManager.WIFI_STATE_ENABLING)
    val isBtWiFiSupported: Boolean
        get() = isBtSupported && isWiFiSupported
    val isBtSupported: Boolean
        get() = btManager.adapter != null
    val isWiFiSupported: Boolean
        get() = true

    fun setActivityConnectionState(connecting: Boolean) {
        isActivityConnection = connecting
    }

    //https://javarush.com/groups/posts/3876-kofe-breyk-143-zapechatannihe-sealed-klassih-v-java-17-4-sposoba-realizacii-singleton
    init {
        applicationContext = application.applicationContext
        btManager = applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    companion object {
        private lateinit var instance: ConnectionFactory

        val connectionFactory: ConnectionFactory
            get() {
                if (!::instance.isInitialized) {
                    synchronized(ConnectionFactory::class.java) {
                        instance = ConnectionFactory(App.instance)
                    }
                }
                return instance
            }
    }
}