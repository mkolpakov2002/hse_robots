package ru.hse.control_system_v2;

import static ru.hse.control_system_v2.Constants.THEMES_LIST;
import static ru.hse.control_system_v2.Constants.THEMES_LIST_ANDROID_S;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;

public class ThemeUtils {
    private static String sTheme;

    public static void onActivityCreateSetTheme(Activity activity) {
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        sTheme = sPref.getString("theme", THEMES_LIST[0]);
        switch (sTheme) {
            case "Light" -> {
                activity.setTheme(R.style.AppTheme_Light);
                sTheme = "Light";
            }
            case "Dark" -> {
                activity.setTheme(R.style.AppTheme_Dark);
                sTheme = "Dark";
            }
            case "Rena" -> {
                activity.setTheme(R.style.AppTheme_Rena);
                sTheme = "Rena";
            }
            case "Omelette" -> {
                activity.setTheme(R.style.AppTheme_Omelette);
                sTheme = "Omelette";
            }
            case "FDroid" -> {
                activity.setTheme(R.style.AppTheme_FDroid);
                sTheme = "FDroid";
            }
            case "Gold" -> {
                activity.setTheme(R.style.AppTheme_Gold);
                sTheme = "Gold";
            }
            case "RenaLight" -> {
                activity.setTheme(R.style.AppTheme_RenaLight);
                sTheme = "RenaLight";
            }
            case "Mint" -> {
                activity.setTheme(R.style.AppTheme_Mint);
                sTheme = "Mint";
            }
            case "FDroidDark" -> {
                activity.setTheme(R.style.AppTheme_FDroidDark);
                sTheme = "FDroidDark";
            }
        }
    }

    public static String getCurrentTheme() {
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        return sPref.getString("theme", THEMES_LIST[0]);
    }

    public static void setTheme(String sTheme, Activity activity) {
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        ThemeUtils.sTheme = sTheme;
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("theme", sTheme);
        ed.apply();
        activity.recreate();
    }
}
