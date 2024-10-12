package com.reysl.uroboros.data.db.note_db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.reysl.uroboros.Converters
import com.reysl.uroboros.data.Note

@Database(entities = [Note::class], version = 7)
@TypeConverters(Converters::class)
abstract class NoteDatabase: RoomDatabase() {

    companion object {
        const val NAME = "Note_DB"
    }

    abstract fun getNoteDao(): NoteDao
}