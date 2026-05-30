package com.reysl.uroboros.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.reysl.uroboros.components.MainApplication
import com.reysl.uroboros.data.preferences.ReminderIntervals
import com.reysl.uroboros.data.preferences.ReminderTime
import com.reysl.uroboros.data.preferences.StartTab
import com.reysl.uroboros.data.preferences.ThemeMode
import com.reysl.uroboros.data.preferences.UserPreferences
import com.reysl.uroboros.notification.ReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = UserPreferences(application)

    private val _themeMode = MutableStateFlow(preferences.getThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _reminderIntervals = MutableStateFlow(preferences.getReminderIntervals())
    val reminderIntervals: StateFlow<List<Int>> = _reminderIntervals.asStateFlow()

    private val _reminderTime = MutableStateFlow(preferences.getReminderTime())
    val reminderTime: StateFlow<ReminderTime> = _reminderTime.asStateFlow()

    private val _startTab = MutableStateFlow(preferences.getStartTab())
    val startTab: StateFlow<StartTab> = _startTab.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        preferences.setThemeMode(mode)
        _themeMode.value = mode
    }

    fun setStartTab(tab: StartTab) {
        preferences.setStartTab(tab)
        _startTab.value = tab
    }

    fun updateReminderIntervals(
        days: List<Int>,
        context: Context,
        onComplete: (Result<Unit>) -> Unit = {},
    ) {
        val normalized = ReminderIntervals.normalize(days)
        preferences.setReminderIntervals(normalized)
        _reminderIntervals.value = normalized
        rescheduleAllReminders(context, onComplete)
    }

    fun updateReminderTime(
        time: ReminderTime,
        context: Context,
        onComplete: (Result<Unit>) -> Unit = {},
    ) {
        preferences.setReminderTime(time)
        _reminderTime.value = time
        rescheduleAllReminders(context, onComplete)
    }

    fun resetReminderIntervals(context: Context, onComplete: (Result<Unit>) -> Unit = {}) {
        updateReminderIntervals(ReminderIntervals.DEFAULT, context, onComplete)
    }

    private fun rescheduleAllReminders(
        context: Context,
        onComplete: (Result<Unit>) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val notes = MainApplication.noteDatabase.getNoteDao().getAllNotesSync()
                ReminderScheduler.rescheduleAll(context, notes)
            }.fold(
                onSuccess = { withContext(Dispatchers.Main) { onComplete(Result.success(Unit)) } },
                onFailure = { withContext(Dispatchers.Main) { onComplete(Result.failure(it)) } },
            )
        }
    }
}
