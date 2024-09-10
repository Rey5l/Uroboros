package com.reysl.uroboros.data.db.note_db

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reysl.uroboros.MainApplication
import com.reysl.uroboros.data.Note
import com.reysl.uroboros.data.Tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.time.Instant

class NoteViewModel : ViewModel() {
    val noteDao = MainApplication.noteDatabase.getNoteDao()
    val tagDao = MainApplication.tagDatabase.getTagDao()
    val noteList: LiveData<List<Note>> = noteDao.getAllNote()

    fun addNote(title: String, description: String, tag: String, markdownText: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                tagDao.addTag(Tag(tag = tag))
                noteDao.addNote(
                    Note(
                        title = title,
                        description = description,
                        isFavourite = false,
                        tag = tag,
                        styledText = markdownText,
                        time = Date.from(Instant.now())
                    )
                )
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
                    Toast.makeText(context, "Не получилось добавить материал", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    fun updateNoteTag(note: Note, newTag: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val oldTag = note.tag
            noteDao.updateNote(note.copy(tag = newTag))
            val oldTagCount = noteDao.getNotesCountByTag(oldTag)
            if (oldTagCount == 0) {
                tagDao.deleteTagByName(oldTag)
            }
            tagDao.addTag(Tag(tag = newTag))
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.deleteNote(id)
        }
    }

}