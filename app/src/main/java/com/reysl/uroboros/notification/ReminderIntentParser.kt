package com.reysl.uroboros.notification

import android.content.Intent

object ReminderIntentParser {

    const val ACTION_OPEN_NOTE = "OPEN_NOTE"
    const val EXTRA_NOTE_ID = "note_id"

    fun isOpenNoteIntent(intent: Intent?): Boolean =
        intent?.action == ACTION_OPEN_NOTE

    fun parseNoteId(intent: Intent?): Long? {
        if (!isOpenNoteIntent(intent)) return null
        val noteId = intent?.getLongExtra(EXTRA_NOTE_ID, -1L) ?: -1L
        return noteId.takeIf { it != -1L }
    }
}
