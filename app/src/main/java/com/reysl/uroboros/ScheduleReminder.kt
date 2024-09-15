package com.reysl.uroboros

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.concurrent.TimeUnit

fun scheduleReminder(context: Context, daysUntilReminder: Long) {
    val reminderRequest: WorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>().setInitialDelay(
        daysUntilReminder,
        TimeUnit.DAYS
    ).build()

    WorkManager.getInstance(context).enqueue(reminderRequest)
}