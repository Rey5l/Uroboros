package com.reysl.uroboros.data.preferences

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.time.LocalDate

class QuizGenerationLimiterTest {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val storage = mutableMapOf<String, Any?>()

    @Before
    fun setUp() {
        storage.clear()
        storage[KEY_DATE] = LocalDate.now().toString()
        storage[KEY_COUNT] = 0

        editor = mock(SharedPreferences.Editor::class.java)
        `when`(editor.putString(anyString(), anyString())).thenAnswer {
            storage[it.arguments[0] as String] = it.arguments[1] as String
            editor
        }
        `when`(editor.putInt(anyString(), anyInt())).thenAnswer {
            storage[it.arguments[0] as String] = it.arguments[1] as Int
            editor
        }
        `when`(editor.apply()).then { null }

        prefs = mock(SharedPreferences::class.java)
        `when`(prefs.edit()).thenReturn(editor)
        `when`(prefs.getString(anyString(), org.mockito.ArgumentMatchers.isNull())).thenAnswer {
            storage[it.arguments[0] as String] as String?
        }
        `when`(prefs.getInt(anyString(), anyInt())).thenAnswer {
            storage[it.arguments[0] as String] as? Int ?: it.arguments[1] as Int
        }

        context = mock(Context::class.java)
        `when`(context.applicationContext).thenReturn(context)
        `when`(context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)).thenReturn(prefs)
    }

    @Test
    fun `remainingToday starts at daily limit`() {
        val limiter = QuizGenerationLimiter(context)
        assertEquals(QuizGenerationLimiter.DAILY_LIMIT, limiter.remainingToday())
    }

    @Test
    fun `tryConsumeGeneration decreases remaining count`() {
        val limiter = QuizGenerationLimiter(context)
        assertTrue(limiter.tryConsumeGeneration())
        assertEquals(QuizGenerationLimiter.DAILY_LIMIT - 1, limiter.remainingToday())
    }

    @Test
    fun `tryConsumeGeneration returns false after limit reached`() {
        val limiter = QuizGenerationLimiter(context)
        repeat(QuizGenerationLimiter.DAILY_LIMIT) {
            assertTrue(limiter.tryConsumeGeneration())
        }
        assertFalse(limiter.tryConsumeGeneration())
        assertEquals(0, limiter.remainingToday())
    }

    companion object {
        private const val PREFS_NAME = "uroboros_quiz_generation"
        private const val KEY_DATE = "generation_date"
        private const val KEY_COUNT = "generation_count"
    }
}
