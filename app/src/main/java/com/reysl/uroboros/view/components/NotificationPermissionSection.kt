package com.reysl.uroboros.view.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.reysl.uroboros.R
import com.reysl.uroboros.notification.NotificationPermission
import com.reysl.uroboros.ui.theme.acherusFeral
import com.reysl.uroboros.ui.theme.appGreen

@Composable
fun NotificationPermissionPrompt(
    visible: Boolean,
    onDismiss: () -> Unit,
    onPermissionChanged: () -> Unit = {},
) {
    val context = LocalContext.current
    val activity = context as? androidx.activity.ComponentActivity

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) {
        onPermissionChanged()
        if (NotificationPermission.areEnabled(context)) {
            onDismiss()
        }
    }

    if (!visible || NotificationPermission.areEnabled(context)) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.notification_permission_title),
                fontFamily = acherusFeral,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Text(
                text = stringResource(R.string.notification_permission_message),
                fontFamily = acherusFeral,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (activity != null) {
                        NotificationPermission.request(activity, permissionLauncher)
                    } else {
                        NotificationPermission.openSettings(context)
                    }
                },
            ) {
                Text(
                    text = stringResource(R.string.settings_notifications_grant),
                    fontFamily = acherusFeral,
                    fontWeight = FontWeight.Bold,
                    color = appGreen(),
                )
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = { NotificationPermission.openSettings(context) }) {
                    Text(
                        text = stringResource(R.string.settings_notifications_open_settings),
                        fontFamily = acherusFeral,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(R.string.notification_permission_later),
                        fontFamily = acherusFeral,
                    )
                }
            }
        },
    )
}

@Composable
fun NotificationPermissionSection(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = context as? androidx.activity.ComponentActivity
    val lifecycleOwner = LocalLifecycleOwner.current
    var enabled by remember { mutableStateOf(NotificationPermission.areEnabled(context)) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) {
        enabled = NotificationPermission.areEnabled(context)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                enabled = NotificationPermission.areEnabled(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.settings_notifications_hint),
            fontFamily = acherusFeral,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.settings_notifications_status),
                fontFamily = acherusFeral,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(
                    if (enabled) {
                        R.string.settings_notifications_status_granted
                    } else {
                        R.string.settings_notifications_status_denied
                    },
                ),
                fontFamily = acherusFeral,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (enabled) appGreen() else colorResource(R.color.orange_donation),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (!enabled) {
                TextButton(
                    onClick = {
                        if (activity != null) {
                            NotificationPermission.request(activity, permissionLauncher)
                        } else {
                            NotificationPermission.openSettings(context)
                        }
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = stringResource(R.string.settings_notifications_grant),
                        fontFamily = acherusFeral,
                        fontWeight = FontWeight.Bold,
                        color = appGreen(),
                    )
                }
            }
            TextButton(
                onClick = { NotificationPermission.openSettings(context) },
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(R.string.settings_notifications_open_settings),
                    fontFamily = acherusFeral,
                    color = if (enabled) appGreen() else colorResource(R.color.orange_donation),
                )
            }
        }
    }
}
