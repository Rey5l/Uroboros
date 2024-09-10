package com.reysl.uroboros.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val isFavourite: Boolean,
    val tag: String,
    val styledText: String,
    val time: Date
)

