package com.reysl.uroboros.data.db.note_db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.reysl.uroboros.data.MaterialQuiz

@Dao
interface MaterialQuizDao {

    @Query("SELECT * FROM material_quiz WHERE noteId = :noteId LIMIT 1")
    suspend fun getByNoteId(noteId: Long): MaterialQuiz?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(quiz: MaterialQuiz)

    @Query("DELETE FROM material_quiz WHERE noteId = :noteId")
    suspend fun deleteByNoteId(noteId: Long)
}
