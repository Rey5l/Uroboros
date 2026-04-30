package com.reysl.uroboros.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class TagTest {

    @Test
    fun `Tag creation with all fields`() {
        val tag = Tag(id = 1, tag = "TestTag")

        assertEquals(1, tag.id)
        assertEquals("TestTag", tag.tag)
    }

    @Test
    fun `Tag creation with default id`() {
        val tag = Tag(tag = "TestTag")

        assertEquals(0, tag.id)
        assertEquals("TestTag", tag.tag)
    }

    @Test
    fun `Tag equality check`() {
        val tag1 = Tag(id = 1, tag = "TestTag")
        val tag2 = Tag(id = 1, tag = "TestTag")

        assertEquals(tag1, tag2)
    }

    @Test
    fun `Tag inequality check`() {
        val tag1 = Tag(id = 1, tag = "Tag1")
        val tag2 = Tag(id = 2, tag = "Tag2")

        assertNotEquals(tag1, tag2)
    }

    @Test
    fun `Tag copy with modified fields`() {
        val originalTag = Tag(id = 1, tag = "Original")
        val copiedTag = originalTag.copy(tag = "Modified")

        assertEquals("Modified", copiedTag.tag)
        assertEquals(originalTag.id, copiedTag.id)
    }

    @Test
    fun `Tag with empty string`() {
        val tag = Tag(id = 1, tag = "")

        assertEquals("", tag.tag)
    }

    @Test
    fun `Tag with special characters`() {
        val tag = Tag(id = 1, tag = "Test-Tag_123")

        assertEquals("Test-Tag_123", tag.tag)
    }
}
