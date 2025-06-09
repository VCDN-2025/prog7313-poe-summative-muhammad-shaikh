package com.budgetbuddy

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

/**
 * Utility object to manage light/dark theme switching using SharedPreferences.
 */
object ThemeUtils {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_DARK_MODE = "dark_mode"

    // Checks if dark mode is enabled
    fun isDarkMode(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }

    // Applies the saved theme preference
    fun applyTheme(context: Context) {
        val darkMode = isDarkMode(context)
        AppCompatDelegate.setDefaultNightMode(
            if (darkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    // Toggles the theme and saves preference
    fun toggleTheme(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isDark = isDarkMode(context)
        prefs.edit().putBoolean(KEY_DARK_MODE, !isDark).apply()
        applyTheme(context)
    }
}
