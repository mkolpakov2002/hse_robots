package ru.hse.control_system_v2;

import java.util.UUID;

public interface AppConstants {
    public static final String DATABASE_NAME = "devices";

    public static final String PROTO_DATABASE_NAME = "protocols";

    String REPO_LINK = "https://github.com/mkolpakov2002/hse_robots";
    String APP_LOG_TAG = "HSE_Robots";
    String[] THEMES_LIST = {"Light", "Dark"};
    String[] THEMES_LIST_ANDROID_S = {"System", "Light", "Dark"};
    public static int BUTTON_ITEM_TYPE = 0;
    public static int DEVICE_ITEM_TYPE = 1;
    public static final String TAG = "HSE_GCS";
    String[] CONNECTION_LIST = {"Bluetooth", "IP"};

    public static UUID APP_BLUETOOTH_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
}
