package ru.hse.control_system_v2;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.room.Room;

import com.google.android.material.color.DynamicColors;

import java.util.ArrayList;

import ru.hse.control_system_v2.dbprotocol.ProtocolDBHelper;
import ru.hse.control_system_v2.list_devices.DeviceItemType;

public class App extends Application {

    private static App instance;

    private static AppDataBase database;
    private static BluetoothAdapter btAdapter;
    private static WifiManager wifiManager;
    private ProtocolDBHelper protocolDBHelper;
    private static boolean isConnecting = false;
    private static ArrayList<DeviceItemType> devicesList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, AppDataBase.class, "devices")
                .allowMainThreadQueries()
                .build();
        DynamicColors.applyToActivitiesIfAvailable(this);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        protocolDBHelper = new ProtocolDBHelper(this.getApplicationContext());
    }

    public ArrayList<String> getProtocolNames() {
        return protocolDBHelper.getProtocolNames();
    }

    public static App getInstance() {
        return instance;
    }

    public static AppDataBase getDatabase() {
        return database;
    }

    public static Context getContext() {
        return getInstance().getApplicationContext();
    }

    public static boolean isBtEnabled() {
        return btAdapter.getState() == BluetoothAdapter.STATE_ON
                || btAdapter.getState() == BluetoothAdapter.STATE_TURNING_ON;
    }

    public static boolean isWiFiEnabled() {
        Log.e("HSE", String.valueOf(WifiManager.WIFI_STATE_ENABLED));
        return wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED
                || wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING;
    }


    public static boolean isBtWiFiSupported() {
        return btAdapter != null && wifiManager != null;
    }

    public static boolean isBtSupported() {
        return btAdapter != null;
    }

    public static boolean isWiFiSupported() {
        return wifiManager != null;
    }

    public static BluetoothAdapter getBtAdapter() {
        return btAdapter;
    }

    public static void setConnecting(boolean connecting) {
        isConnecting = connecting;
    }

    public static boolean isIsConnecting(){return isConnecting;}

    public static synchronized ArrayList<DeviceItemType> getDevicesList() {
        return devicesList;
    }

    public static synchronized void setDevicesList(ArrayList<DeviceItemType> newDevicesList) {
        devicesList = newDevicesList;
    }
}