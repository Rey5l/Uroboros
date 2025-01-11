package com.reysl.uroboros

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.google.firebase.FirebaseApp
import com.reysl.uroboros.notification.checkAndRequestNotificationPermission
import com.reysl.uroboros.ui.theme.UroborosTheme
import com.reysl.uroboros.view.navigation.AppNavigation
import com.reysl.uroboros.viewmodel.AuthViewModel
import com.reysl.uroboros.viewmodel.NoteViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestNotificationPermission(this)
        FirebaseApp.initializeApp(this)
        createNotificationChannel()


        val authViewModel: AuthViewModel by viewModels()
        val noteViewModel: NoteViewModel by viewModels()

        setContent {

            UroborosTheme {
                AppNavigation(
                    authViewModel = authViewModel,
                    noteViewModel = noteViewModel,
                )
            }

        }
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelId = "ReminderChannel"
            val descriptionText = getString(R.string.description_text_notification_channel)
            val channel = NotificationChannel(
                channelId,
                "Уведомления для повторения материала",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}


