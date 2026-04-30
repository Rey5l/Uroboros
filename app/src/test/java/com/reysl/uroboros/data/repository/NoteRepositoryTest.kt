package com.reysl.uroboros.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.reysl.uroboros.data.Note
import com.reysl.uroboros.data.db.note_db.NoteDao
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
class NoteRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var noteDao: NoteDao

    private lateinit var repository: NoteRepository

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
        repository = NoteRepository(noteDao)
    }

    @Test
    fun `getAllNotes returns LiveData from DAO`() {
        val expectedNotes = MutableLiveData<List<Note>>()
        expectedNotes.value = listOf(testNote)

        `when`(noteDao.getAllNote()).thenReturn(expectedNotes)

        val result = repository.getAllNotes()

        assertEquals(expectedNotes.value, result.value)
        verify(noteDao).getAllNote()
    }

    @Test
    fun `update calls DAO updateNote`() = runTest {
        val updatedNote = testNote.copy(title = "Updated Title")

        repository.update(updatedNote)

        verify(noteDao).updateNote(updatedNote)
    }

    @Test
    fun `getFavouriteMaterials returns favourite notes`() {
        val favouriteNotes = MutableLiveData<List<Note>>()
        favouriteNotes.value = listOf(testNote.copy(isFavourite = true))

        `when`(noteDao.getFavouriteNotes(true)).thenReturn(favouriteNotes)

        val result = repository.getFavouriteMaterials(true)

        assertEquals(favouriteNotes.value, result.value)
        verify(noteDao).getFavouriteNotes(true)
    }

    @Test
    fun `getFavouriteMaterials returns non-favourite notes`() {
        val nonFavouriteNotes = MutableLiveData<List<Note>>()
        nonFavouriteNotes.value = listOf(testNote)

        `when`(noteDao.getFavouriteNotes(false)).thenReturn(nonFavouriteNotes)

        val result = repository.getFavouriteMaterials(false)

        assertEquals(nonFavouriteNotes.value, result.value)
        verify(noteDao).getFavouriteNotes(false)
    }
}
