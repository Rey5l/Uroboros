package com.reysl.uroboros.data.db.note_db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.reysl.uroboros.components.Converters
import com.reysl.uroboros.data.MaterialQuiz
import com.reysl.uroboros.data.Note

@Database(entities = [Note::class, MaterialQuiz::class], version = 9)
@TypeConverters(Converters::class)
abstract class NoteDatabase: RoomDatabase() {

    companion object {
        const val NAME = "Note_DB"
    }

    abstract fun getNoteDao(): NoteDao
    abstract fun getMaterialQuizDao(): MaterialQuizDao
}