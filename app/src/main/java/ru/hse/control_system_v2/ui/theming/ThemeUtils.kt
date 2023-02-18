package ru.hse.control_system_v2.ui.theming

import android.app.Activity
import android.content.res.Configuration
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import ru.hse.control_system_v2.App
import ru.hse.control_system_v2.AppConstants
import ru.hse.control_system_v2.R

object ThemeUtils {
    private var sTheme: String? = null
    @JvmStatic
    fun onActivityCreateSetTheme(activity: Activity) {
        when (activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                activity.setTheme(R.style.AppTheme_Light)
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                activity.setTheme(R.style.AppTheme_Dark)
            }
        }


    }

    @JvmStatic
    val currentTheme: String?
        get() {
            val sPref = PreferenceManager.getDefaultSharedPreferences(App.context)
            return sPref.getString("theme", AppConstants.THEMES_LIST[0])
        }

    @JvmStatic
    fun switchTheme(name: String?) {
//        if (name.equals(THEMES_LIST[1])) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        } else if (name.equals(THEMES_LIST[0])){
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
//        }
    }
}