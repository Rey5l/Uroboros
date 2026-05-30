package com.reysl.uroboros.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reysl.uroboros.R
import com.reysl.uroboros.data.preferences.ReminderIntervals
import com.reysl.uroboros.data.preferences.ReminderTime
import com.reysl.uroboros.ui.theme.appGreen
import com.reysl.uroboros.ui.theme.acherusFeral
import com.reysl.uroboros.view.pages.home_page.FilterChipCard

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReminderIntervalsSection(
    intervals: List<Int>,
    reminderTime: ReminderTime,
    onIntervalsChange: (List<Int>) -> Unit,
    onTimeChange: (ReminderTime) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var dayInput by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf<String?>(null) }
    var showTimePicker by remember { mutableStateOf(false) }

    val invalidMessage = stringResource(R.string.settings_reminders_invalid)
    val duplicateMessage = stringResource(R.string.settings_reminders_duplicate)
    val maxCountMessage = stringResource(
        R.string.settings_reminders_max_count,
        ReminderIntervals.MAX_COUNT,
    )
    val rangeMessage = stringResource(
        R.string.settings_reminders_range,
        ReminderIntervals.MIN_DAYS,
        ReminderIntervals.MAX_DAYS,
    )

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.settings_reminders_hint),
            fontFamily = acherusFeral,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        Text(
            text = stringResource(R.string.settings_reminders_time),
            fontFamily = acherusFeral,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        FilterChipCard(
            label = reminderTime.format(),
            selected = false,
            onClick = { showTimePicker = true },
        )

        Text(
            text = stringResource(R.string.settings_reminders_time_hint),
            fontFamily = acherusFeral,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
        )

        Text(
            text = stringResource(R.string.settings_reminders_intervals),
            fontFamily = acherusFeral,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            intervals.forEach { days ->
                FilterChipCard(
                    label = stringResource(R.string.settings_reminders_days_short, days),
                    selected = true,
                    onClick = {
                        if (intervals.size > 1) {
                            onIntervalsChange(intervals.filter { it != days })
                        }
                    },
                )
            }
        }

        if (intervals.size == 1) {
            Text(
                text = stringResource(R.string.settings_reminders_min_one),
                fontFamily = acherusFeral,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = dayInput,
                onValueChange = {
                    dayInput = it.filter { char -> char.isDigit() }.take(3)
                    inputError = null
                },
                modifier = Modifier.weight(1f),
                label = {
                    Text(
                        text = stringResource(R.string.settings_reminders_days_hint),
                        fontFamily = acherusFeral,
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = inputError != null,
                supportingText = inputError?.let { error ->
                    { Text(text = error, fontFamily = acherusFeral) }
                },
            )
            TextButton(
                onClick = {
                    val days = dayInput.toIntOrNull()
                    when {
                        days == null -> inputError = invalidMessage
                        days !in ReminderIntervals.MIN_DAYS..ReminderIntervals.MAX_DAYS -> {
                            inputError = rangeMessage
                        }
                        days in intervals -> inputError = duplicateMessage
                        intervals.size >= ReminderIntervals.MAX_COUNT -> inputError = maxCountMessage
                        else -> {
                            onIntervalsChange((intervals + days).sorted())
                            dayInput = ""
                            inputError = null
                        }
                    }
                },
            ) {
                Text(
                    text = stringResource(R.string.add),
                    fontFamily = acherusFeral,
                    fontWeight = FontWeight.Bold,
                    color = appGreen(),
                )
            }
        }

        TextButton(
            onClick = onReset,
            modifier = Modifier.align(Alignment.End),
        ) {
            Text(
                text = stringResource(R.string.settings_reminders_reset),
                fontFamily = acherusFeral,
                color = appGreen(),
            )
        }
    }

    if (showTimePicker) {
        val pickerState = rememberTimePickerState(
            initialHour = reminderTime.hour,
            initialMinute = reminderTime.minute,
            is24Hour = true,
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = {
                Text(
                    text = stringResource(R.string.settings_reminders_time),
                    fontFamily = acherusFeral,
                )
            },
            text = {
                TimePicker(state = pickerState)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onTimeChange(ReminderTime(pickerState.hour, pickerState.minute))
                        showTimePicker = false
                    },
                ) {
                    Text(
                        text = stringResource(R.string.settings_reminders_done),
                        fontFamily = acherusFeral,
                        fontWeight = FontWeight.Bold,
                        color = appGreen(),
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(
                        text = stringResource(R.string.cancel),
                        fontFamily = acherusFeral,
                    )
                }
            },
        )
    }
}
