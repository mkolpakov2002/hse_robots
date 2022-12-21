package ru.hse.control_system_v2;

import static ru.hse.control_system_v2.Constants.APP_PREFERENCES_NUMBER_COMMAND_FIRST_CHAR;
import static ru.hse.control_system_v2.Constants.APP_PREFERENCES_NUMBER_COMMAND_LAST_CHAR;
import static ru.hse.control_system_v2.Constants.APP_PREFERENCES_STRING_COMMAND_FIRST_CHAR;
import static ru.hse.control_system_v2.Constants.APP_PREFERENCES_STRING_COMMAND_LAST_CHAR;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.preference.PreferenceManager;
import androidx.room.Room;

import java.util.ArrayList;

import ru.hse.control_system_v2.data.AppDataBase;
import ru.hse.control_system_v2.data.ProtocolDBHelper;
import ru.hse.control_system_v2.data.DeviceItemType;

public class App extends Application {

    private static App instance;

    private static AppDataBase database;
    private static BluetoothAdapter btAdapter;
    private static WifiManager wifiManager;
    private ProtocolDBHelper protocolDBHelper;
    private static boolean isConnecting = false;
    private static boolean connectionState = true;
    private static ArrayList<DeviceItemType> devicesList = new ArrayList<>();
    static SharedPreferences mSettings;
    static String numberCommandFirstChar;
    static String numberCommandLastChar;
    static String stringCommandFirstChar;
    static String stringCommandLastChar;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, AppDataBase.class, "devices")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        protocolDBHelper = new ProtocolDBHelper(this.getApplicationContext());
        mSettings = PreferenceManager.getDefaultSharedPreferences(App.getInstance());

        numberCommandFirstChar = (mSettings.getString(APP_PREFERENCES_NUMBER_COMMAND_FIRST_CHAR, ""));
        numberCommandLastChar = (mSettings.getString(APP_PREFERENCES_NUMBER_COMMAND_LAST_CHAR, ""));
        stringCommandFirstChar = (mSettings.getString(APP_PREFERENCES_STRING_COMMAND_FIRST_CHAR, ""));
        stringCommandLastChar = (mSettings.getString(APP_PREFERENCES_STRING_COMMAND_LAST_CHAR, ""));

    }

    public static String getNumberCommandFirstChar() {
        return numberCommandFirstChar;
    }

    public static String getNumberCommandLastChar() {
        return numberCommandLastChar;
    }

    public static String getStringCommandFirstChar() {
        return stringCommandFirstChar;
    }

    public static String getStringCommandLastChar() {
        return stringCommandLastChar;
    }

    public static void updateDataParams(String numberCommandFirstChar, String numberCommandLastChar, String stringCommandFirstChar, String stringCommandLastChar){
        mSettings = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        SharedPreferences.Editor editor = mSettings.edit();

        App.numberCommandFirstChar = numberCommandFirstChar;
        App.numberCommandLastChar = numberCommandLastChar;
        App.stringCommandFirstChar = stringCommandFirstChar;
        App.stringCommandLastChar = stringCommandLastChar;
        editor.putString(APP_PREFERENCES_NUMBER_COMMAND_FIRST_CHAR, numberCommandFirstChar);
        editor.putString(APP_PREFERENCES_NUMBER_COMMAND_LAST_CHAR, numberCommandLastChar);
        editor.putString(APP_PREFERENCES_STRING_COMMAND_FIRST_CHAR, stringCommandFirstChar);
        editor.putString(APP_PREFERENCES_STRING_COMMAND_LAST_CHAR, stringCommandLastChar);
        editor.apply();
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

    public static void setServiceConnecting(boolean connecting) {
        isConnecting = connecting;
    }

    public static boolean isServiceConnecting(){return isConnecting;}

    public static void setActivityConnectionState(boolean connecting) {
        connectionState = connecting;
    }

    public static boolean isActivityConnection(){return connectionState;}

    public static synchronized ArrayList<DeviceItemType> getDevicesList() {
        return devicesList;
    }

    public static synchronized void setDevicesList(ArrayList<DeviceItemType> newDevicesList) {
        devicesList = newDevicesList;
    }
}