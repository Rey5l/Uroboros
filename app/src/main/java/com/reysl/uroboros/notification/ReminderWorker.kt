package com.reysl.uroboros.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.reysl.uroboros.MainActivity
import com.reysl.uroboros.R

class ReminderWorker(private val context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val noteId = inputData.getLong("note_id", -1)
        val noteTitle = inputData.getString("note_title") ?: "Повторение материала"

        if (noteId == -1L) {
            Log.e("ReminderWorker", "Invalid noteId provided.")
            return Result.failure()
        }

        sendNotification(context, noteId, noteTitle)
        return Result.success()
    }

    private fun sendNotification(context: Context, noteId: Long, noteTitle: String) {
        val channelId = "ReminderChannel"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            action = ReminderIntentParser.ACTION_OPEN_NOTE
            putExtra(ReminderIntentParser.EXTRA_NOTE_ID, noteId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            noteId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Повторите материал: $noteTitle")
            .setContentText("Повторите загруженный материал")
            .setSmallIcon(R.drawable.uroboros_underline)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(noteId.toInt(), notification)
    }
}
