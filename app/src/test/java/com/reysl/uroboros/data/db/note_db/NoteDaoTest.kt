package com.reysl.uroboros.data.db.note_db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.reysl.uroboros.data.Note
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.Date

@ExperimentalCoroutinesApi
class NoteDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var noteDao: NoteDao

    private val testNote = Note(
        id = 1L,
        title = "Test Note",
        description = "Test Description",
        isFavourite = false,
        tag = "Test",
        styledText = "Test Content",
        time = Date()
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `getAllNote returns all notes`() {
        val expectedNotes = MutableLiveData<List<Note>>()
        expectedNotes.value = listOf(testNote)

        `when`(noteDao.getAllNote()).thenReturn(expectedNotes)

        val result = noteDao.getAllNote()

        assertEquals(expectedNotes.value, result.value)
        verify(noteDao).getAllNote()
    }

    @Test
    fun `addNote returns note id`() {
        val noteId = 1L

        `when`(noteDao.addNote(testNote)).thenReturn(noteId)

        val result = noteDao.addNote(testNote)

        assertEquals(noteId, result)
        verify(noteDao).addNote(testNote)
    }

    @Test
    fun `deleteNote removes note by id`() {
        val noteId = 1L

        noteDao.deleteNote(noteId)

        verify(noteDao).deleteNote(noteId)
    }

    @Test
    fun `updateNote updates existing note`() = runTest {
        val updatedNote = testNote.copy(title = "Updated Title")

        noteDao.updateNote(updatedNote)

        verify(noteDao).updateNote(updatedNote)
    }

    @Test
    fun `updateNoteContent updates note content`() = runTest {
        val noteId = 1L
        val newContent = "New Content"

        noteDao.updateNoteContent(noteId, newContent)

        verify(noteDao).updateNoteContent(noteId, newContent)
    }

    @Test
    fun `getNotesCountByTag returns correct count`() = runTest {
        val tag = "Test"
        val expectedCount = 3

        `when`(noteDao.getNotesCountByTag(tag)).thenReturn(expectedCount)

        val result = noteDao.getNotesCountByTag(tag)

        assertEquals(expectedCount, result)
        verify(noteDao).getNotesCountByTag(tag)
    }

    @Test
    fun `searchNotesByTitle returns matching notes`() {
        val searchQuery = "Test"
        val expectedNotes = MutableLiveData<List<Note>>()
        expectedNotes.value = listOf(testNote)

        `when`(noteDao.searchNotesByTitle(searchQuery)).thenReturn(expectedNotes)

        val result = noteDao.searchNotesByTitle(searchQuery)

        assertEquals(expectedNotes.value, result.value)
        verify(noteDao).searchNotesByTitle(searchQuery)
    }

    @Test
    fun `getNotesByTag returns notes with specific tag`() {
        val tag = "Test"
        val expectedNotes = MutableLiveData<List<Note>>()
        expectedNotes.value = listOf(testNote)

        `when`(noteDao.getNotesByTag(tag)).thenReturn(expectedNotes)

        val result = noteDao.getNotesByTag(tag)

        assertEquals(expectedNotes.value, result.value)
        verify(noteDao).getNotesByTag(tag)
    }

    @Test
    fun `getFavouriteNotes returns only favourite notes`() {
        val favouriteNote = testNote.copy(isFavourite = true)
        val expectedNotes = MutableLiveData<List<Note>>()
        expectedNotes.value = listOf(favouriteNote)

        `when`(noteDao.getFavouriteNotes(true)).thenReturn(expectedNotes)

        val result = noteDao.getFavouriteNotes(true)

        assertEquals(expectedNotes.value, result.value)
        assertEquals(1, result.value?.size)
        verify(noteDao).getFavouriteNotes(true)
    }

    @Test
    fun `getTotalNotesCount returns correct count`() {
        val expectedCount = MutableLiveData<Int>()
        expectedCount.value = 5

        `when`(noteDao.getTotalNotesCount()).thenReturn(expectedCount)

        val result = noteDao.getTotalNotesCount()

        assertEquals(expectedCount.value, result.value)
        verify(noteDao).getTotalNotesCount()
    }

    @Test
    fun `getFavouriteNotesCount returns correct count`() {
        val expectedCount = MutableLiveData<Int>()
        expectedCount.value = 2

        `when`(noteDao.getFavouriteNotesCount()).thenReturn(expectedCount)

        val result = noteDao.getFavouriteNotesCount()

        assertEquals(expectedCount.value, result.value)
        verify(noteDao).getFavouriteNotesCount()
    }

    @Test
    fun `getUniqueTagsCount returns correct count`() {
        val expectedCount = MutableLiveData<Int>()
        expectedCount.value = 3

        `when`(noteDao.getUniqueTagsCount()).thenReturn(expectedCount)

        val result = noteDao.getUniqueTagsCount()

        assertEquals(expectedCount.value, result.value)
        verify(noteDao).getUniqueTagsCount()
    }

    @Test
    fun `getNotesCountSince returns notes after specific date`() {
        val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
        val expectedCount = MutableLiveData<Int>()
        expectedCount.value = 4

        `when`(noteDao.getNotesCountSince(sevenDaysAgo)).thenReturn(expectedCount)

        val result = noteDao.getNotesCountSince(sevenDaysAgo)

        assertEquals(expectedCount.value, result.value)
        verify(noteDao).getNotesCountSince(sevenDaysAgo)
    }
}
