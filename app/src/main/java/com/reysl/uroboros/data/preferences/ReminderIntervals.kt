package com.reysl.uroboros.data.preferences

object ReminderIntervals {

    val DEFAULT = listOf(1, 3, 7, 14)

    const val MIN_DAYS = 1
    const val MAX_DAYS = 365
    const val MAX_COUNT = 8

    fun normalize(days: List<Int>): List<Int> =
        days
            .filter { it in MIN_DAYS..MAX_DAYS }
            .distinct()
            .sorted()
            .take(MAX_COUNT)
            .ifEmpty { DEFAULT }

    fun parse(stored: String?): List<Int> {
        if (stored.isNullOrBlank()) return DEFAULT
        val parsed = stored.split(',')
            .mapNotNull { it.trim().toIntOrNull() }
        return normalize(parsed)
    }

    fun serialize(days: List<Int>): String = normalize(days).joinToString(",")
}
