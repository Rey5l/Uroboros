package com.reysl.uroboros.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "material_quiz",
    indices = [Index(value = ["noteId"], unique = true)],
)
data class MaterialQuiz(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val noteId: Long,
    val question: String,
    val answer: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOptionIndex: Int,
    val createdAt: Long = System.currentTimeMillis(),
) {
    fun options(): List<String> = listOf(optionA, optionB, optionC, optionD)
}
