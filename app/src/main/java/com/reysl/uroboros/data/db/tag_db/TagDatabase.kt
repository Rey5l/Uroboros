package com.reysl.uroboros.data.db.tag_db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.reysl.uroboros.data.Tag

@Database(entities = [Tag::class], version = 3)
abstract class TagDatabase: RoomDatabase() {
    companion object {
        const val NAME = "Tag_DB"
    }

    abstract fun getTagDao(): TagDao
}