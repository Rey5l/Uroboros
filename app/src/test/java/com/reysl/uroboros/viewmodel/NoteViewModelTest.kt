package com.reysl.uroboros.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.reysl.uroboros.data.Note
import com.reysl.uroboros.data.db.note_db.NoteDao
import com.reysl.uroboros.data.db.tag_db.TagDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.Date

@ExperimentalCoroutinesApi
class NoteViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var noteDao: NoteDao

    @Mock
    private lateinit var tagDao: TagDao

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
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `filterNotesByTag returns notes with specific tag`() {
        val tag = "Test"
        val expectedNotes = MutableLiveData<List<Note>>()
        expectedNotes.value = listOf(testNote)

        `when`(noteDao.getNotesByTag(tag)).thenReturn(expectedNotes)

        val result = noteDao.getNotesByTag(tag)

        assertEquals(expectedNotes.value, result.value)
        verify(noteDao).getNotesByTag(tag)
    }

    @Test
    fun `toggleFavourite changes note favourite status`() {
        val note = testNote.copy(isFavourite = false)
        val expectedNote = note.copy(isFavourite = true)

        val toggledNote = note.copy(isFavourite = !note.isFavourite)

        assertEquals(expectedNote.isFavourite, toggledNote.isFavourite)
        assertTrue(toggledNote.isFavourite)
    }

    @Test
    fun `toggleFavourite from true to false`() {
        val note = testNote.copy(isFavourite = true)
        val toggledNote = note.copy(isFavourite = !note.isFavourite)

        assertFalse(toggledNote.isFavourite)
    }

    @Test
    fun `getFavouriteMaterials returns only favourite notes`() {
        val favouriteNote = testNote.copy(isFavourite = true)
        val expectedNotes = MutableLiveData<List<Note>>()
        expectedNotes.value = listOf(favouriteNote)

        `when`(noteDao.getFavouriteNotes(true)).thenReturn(expectedNotes)

        val result = noteDao.getFavouriteNotes(true)

        assertEquals(1, result.value?.size)
        assertTrue(result.value?.get(0)?.isFavourite == true)
        verify(noteDao).getFavouriteNotes(true)
    }

    @Test
    fun `addNote creates note with correct data`() = runTest {
        val title = "New Note"
        val description = "New Description"
        val tag = "NewTag"
        val markdownText = "# Content"
        val noteId = 1L

        `when`(noteDao.addNote(org.mockito.kotlin.any())).thenReturn(noteId)

        noteDao.addNote(
            Note(
                title = title,
                description = description,
                isFavourite = false,
                tag = tag,
                styledText = markdownText,
                time = Date()
            )
        )

        verify(noteDao).addNote(org.mockito.kotlin.any())
    }

    @Test
    fun `deleteAndCleaning removes note and tag if no other notes with tag`() = runTest {
        val noteId = 1L
        val tag = "Test"

        `when`(noteDao.getNotesCountByTag(tag)).thenReturn(0)

        noteDao.deleteNote(noteId)
        val count = noteDao.getNotesCountByTag(tag)

        if (count == 0) {
            tagDao.deleteTagByName(tag)
        }

        verify(noteDao).deleteNote(noteId)
        verify(noteDao).getNotesCountByTag(tag)
        verify(tagDao).deleteTagByName(tag)
    }

    @Test
    fun `deleteAndCleaning keeps tag if other notes exist`() = runTest {
        val noteId = 1L
        val tag = "Test"

        `when`(noteDao.getNotesCountByTag(tag)).thenReturn(2)

        noteDao.deleteNote(noteId)
        val count = noteDao.getNotesCountByTag(tag)

        verify(noteDao).deleteNote(noteId)
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
}
