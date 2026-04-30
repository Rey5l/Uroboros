package com.reysl.uroboros.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

class NoteTest {

    @Test
    fun `Note creation with all fields`() {
        val date = Date()
        val note = Note(
            id = 1L,
            title = "Test Title",
            description = "Test Description",
            isFavourite = false,
            tag = "TestTag",
            styledText = "Styled content",
            time = date
        )

        assertEquals(1L, note.id)
        assertEquals("Test Title", note.title)
        assertEquals("Test Description", note.description)
        assertFalse(note.isFavourite)
        assertEquals("TestTag", note.tag)
        assertEquals("Styled content", note.styledText)
        assertEquals(date, note.time)
    }

    @Test
    fun `Note copy with modified fields`() {
        val originalNote = Note(
            id = 1L,
            title = "Original",
            description = "Description",
            isFavourite = false,
            tag = "Tag",
            styledText = "Content",
            time = Date()
        )

        val copiedNote = originalNote.copy(title = "Modified", isFavourite = true)

        assertEquals("Modified", copiedNote.title)
        assertTrue(copiedNote.isFavourite)
        assertEquals(originalNote.description, copiedNote.description)
        assertEquals(originalNote.id, copiedNote.id)
    }

    @Test
    fun `Note equality check`() {
        val date = Date()
        val note1 = Note(
            id = 1L,
            title = "Test",
            description = "Desc",
            isFavourite = false,
            tag = "Tag",
            styledText = "Content",
            time = date
        )

        val note2 = Note(
            id = 1L,
            title = "Test",
            description = "Desc",
            isFavourite = false,
            tag = "Tag",
            styledText = "Content",
            time = date
        )

        assertEquals(note1, note2)
    }

    @Test
    fun `Note inequality check`() {
        val note1 = Note(
            id = 1L,
            title = "Test1",
            description = "Desc",
            isFavourite = false,
            tag = "Tag",
            styledText = "Content",
            time = Date()
        )

        val note2 = Note(
            id = 2L,
            title = "Test2",
            description = "Desc",
            isFavourite = false,
            tag = "Tag",
            styledText = "Content",
            time = Date()
        )

        assertNotEquals(note1, note2)
    }

    @Test
    fun `Note with default id`() {
        val note = Note(
            title = "Test",
            description = "Desc",
            isFavourite = false,
            tag = "Tag",
            styledText = "Content",
            time = Date()
        )

        assertEquals(0L, note.id)
    }

    @Test
    fun `Toggle favourite status`() {
        val note = Note(
            id = 1L,
            title = "Test",
            description = "Desc",
            isFavourite = false,
            tag = "Tag",
            styledText = "Content",
            time = Date()
        )

        val toggledNote = note.copy(isFavourite = !note.isFavourite)

        assertFalse(note.isFavourite)
        assertTrue(toggledNote.isFavourite)
    }
}
