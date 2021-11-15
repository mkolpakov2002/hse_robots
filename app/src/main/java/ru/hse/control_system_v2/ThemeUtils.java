package ru.hse.control_system_v2;

import static android.content.Context.MODE_PRIVATE;

import static ru.hse.control_system_v2.Constants.THEMES_LIST;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
                break;
            case "Dark":
                activity.setTheme(R.style.AppTheme_Dark);
                break;
            case "Rena":
                activity.setTheme(R.style.AppTheme_Rena);
                break;
            case "Omelette":
                activity.setTheme(R.style.AppTheme_Omelette);
                break;
            case "Pixel":
                activity.setTheme(R.style.AppTheme_Pixel);
                break;
            case "FDroid":
                activity.setTheme(R.style.AppTheme_FDroid);
                break;
            case "Dark2":
                activity.setTheme(R.style.AppTheme_Dark2);
                break;
            case "Gold":
                activity.setTheme(R.style.AppTheme_Gold);
                break;
            case "RenaLight":
                activity.setTheme(R.style.AppTheme_RenaLight);
                break;
            case "Mint":
                activity.setTheme(R.style.AppTheme_Mint);
                break;
            case "FDroidDark":
                activity.setTheme(R.style.AppTheme_FDroidDark);
                break;
        }
    }
}
