package com.reysl.uroboros.data.db.note_db

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reysl.uroboros.MainApplication
import com.reysl.uroboros.NoteRepository
import com.reysl.uroboros.data.Note
import com.reysl.uroboros.data.Tag
import com.reysl.uroboros.scheduleReminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.Date

class NoteViewModel : ViewModel() {
    private val repository: NoteRepository
    val noteDao = MainApplication.noteDatabase.getNoteDao()
    val tagDao = MainApplication.tagDatabase.getTagDao()
    val noteList: LiveData<List<Note>>

    init {
        val noteDao = noteDao
        repository = NoteRepository(noteDao)
        noteList = repository.getAllNotes()
    }

    fun addNote(title: String, description: String, tag: String, markdownText: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                tagDao.addTag(Tag(tag = tag))
                val noteId = noteDao.addNote(
                    Note(
                        title = title,
                        description = description,
                        isFavourite = false,
                        tag = tag,
                        styledText = markdownText,
                        time = Date.from(Instant.now())
                    )
                )
                val forgettingCurveIntervals = listOf(1L, 3L, 7L, 14L)
                var cumulativeDelay = 0L
                forgettingCurveIntervals.forEach { days ->
                    cumulativeDelay += days
                    scheduleReminder(context, noteId, title, markdownText, cumulativeDelay)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Не получилось добавить материал", Toast.LENGTH_SHORT).show()
                }
            }


        }
    }

    fun deleteAndCleaning(note: Note, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val tag = note.tag
                noteDao.deleteNote(note.id)

                val count = noteDao.getNotesCountByTag(tag)
                if (count == 0) {
                    tagDao.deleteTagByName(tag)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Не получилось удалить материал", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    fun searchNote(title: String): LiveData<List<Note>> {
        return if (title.isNotEmpty()) {
            noteDao.searchNotesByTitle(title)
        } else {
            noteDao.getAllNote()
        }
    }

    fun filterNotesByTag(tag: String): LiveData<List<Note>> {
        return noteDao.getNotesByTag(tag)
    }

    fun toggleFavourite(note: Note) {
        val updatedNote = note.copy(isFavourite = !note.isFavourite)
        updateNoteInDatabase(updatedNote)
    }

    private fun updateNoteInDatabase(note: Note) {
        viewModelScope.launch {
            repository.update(note)
        }
    }

    fun getFavouriteMaterials(isFavourite: Boolean): LiveData<List<Note>> {
        return repository.getFavouriteMaterials(isFavourite)
    }

}