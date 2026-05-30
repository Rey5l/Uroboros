package com.reysl.uroboros.view.pages

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reysl.uroboros.R
import com.reysl.uroboros.data.preferences.StartTab
import com.reysl.uroboros.data.preferences.ThemeMode
import com.reysl.uroboros.ui.theme.UroborosTheme
import com.reysl.uroboros.ui.theme.acherusFeral
import com.reysl.uroboros.ui.theme.appLogoRes
import com.reysl.uroboros.utils.NotesBackup
import com.reysl.uroboros.view.components.NotificationPermissionSection
import com.reysl.uroboros.view.components.ReminderIntervalsSection
import com.reysl.uroboros.view.components.StatisticsWidget
import com.reysl.uroboros.view.pages.home_page.FilterChipCard
import com.reysl.uroboros.viewmodel.NoteViewModel
import com.reysl.uroboros.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsPage(
    noteViewModel: NoteViewModel,
    settingsViewModel: SettingsViewModel,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val themeMode by settingsViewModel.themeMode.collectAsState()
    val startTab by settingsViewModel.startTab.collectAsState()
    val reminderIntervals by settingsViewModel.reminderIntervals.collectAsState()
    val reminderTime by settingsViewModel.reminderTime.collectAsState()
    val appVersion = remember { context.readAppVersionName() }

    var showInfoDialog by remember { mutableStateOf(false) }
    var pendingImport by remember { mutableStateOf<NotesBackup.Backup?>(null) }
    var pendingImportJson by remember { mutableStateOf<String?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            runCatching {
                val json = noteViewModel.createBackupJson()
                context.contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.write(json.toByteArray(Charsets.UTF_8))
                } ?: error("Cannot open output stream")
            }.onSuccess {
                Toast.makeText(
                    context,
                    context.getString(R.string.settings_export_success),
                    Toast.LENGTH_SHORT,
                ).show()
            }.onFailure {
                Toast.makeText(
                    context,
                    context.getString(R.string.settings_export_error),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            runCatching {
                val json = context.contentResolver.openInputStream(uri)?.use { stream ->
                    stream.bufferedReader().readText()
                } ?: error("Cannot open input stream")
                pendingImportJson = json
                NotesBackup.fromJson(json)
            }.onSuccess { backup ->
                pendingImport = backup
            }.onFailure {
                Toast.makeText(
                    context,
                    context.getString(R.string.settings_import_error),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    UroborosTheme(themeMode = themeMode) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            Text(
                text = stringResource(R.string.settings),
                fontFamily = acherusFeral,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                modifier = Modifier.padding(top = 20.dp, bottom = 16.dp),
            )

            StatisticsWidget(noteViewModel = noteViewModel)

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSectionCard(title = stringResource(R.string.settings_theme)) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ThemeMode.entries.forEach { mode ->
                        FilterChipCard(
                            label = stringResource(themeModeLabel(mode)),
                            selected = themeMode == mode,
                            onClick = { settingsViewModel.setThemeMode(mode) },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSectionCard(title = stringResource(R.string.settings_start_tab)) {
                Text(
                    text = stringResource(R.string.settings_start_tab_hint),
                    fontFamily = acherusFeral,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    StartTab.entries.forEach { tab ->
                        FilterChipCard(
                            label = stringResource(startTabLabel(tab)),
                            selected = startTab == tab,
                            onClick = { settingsViewModel.setStartTab(tab) },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSectionCard(title = stringResource(R.string.settings_notifications)) {
                NotificationPermissionSection()
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSectionCard(title = stringResource(R.string.settings_reminders)) {
                ReminderIntervalsSection(
                    intervals = reminderIntervals,
                    reminderTime = reminderTime,
                    onIntervalsChange = { updated ->
                        settingsViewModel.updateReminderIntervals(updated, context) { result ->
                            val message = result.fold(
                                onSuccess = { context.getString(R.string.settings_reminders_saved) },
                                onFailure = { context.getString(R.string.settings_reminders_save_error) },
                            )
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    },
                    onTimeChange = { time ->
                        settingsViewModel.updateReminderTime(time, context) { result ->
                            val message = result.fold(
                                onSuccess = { context.getString(R.string.settings_reminders_saved) },
                                onFailure = { context.getString(R.string.settings_reminders_save_error) },
                            )
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    },
                    onReset = {
                        settingsViewModel.resetReminderIntervals(context) { result ->
                            val message = result.fold(
                                onSuccess = { context.getString(R.string.settings_reminders_saved) },
                                onFailure = { context.getString(R.string.settings_reminders_save_error) },
                            )
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSectionCard(title = stringResource(R.string.settings_data)) {
                SettingsActionRow(
                    icon = R.drawable.success,
                    label = stringResource(R.string.settings_export),
                    onClick = {
                        scope.launch {
                            if (noteViewModel.getNotesCount() == 0) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.settings_export_empty),
                                    Toast.LENGTH_SHORT,
                                ).show()
                                return@launch
                            }
                            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                            exportLauncher.launch("uroboros_backup_$date.json")
                        }
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                SettingsActionRow(
                    icon = R.drawable.adding,
                    label = stringResource(R.string.settings_import),
                    onClick = { importLauncher.launch(arrayOf("application/json", "text/plain", "*/*")) },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showInfoDialog = true },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(R.drawable.info),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        colorFilter = ColorFilter.tint(colorResource(R.color.green)),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.settings_about),
                            fontFamily = acherusFeral,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                        )
                        Text(
                            text = stringResource(R.string.settings_about_subtitle, appVersion),
                            fontFamily = acherusFeral,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        if (showInfoDialog) {
            SettingsInfoDialog(
                appVersion = appVersion,
                onDismiss = { showInfoDialog = false },
            )
        }

        pendingImport?.let { backup ->
            AlertDialog(
                onDismissRequest = {
                    pendingImport = null
                    pendingImportJson = null
                },
                title = {
                    Text(
                        text = stringResource(R.string.settings_import_title),
                        fontFamily = acherusFeral,
                        fontWeight = FontWeight.Bold,
                    )
                },
                text = {
                    Column {
                        Text(
                            text = stringResource(
                                R.string.settings_import_message,
                                backup.notes.size,
                                backup.tags.size,
                            ),
                            fontFamily = acherusFeral,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(
                                onClick = {
                                    importNotes(
                                        noteViewModel = noteViewModel,
                                        context = context,
                                        json = pendingImportJson,
                                        replaceExisting = false,
                                        onFinished = {
                                            pendingImport = null
                                            pendingImportJson = null
                                        },
                                    )
                                },
                            ) {
                                Text(
                                    text = stringResource(R.string.settings_import_merge),
                                    color = colorResource(R.color.green),
                                    fontFamily = acherusFeral,
                                )
                            }
                            TextButton(
                                onClick = {
                                    importNotes(
                                        noteViewModel = noteViewModel,
                                        context = context,
                                        json = pendingImportJson,
                                        replaceExisting = true,
                                        onFinished = {
                                            pendingImport = null
                                            pendingImportJson = null
                                        },
                                    )
                                },
                            ) {
                                Text(
                                    text = stringResource(R.string.settings_import_replace),
                                    color = colorResource(R.color.orange_donation),
                                    fontFamily = acherusFeral,
                                )
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(
                        onClick = {
                            pendingImport = null
                            pendingImportJson = null
                        },
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            fontFamily = acherusFeral,
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontFamily = acherusFeral,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            content()
        }
    }
}

@Composable
private fun SettingsActionRow(
    icon: Int,
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = label,
            tint = colorResource(R.color.green),
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontFamily = acherusFeral,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )
    }
}

private fun themeModeLabel(mode: ThemeMode): Int = when (mode) {
    ThemeMode.SYSTEM -> R.string.settings_theme_system
    ThemeMode.LIGHT -> R.string.settings_theme_light
    ThemeMode.DARK -> R.string.settings_theme_dark
}

private fun startTabLabel(tab: StartTab): Int = when (tab) {
    StartTab.NOTES -> R.string.notes
    StartTab.HOME -> R.string.home
    StartTab.SETTINGS -> R.string.settings
}

private fun importNotes(
    noteViewModel: NoteViewModel,
    context: android.content.Context,
    json: String?,
    replaceExisting: Boolean,
    onFinished: () -> Unit,
) {
    val payload = json ?: return
    noteViewModel.importBackup(
        json = payload,
        replaceExisting = replaceExisting,
        context = context,
    ) { result ->
        onFinished()
        val message = result.fold(
            onSuccess = { count ->
                context.getString(R.string.settings_import_success, count)
            },
            onFailure = {
                context.getString(R.string.settings_import_error)
            },
        )
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun SettingsInfoDialog(
    appVersion: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.settings_about),
                fontFamily = acherusFeral,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(appLogoRes()),
                        contentDescription = stringResource(R.string.app_name),
                        modifier = Modifier.size(72.dp),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        fontFamily = acherusFeral,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                    )
                    Text(
                        text = stringResource(R.string.settings_about_version, appVersion),
                        fontFamily = acherusFeral,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                SettingsAboutDetailRow(
                    label = stringResource(R.string.settings_about_developer),
                    value = stringResource(R.string.settings_about_developer_name),
                )
                SettingsAboutDetailRow(
                    label = stringResource(R.string.settings_about_package),
                    value = stringResource(R.string.settings_about_package_name),
                )

                Text(
                    text = stringResource(R.string.settings_about_description),
                    fontFamily = acherusFeral,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(top = 12.dp),
                )

                Text(
                    text = stringResource(R.string.settings_about_copyright),
                    fontFamily = acherusFeral,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 12.dp),
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Text(
                    text = stringResource(R.string.settings_about_contacts),
                    fontFamily = acherusFeral,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 4.dp),
                )

                SettingsInfoItem(
                    icon = R.drawable.donation,
                    label = stringResource(R.string.support_the_author),
                    url = "https://www.donationalerts.com/r/reysl",
                )
                SettingsInfoItem(
                    icon = R.drawable.telegram,
                    label = stringResource(R.string.telegram_channel),
                    url = "https://t.me/reysldevblog",
                )
                SettingsInfoItem(
                    icon = R.drawable.help,
                    label = stringResource(R.string.feedback),
                    email = "Rey5l@yandex.ru",
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.close),
                    color = colorResource(R.color.green),
                    fontFamily = acherusFeral,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
    )
}

@Composable
private fun SettingsAboutDetailRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontFamily = acherusFeral,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            fontFamily = acherusFeral,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

private fun android.content.Context.readAppVersionName(): String = runCatching {
    val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
    } else {
        @Suppress("DEPRECATION")
        packageManager.getPackageInfo(packageName, 0)
    }
    packageInfo.versionName ?: "?"
}.getOrDefault("?")

@Composable
private fun SettingsInfoItem(
    icon: Int,
    label: String,
    url: String? = null,
    email: String? = null,
) {
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                if (url != null) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } else if (email != null) {
                    context.startActivity(
                        Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:$email")
                        }
                    )
                }
            },
    ) {
        val tint = if (icon == R.drawable.donation) {
            colorResource(R.color.orange_donation)
        } else {
            colorResource(R.color.green)
        }
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = if (icon == R.drawable.donation) tint else MaterialTheme.colorScheme.onBackground,
            fontWeight = if (icon == R.drawable.donation) FontWeight.Bold else FontWeight.Normal,
            fontFamily = acherusFeral,
        )
    }
}
