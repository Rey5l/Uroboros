package com.reysl.uroboros.utils

import com.reysl.uroboros.data.Note
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Date

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class NotesBackupTest {

    @Test
    fun `toJson and fromJson preserve notes and tags`() {
        val note = Note(
            id = 5L,
            title = "Kotlin",
            description = "Desc",
            isFavourite = true,
            tag = "Study",
            styledText = "# Title",
            time = Date(1_700_000_000_000L),
        )
        val tags = listOf("Study", "Work")

        val json = NotesBackup.toJson(listOf(note), tags)
        val backup = NotesBackup.fromJson(json)

        assertEquals(1, backup.notes.size)
        assertEquals("Kotlin", backup.notes[0].title)
        assertEquals(true, backup.notes[0].isFavourite)
        assertEquals(tags, backup.tags)
    }

    @Test
    fun `fromJson includes version field`() {
        val json = NotesBackup.toJson(emptyList(), emptyList())
        assertTrue(json.contains("\"version\": ${NotesBackup.VERSION}"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `fromJson rejects unsupported version`() {
        NotesBackup.fromJson("""{"version":999,"notes":[],"tags":[]}""")
    }
}
