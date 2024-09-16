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

    @Query("Select Count(*) From Note Where tag = :tag")
    suspend fun getNotesCountByTag(tag: String): Int


}