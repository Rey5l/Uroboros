package com.reysl.uroboros

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.reysl.uroboros.notification.NotificationPermission
import com.reysl.uroboros.notification.ReminderIntentParser
import com.reysl.uroboros.ui.theme.LocalThemeMode
import com.reysl.uroboros.ui.theme.UroborosTheme
import com.reysl.uroboros.view.components.NotificationPermissionPrompt
import com.reysl.uroboros.view.navigation.AppNavigation
import com.reysl.uroboros.viewmodel.NoteViewModel
import com.reysl.uroboros.viewmodel.ReminderNavigationViewModel
import com.reysl.uroboros.viewmodel.SettingsViewModel
import com.reysl.uroboros.viewmodel.ShareImportViewModel

class MainActivity : ComponentActivity() {

    private val noteViewModel: NoteViewModel by viewModels()
    private val shareImportViewModel: ShareImportViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val reminderNavigationViewModel: ReminderNavigationViewModel by viewModels()

    private var showNotificationPrompt by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel()
        handleAppIntent(intent)

        setContent {
            val themeMode by settingsViewModel.themeMode.collectAsState()
            CompositionLocalProvider(LocalThemeMode provides themeMode) {
                UroborosTheme {
                    NotificationPermissionPrompt(
                        visible = showNotificationPrompt,
                        onDismiss = { showNotificationPrompt = false },
                        onPermissionChanged = { updateNotificationPromptVisibility() },
                    )
                    AppNavigation(
                        noteViewModel = noteViewModel,
                        shareImportViewModel = shareImportViewModel,
                        settingsViewModel = settingsViewModel,
                        reminderNavigationViewModel = reminderNavigationViewModel,
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateNotificationPromptVisibility()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleAppIntent(intent)
    }

    private fun updateNotificationPromptVisibility() {
        showNotificationPrompt = !NotificationPermission.areEnabled(this)
    }

    private fun handleAppIntent(intent: Intent?) {
        if (ReminderIntentParser.isOpenNoteIntent(intent)) {
            reminderNavigationViewModel.handleIntent(intent)
            intent?.action = null
            return
        }
        shareImportViewModel.handleIntent(intent)
        intent?.action = null
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelId = "ReminderChannel"
            val descriptionText = getString(R.string.description_text_notification_channel)
            val channel = NotificationChannel(
                channelId,
                getString(R.string.settings_notifications_channel_name),
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
