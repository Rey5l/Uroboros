package com.reysl.uroboros.notification

import android.content.Context
import androidx.work.WorkManager
import com.reysl.uroboros.data.Note
import com.reysl.uroboros.data.preferences.ReminderIntervals
import com.reysl.uroboros.data.preferences.ReminderTime
import com.reysl.uroboros.data.preferences.ReminderTimeCalculator
import com.reysl.uroboros.data.preferences.UserPreferences

object ReminderScheduler {

    fun intervalsFrom(context: Context): List<Int> =
        UserPreferences(context.applicationContext).getReminderIntervals()

    fun reminderTimeFrom(context: Context): ReminderTime =
        UserPreferences(context.applicationContext).getReminderTime()

    fun scheduleForNote(
        context: Context,
        noteId: Long,
        title: String,
        content: String,
        noteTag: String,
        baseTimeMillis: Long = System.currentTimeMillis(),
        intervalsDays: List<Int>? = null,
        reminderTime: ReminderTime? = null,
    ) {
        val appContext = context.applicationContext
        val intervals = intervalsDays ?: intervalsFrom(appContext)
        val time = reminderTime ?: reminderTimeFrom(appContext)
        val now = System.currentTimeMillis()

        intervals.forEach { days ->
            val targetMs = ReminderTimeCalculator.targetMillis(
                baseTimeMillis = baseTimeMillis,
                intervalDays = days,
                reminderTime = time,
            )
            val delayMs = targetMs - now
            if (delayMs > 0) {
                scheduleReminder(
                    context = context,
                    noteId = noteId,
                    noteTitle = title,
                    noteContent = content,
                    noteTag = noteTag,
                    delayMillis = delayMs,
                )
            }
        }
    }

    fun rescheduleAll(
        context: Context,
        notes: List<Note>,
        intervals: List<Int> = intervalsFrom(context),
        reminderTime: ReminderTime = reminderTimeFrom(context),
    ) {
        val workManager = WorkManager.getInstance(context)
        val normalizedIntervals = ReminderIntervals.normalize(intervals)
        notes.forEach { note ->
            workManager.cancelAllWorkByTag("Reminder_${note.id}")
            scheduleForNote(
                context = context,
                noteId = note.id,
                title = note.title,
                content = note.styledText,
                noteTag = note.tag,
                baseTimeMillis = note.time.time,
                intervalsDays = normalizedIntervals,
                reminderTime = reminderTime,
            )
        }
    }
}
