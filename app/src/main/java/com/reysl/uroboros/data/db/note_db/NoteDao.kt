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

    @Query("SELECT * FROM NOTE")
    suspend fun getAllNotesSync(): List<Note>

    @Query("SELECT * FROM NOTE WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: Long): Note?

    @Query("DELETE FROM NOTE")
    suspend fun deleteAllNotes()

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

    @Query("SELECT * FROM Note WHERE tag = :tag")
    fun getNotesByTag(tag: String): LiveData<List<Note>>

    @Query("SELECT * FROM Note WHERE isFavourite = :isFavourite")
    fun getFavouriteNotes(isFavourite: Boolean): LiveData<List<Note>>

    @Query("SELECT COUNT(*) FROM Note")
    fun getTotalNotesCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM Note WHERE isFavourite = 1")
    fun getFavouriteNotesCount(): LiveData<Int>

    @Query("SELECT COUNT(DISTINCT tag) FROM Note")
    fun getUniqueTagsCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM Note WHERE time >= :startDate")
    fun getNotesCountSince(startDate: Long): LiveData<Int>

    @Query("SELECT tag, COUNT(*) as count FROM Note GROUP BY tag ORDER BY count DESC LIMIT 3")
    fun getTopTags(): LiveData<List<TagCount>>
}

data class TagCount(
    val tag: String,
    val count: Int
)