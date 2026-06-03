package com.reysl.uroboros.data.preferences

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class ReminderTimeTest {

    @Test
    fun `format pads hour and minute`() {
        assertEquals("09:05", ReminderTime(hour = 9, minute = 5).format())
    }

    @Test
    fun `default reminder time is 09-00`() {
        assertEquals(9, ReminderTime.DEFAULT.hour)
        assertEquals(0, ReminderTime.DEFAULT.minute)
    }

    @Test
    fun `targetMillis adds interval days and sets time of day`() {
        val zone = ZoneId.systemDefault()
        val baseDate = LocalDate.of(2026, 1, 10)
        val baseMillis = baseDate.atStartOfDay(zone).toInstant().toEpochMilli()
        val reminderTime = ReminderTime(hour = 14, minute = 30)

        val targetMillis = ReminderTimeCalculator.targetMillis(
            baseTimeMillis = baseMillis,
            intervalDays = 3,
            reminderTime = reminderTime,
        )

        val target = java.time.Instant.ofEpochMilli(targetMillis).atZone(zone)
        assertEquals(LocalDate.of(2026, 1, 13), target.toLocalDate())
        assertEquals(14, target.hour)
        assertEquals(30, target.minute)
    }
}
