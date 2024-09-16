package com.reysl.uroboros

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val noteId = inputData.getLong("note_id", -1)
        val noteTitle = inputData.getString("note_title") ?: "Повторение материала"
        val noteContent = inputData.getString("note_content") ?: "Повторите загруженный материал"

        if (noteId != -1L) {
            sendNotification(noteId, noteTitle, noteContent)
        }

        return Result.success()
    }

    private fun sendNotification(noteId: Long, title: String, message: String) {
        val channelId = "ReminderChannel"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            action = "OPEN_NOTE"
            putExtra("note_id", noteId)
            putExtra("note_title", title)
            putExtra("note_content", message)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Повторите загруженный материал")
            .setContentText("Пора повторить материал: \"$title\"")
            .setSmallIcon(R.drawable.uroboros_underline)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Уведомления для повторения материала",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(noteId.toInt(), notification)
    }
}