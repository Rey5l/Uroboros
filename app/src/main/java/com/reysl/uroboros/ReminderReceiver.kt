package com.reysl.uroboros

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import android.app.NotificationManager

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val noteId = intent.getLongExtra("note_id", -1)
        val noteTitle = intent.getStringExtra("note_title") ?: "Повторение материала"
        val noteContent = intent.getStringExtra("note_content") ?: "Повторите загруженный материал"

        if (noteId != -1L) {
            sendNotification(context, noteId, noteTitle, noteContent)
        }
    }

    private fun sendNotification(context: Context, noteId: Long, title: String, message: String) {
        val channelId = "ReminderChannel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Повторите загруженный материал")
            .setContentText("Пора повторить материал: \"$title\"")
            .setSmallIcon(R.drawable.uroboros_underline)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(noteId.toInt(), notification)
    }
}
