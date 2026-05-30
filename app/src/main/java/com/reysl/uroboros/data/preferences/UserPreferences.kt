package com.reysl.uroboros.data.preferences

import android.content.Context

class UserPreferences(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getThemeMode(): ThemeMode {
        val value = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)
        return runCatching { ThemeMode.valueOf(value!!) }.getOrDefault(ThemeMode.SYSTEM)
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    fun getReminderIntervals(): List<Int> =
        ReminderIntervals.parse(prefs.getString(KEY_REMINDER_INTERVALS, null))

    fun setReminderIntervals(days: List<Int>) {
        prefs.edit()
            .putString(KEY_REMINDER_INTERVALS, ReminderIntervals.serialize(days))
            .apply()
    }

    fun getReminderTime(): ReminderTime {
        val hour = prefs.getInt(KEY_REMINDER_HOUR, ReminderTime.DEFAULT.hour)
        val minute = prefs.getInt(KEY_REMINDER_MINUTE, ReminderTime.DEFAULT.minute)
        return ReminderTime(
            hour = hour.coerceIn(0, 23),
            minute = minute.coerceIn(0, 59),
        )
    }

    fun setReminderTime(time: ReminderTime) {
        prefs.edit()
            .putInt(KEY_REMINDER_HOUR, time.hour)
            .putInt(KEY_REMINDER_MINUTE, time.minute)
            .apply()
    }

    fun getStartTab(): StartTab {
        val value = prefs.getString(KEY_START_TAB, StartTab.HOME.name)
        return runCatching { StartTab.valueOf(value!!) }.getOrDefault(StartTab.HOME)
    }

    fun setStartTab(tab: StartTab) {
        prefs.edit().putString(KEY_START_TAB, tab.name).apply()
    }

    companion object {
        private const val PREFS_NAME = "uroboros_settings"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_REMINDER_INTERVALS = "reminder_intervals"
        private const val KEY_REMINDER_HOUR = "reminder_hour"
        private const val KEY_REMINDER_MINUTE = "reminder_minute"
        private const val KEY_START_TAB = "start_tab"
    }
}
