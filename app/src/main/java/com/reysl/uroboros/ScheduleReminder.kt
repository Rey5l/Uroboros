package com.reysl.uroboros

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

fun scheduleReminder(
    context: Context,
    noteId: Long,
    title: String,
    content: String,
    daysUntilReminder: Long
) {
    val reminderRequest: WorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>().setInitialDelay(
        daysUntilReminder,
        TimeUnit.DAYS
    ).setInputData(
        workDataOf(
            "note_id" to noteId,
            "note_title" to title,
            "note_content" to content
        )
    ).build()

    WorkManager.getInstance(context).enqueue(reminderRequest)
}