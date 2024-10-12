package com.reysl.uroboros.data.db.note_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.reysl.uroboros.data.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM NOTE")
    fun getAllNote(): LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNote(note: Note): Long

    @Query("DELETE FROM NOTE WHERE id = :id")
    fun deleteNote(id: Long)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNote(note: Note)

    @Query("UPDATE Note SET styledText = :newContent WHERE id = :id")
    suspend fun updateNoteContent(id: Long, newContent: String)

    @Query("SELECT Count(*) FROM Note WHERE tag = :tag")
    suspend fun getNotesCountByTag(tag: String): Int

    @Query("SELECT * FROM Note WHERE title LIKE '%' || :title || '%'")
    fun searchNotesByTitle(title: String): LiveData<List<Note>>



}