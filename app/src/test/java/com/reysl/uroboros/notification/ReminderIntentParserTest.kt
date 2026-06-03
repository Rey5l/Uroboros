package com.reysl.uroboros.notification

import android.content.Intent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ReminderIntentParserTest {

    @Test
    fun `isOpenNoteIntent returns true for OPEN_NOTE action`() {
        val intent = Intent().apply {
            action = ReminderIntentParser.ACTION_OPEN_NOTE
        }
        assertTrue(ReminderIntentParser.isOpenNoteIntent(intent))
    }

    @Test
    fun `isOpenNoteIntent returns false for other action`() {
        assertFalse(ReminderIntentParser.isOpenNoteIntent(Intent(Intent.ACTION_MAIN)))
    }

    @Test
    fun `parseNoteId returns id from valid intent`() {
        val intent = Intent().apply {
            action = ReminderIntentParser.ACTION_OPEN_NOTE
            putExtra(ReminderIntentParser.EXTRA_NOTE_ID, 42L)
        }
        assertEquals(42L, ReminderIntentParser.parseNoteId(intent))
    }

    @Test
    fun `parseNoteId returns null when id missing`() {
        val intent = Intent().apply {
            action = ReminderIntentParser.ACTION_OPEN_NOTE
        }
        assertNull(ReminderIntentParser.parseNoteId(intent))
    }

    @Test
    fun `parseNoteId returns null for non reminder intent`() {
        val intent = Intent().apply {
            putExtra(ReminderIntentParser.EXTRA_NOTE_ID, 10L)
        }
        assertNull(ReminderIntentParser.parseNoteId(intent))
    }
}
