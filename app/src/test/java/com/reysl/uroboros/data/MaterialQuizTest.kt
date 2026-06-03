package com.reysl.uroboros.data

import org.junit.Assert.assertEquals
import org.junit.Test

class MaterialQuizTest {

    @Test
    fun `options returns all four choices in order`() {
        val quiz = MaterialQuiz(
            noteId = 1L,
            question = "Q?",
            answer = "A",
            optionA = "One",
            optionB = "Two",
            optionC = "Three",
            optionD = "Four",
            correctOptionIndex = 2,
        )

        assertEquals(listOf("One", "Two", "Three", "Four"), quiz.options())
    }
}
