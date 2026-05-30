package com.reysl.uroboros.data.preferences

import java.time.Instant
import java.time.ZoneId

data class ReminderTime(
    val hour: Int,
    val minute: Int,
) {
    init {
        require(hour in 0..23) { "Hour must be 0..23" }
        require(minute in 0..59) { "Minute must be 0..59" }
    }

    fun format(): String = "%02d:%02d".format(hour, minute)

    companion object {
        val DEFAULT = ReminderTime(hour = 9, minute = 0)
    }
}

object ReminderTimeCalculator {

    fun targetMillis(
        baseTimeMillis: Long,
        intervalDays: Int,
        reminderTime: ReminderTime,
    ): Long {
        return Instant.ofEpochMilli(baseTimeMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .plusDays(intervalDays.toLong())
            .atTime(reminderTime.hour, reminderTime.minute)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
}
