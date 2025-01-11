package com.reysl.uroboros.notification

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

fun scheduleReminder(
    context: Context,
    noteId: Long,
    noteTitle: String,
    noteContent: String,
    noteTag: String,
    daysUntilReminder: Long
) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
        .setRequiresBatteryNotLow(false)
        .build()

    val reminderRequest: WorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
        .setInitialDelay(daysUntilReminder, TimeUnit.DAYS)
        .setConstraints(constraints)
        .setInputData(
            workDataOf(
                "note_id" to noteId,
                "note_title" to noteTitle,
                "note_content" to noteContent,
                "note_tag" to noteTag
            )
        )
        .addTag("Reminder_$noteId")
        .build()

    WorkManager.getInstance(context).enqueue(reminderRequest)
}
