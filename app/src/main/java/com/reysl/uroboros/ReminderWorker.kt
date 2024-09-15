package com.reysl.uroboros

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        sendNotification("Время повторить материал!", "Повторите загруженный материал")
        return Result.success()
    }

    private fun sendNotification(title: String, message: String) {
        val channelId = "ReminderChannel"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification =
            NotificationCompat.Builder(applicationContext, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.uroboros_underline)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Уведомления для повторения материала",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(1, notification)
    }
}