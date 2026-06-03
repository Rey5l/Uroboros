package com.reysl.uroboros.data.llm

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class LlmQuizResponseParserTest {

    private lateinit var parser: LlmQuizResponseParser

    @Before
    fun setUp() {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        parser = LlmQuizResponseParser(moshi)
    }

    @Test
    fun `parse extracts JSON from markdown fenced block`() {
        val raw = """
            ```json
            {
              "question": "Что такое Uroboros?",
              "answer": "Приложение для повторений",
              "options": ["A", "B", "C", "D"],
              "correct_index": 0
            }
            ```
        """.trimIndent()

        val result = parser.parse(raw)

        assertNotNull(result)
        assertEquals("Что такое Uroboros?", result!!.question)
        assertEquals(0, result.correctIndex)
        assertEquals(4, result.options.size)
    }

    @Test
    fun `parse returns null for invalid JSON`() {
        assertNull(parser.parse("not json at all"))
    }
}
