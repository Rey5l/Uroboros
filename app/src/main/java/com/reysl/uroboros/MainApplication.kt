package com.reysl.uroboros

import android.app.Application
import androidx.room.Room
import com.reysl.uroboros.data.db.note_db.NoteDatabase
import com.reysl.uroboros.data.db.tag_db.TagDatabase

class MainApplication: Application() {
    companion object {
        lateinit var noteDatabase: NoteDatabase
        lateinit var tagDatabase: TagDatabase
    }

    override fun onCreate() {
        super.onCreate()
        noteDatabase = Room.databaseBuilder(
            applicationContext,
            NoteDatabase::class.java,
            NoteDatabase.NAME
        )
            .fallbackToDestructiveMigration()
            .build()
        tagDatabase = Room.databaseBuilder(
            applicationContext,
            TagDatabase::class.java,
            TagDatabase.NAME
        ).build()
    }
}