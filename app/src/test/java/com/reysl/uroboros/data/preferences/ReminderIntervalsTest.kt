package com.reysl.uroboros.data.preferences

import org.junit.Assert.assertEquals
import org.junit.Test

class ReminderIntervalsTest {

    @Test
    fun `normalize returns default for empty list`() {
        assertEquals(ReminderIntervals.DEFAULT, ReminderIntervals.normalize(emptyList()))
    }

    @Test
    fun `normalize sorts distinct valid days`() {
        assertEquals(listOf(1, 3, 7, 14), ReminderIntervals.normalize(listOf(14, 3, 7, 1, 3)))
    }

    @Test
    fun `normalize filters out of range values`() {
        assertEquals(listOf(1, 365), ReminderIntervals.normalize(listOf(0, 1, 400, 365)))
    }

    @Test
    fun `normalize limits to max count`() {
        val input = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        assertEquals(ReminderIntervals.MAX_COUNT, ReminderIntervals.normalize(input).size)
    }

    @Test
    fun `parse returns default for null`() {
        assertEquals(ReminderIntervals.DEFAULT, ReminderIntervals.parse(null))
    }

    @Test
    fun `serialize and parse roundtrip`() {
        val days = listOf(2, 5, 10)
        val serialized = ReminderIntervals.serialize(days)
        assertEquals("2,5,10", serialized)
        assertEquals(days, ReminderIntervals.parse(serialized))
    }
}
