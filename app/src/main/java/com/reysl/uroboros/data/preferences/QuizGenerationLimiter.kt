package com.reysl.uroboros.data.preferences

import android.content.Context
import java.time.LocalDate

class QuizGenerationLimiter(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun dailyLimit(): Int = DAILY_LIMIT

    fun remainingToday(): Int = (DAILY_LIMIT - usedToday()).coerceAtLeast(0)

    fun canGenerate(): Boolean = remainingToday() > 0

    fun tryConsumeGeneration(): Boolean {
        resetIfNewDay()
        val used = prefs.getInt(KEY_COUNT, 0)
        if (used >= DAILY_LIMIT) return false
        prefs.edit()
            .putString(KEY_DATE, todayKey())
            .putInt(KEY_COUNT, used + 1)
            .apply()
        return true
    }

    private fun usedToday(): Int {
        resetIfNewDay()
        return prefs.getInt(KEY_COUNT, 0)
    }

    private fun resetIfNewDay() {
        val savedDate = prefs.getString(KEY_DATE, null)
        if (savedDate != todayKey()) {
            prefs.edit()
                .putString(KEY_DATE, todayKey())
                .putInt(KEY_COUNT, 0)
                .apply()
        }
    }

    private fun todayKey(): String = LocalDate.now().toString()

    companion object {
        const val DAILY_LIMIT = 3
        private const val PREFS_NAME = "uroboros_quiz_generation"
        private const val KEY_DATE = "generation_date"
        private const val KEY_COUNT = "generation_count"
    }
}
