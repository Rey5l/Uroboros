package com.reysl.uroboros

import androidx.lifecycle.LiveData
import com.reysl.uroboros.data.Note
import com.reysl.uroboros.data.db.note_db.NoteDao

class NoteRepository(private val noteDao: NoteDao) {

    fun getAllNotes(): LiveData<List<Note>> {
        return noteDao.getAllNote()
    }

    suspend fun update(note: Note) {
        noteDao.updateNote(note)
    }

    fun getFavouriteMaterials(isFavourite: Boolean): LiveData<List<Note>> {
        return noteDao.getFavouriteNotes(isFavourite)
    }
}