package ru.hse.control_system_v2;

import static android.content.Context.MODE_PRIVATE;

import static ru.hse.control_system_v2.Constants.THEMES_LIST;
import static ru.hse.control_system_v2.Constants.THEMES_LIST_ANDROID_S;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.google.android.material.color.DynamicColors;

public class ThemeUtils {
    private static String sTheme;

    public static void changeToTheme(Activity activity) {
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        sTheme = sPref.getString("theme", THEMES_LIST[0]);
        activity.recreate();
    }

    public static void onActivityCreateSetTheme(Activity activity) {
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        sTheme = sPref.getString("theme", THEMES_LIST[0]);
        switch (sTheme) {
            default:
            case "Light":
                activity.setTheme(R.style.AppTheme_Light);
                sTheme = "Light";
                break;
            case "Dark":
                activity.setTheme(R.style.AppTheme_Dark);
                sTheme = "Dark";
                break;
            case "Rena":
                activity.setTheme(R.style.AppTheme_Rena);
                sTheme = "Rena";
                break;
            case "Omelette":
                activity.setTheme(R.style.AppTheme_Omelette);
                sTheme = "Omelette";
                break;
            case "FDroid":
                activity.setTheme(R.style.AppTheme_FDroid);
                sTheme = "FDroid";
                break;
            case "Gold":
                activity.setTheme(R.style.AppTheme_Gold);
                sTheme = "Gold";
                break;
            case "RenaLight":
                activity.setTheme(R.style.AppTheme_RenaLight);
                sTheme = "RenaLight";
                break;
            case "Mint":
                activity.setTheme(R.style.AppTheme_Mint);
                sTheme = "Mint";
                break;
            case "FDroidDark":
                activity.setTheme(R.style.AppTheme_FDroidDark);
                sTheme = "FDroidDark";
                break;
        }

    }

    public static String getCurrentTheme() {
        return sTheme;
    }
}
