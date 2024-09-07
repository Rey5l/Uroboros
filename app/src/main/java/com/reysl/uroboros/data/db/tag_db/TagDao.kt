package com.reysl.uroboros.data.db.tag_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.reysl.uroboros.data.Tag

@Dao
interface TagDao {

    @Query("SELECT * FROM tags")
    fun getAllTags(): LiveData<List<Tag>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addTag(tag: Tag)

    @Query("Select COUNT(*) From tags Where tag = :tag")
    suspend fun getTagCount(tag: String): Int

    @Delete
    suspend fun deleteTag(tag: Tag)

    @Query("Delete From tags Where tag = :tag")
    suspend fun deleteTagByName(tag: String)

}