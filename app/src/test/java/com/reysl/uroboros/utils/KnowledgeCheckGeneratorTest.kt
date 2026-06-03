package com.reysl.uroboros.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KnowledgeCheckGeneratorTest {

    @Test
    fun `generate returns plain segment for blank text`() {
        val segments = KnowledgeCheckGenerator.generate("", seed = 1L)
        assertEquals(1, segments.size)
        assertTrue(segments[0] is KnowledgeCheckSegment.Plain)
    }

    @Test
    fun `generate hides words deterministically for same seed`() {
        val text = "Повторение материала помогает запоминать информацию надолго"
        val first = KnowledgeCheckGenerator.generate(text, seed = 42L)
        val second = KnowledgeCheckGenerator.generate(text, seed = 42L)
        assertEquals(first, second)
    }

    @Test
    fun `generate contains at least one hidden word for long text`() {
        val text = "Повторение материала помогает запоминать информацию надолго"
        val segments = KnowledgeCheckGenerator.generate(text, seed = 7L)
        assertTrue(segments.any { it is KnowledgeCheckSegment.HiddenWord })
    }

    @Test
    fun `reconstructed text matches original`() {
        val text = "Повторение материала помогает запоминать информацию"
        val segments = KnowledgeCheckGenerator.generate(text, seed = 3L)
        val reconstructed = segments.joinToString("") { segment ->
            when (segment) {
                is KnowledgeCheckSegment.Plain -> segment.text
                is KnowledgeCheckSegment.HiddenWord -> segment.text
            }
        }
        assertEquals(text, reconstructed)
    }
}
