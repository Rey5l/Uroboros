package com.reysl.uroboros.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.reysl.uroboros.components.MainApplication
import com.reysl.uroboros.data.repository.NoteRepository
import com.reysl.uroboros.data.Note
import com.reysl.uroboros.data.Tag
import com.reysl.uroboros.notification.ReminderScheduler
import com.reysl.uroboros.utils.NotesBackup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.Date

class NoteViewModel : ViewModel() {
    private val repository: NoteRepository
    val noteDao = MainApplication.noteDatabase.getNoteDao()
    val tagDao = MainApplication.tagDatabase.getTagDao()

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    val noteList: LiveData<List<Note>>
    val totalNotesCount: LiveData<Int>
    val favouriteNotesCount: LiveData<Int>
    val uniqueTagsCount: LiveData<Int>
    val recentNotesCount: LiveData<Int>

    init {
        val noteDao = noteDao
        repository = NoteRepository(noteDao)

        noteList = repository.getAllNotes()
        noteList.observeForever {
            _isLoading.value = false
        }

        totalNotesCount = noteDao.getTotalNotesCount()
        favouriteNotesCount = noteDao.getFavouriteNotesCount()
        uniqueTagsCount = noteDao.getUniqueTagsCount()

        val sevenDaysAgo = Date.from(Instant.now().minusSeconds(7 * 24 * 60 * 60))
        recentNotesCount = noteDao.getNotesCountSince(sevenDaysAgo.time)
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

                scheduleReminders(context, noteId, title, markdownText, tag)
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
                MainApplication.noteDatabase.getMaterialQuizDao().deleteByNoteId(note.id)

                val count = noteDao.getNotesCountByTag(tag)
                if (count == 0) {
                    tagDao.deleteTagByName(tag)
                }

                val workManager = WorkManager.getInstance(context)
                workManager.cancelAllWorkByTag("Reminder_${note.id}")

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Материал успешно удалён", Toast.LENGTH_SHORT).show()
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

    suspend fun createBackupJson(): String = withContext(Dispatchers.IO) {
        val notes = noteDao.getAllNotesSync()
        val tags = tagDao.getAllTagsSync().map { it.tag }
        NotesBackup.toJson(notes, tags)
    }

    suspend fun getNotesCount(): Int = withContext(Dispatchers.IO) {
        noteDao.getAllNotesSync().size
    }

    fun importBackup(
        json: String,
        replaceExisting: Boolean,
        context: Context,
        onComplete: (Result<Int>) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val backup = NotesBackup.fromJson(json)
                if (replaceExisting) {
                    clearAllNotes(context)
                }
                backup.tags.forEach { tagName ->
                    tagDao.addTag(Tag(tag = tagName))
                }
                var importedCount = 0
                backup.notes.forEach { note ->
                    val noteId = noteDao.addNote(note.copy(id = 0))
                    scheduleReminders(
                        context = context,
                        noteId = noteId,
                        title = note.title,
                        content = note.styledText,
                        noteTag = note.tag,
                        baseTimeMillis = note.time.time,
                    )
                    importedCount++
                }
                withContext(Dispatchers.Main) {
                    onComplete(Result.success(importedCount))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onComplete(Result.failure(e))
                }
            }
        }
    }

    private suspend fun clearAllNotes(context: Context) {
        val existingNotes = noteDao.getAllNotesSync()
        val workManager = WorkManager.getInstance(context)
        existingNotes.forEach { note ->
            workManager.cancelAllWorkByTag("Reminder_${note.id}")
        }
        noteDao.deleteAllNotes()
        tagDao.deleteAllTags()
    }

    private fun scheduleReminders(
        context: Context,
        noteId: Long,
        title: String,
        content: String,
        noteTag: String,
        baseTimeMillis: Long = System.currentTimeMillis(),
    ) {
        ReminderScheduler.scheduleForNote(
            context = context,
            noteId = noteId,
            title = title,
            content = content,
            noteTag = noteTag,
            baseTimeMillis = baseTimeMillis,
        )
    }

}