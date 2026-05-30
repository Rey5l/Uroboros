package com.reysl.uroboros.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.reysl.uroboros.R
import com.reysl.uroboros.data.preferences.ThemeMode

@Composable
fun isAppInDarkTheme(): Boolean = when (LocalThemeMode.current) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
}

@Composable
fun appLogoRes(): Int =
    if (isAppInDarkTheme()) R.drawable.uroboros_logo_dark else R.drawable.uroboros_logo

@Composable
fun appGreen(): Color = colorResource(R.color.green)

@Composable
fun appNavbarBackground(): Color =
    if (isAppInDarkTheme()) {
        MaterialTheme.colorScheme.surfaceContainer
    } else {
        colorResource(R.color.navbar_bg)
    }

@Composable
fun appNavSelectedIcon(): Color =
    if (isAppInDarkTheme()) colorResource(R.color.card_color) else Color.White

@Composable
fun appLightGreenSurface(): Color =
    if (isAppInDarkTheme()) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
    } else {
        colorResource(R.color.light_green)
    }

@Composable
fun appOnGreenTopBar(): Color =
    if (isAppInDarkTheme()) colorResource(R.color.card_color) else Color.White

@Composable
fun appOnGreenIcon(): Color =
    if (isAppInDarkTheme()) colorResource(R.color.card_color) else colorResource(R.color.card_color)

@Composable
fun appSecondaryText(): Color =
    if (isAppInDarkTheme()) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        colorResource(R.color.text_color)
    }

@Composable
fun appEditorSurface(): Color =
    if (isAppInDarkTheme()) {
        colorResource(R.color.background_dark)
    } else {
        MaterialTheme.colorScheme.surface
    }

@Composable
fun appChipSelectedText(): Color =
    if (isAppInDarkTheme()) colorResource(R.color.card_color) else colorResource(R.color.card_color)

@Composable
fun appSortAccent(): Color =
    if (isAppInDarkTheme()) MaterialTheme.colorScheme.onPrimary else appGreen()

@Composable
fun appSortContainer(): Color =
    if (isAppInDarkTheme()) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
    } else {
        colorResource(R.color.light_green).copy(alpha = 0.45f)
    }

@Composable
fun appCodeBlockBackground(): Color =
    if (isAppInDarkTheme()) Color(0xFF1A2420) else Color(0xFFDCEFE3)

@Composable
fun appCodeBlockText(): Color =
    if (isAppInDarkTheme()) Color(0xFFE2F0E8) else Color(0xFF163D2A)

@Composable
fun appCodeBlockLanguage(): Color =
    if (isAppInDarkTheme()) Color(0xFF8FD4A8) else Color(0xFF236853)

@Composable
fun appKnowledgeCheckHiddenBg(): Color = appLightGreenSurface()

@Composable
fun appDialogText(): Color =
    if (isAppInDarkTheme()) MaterialTheme.colorScheme.onSurface else colorResource(R.color.black)

@Composable
fun appTagBadgeText(): Color = MaterialTheme.colorScheme.onPrimary

@Composable
fun appTagTimeText(): Color = MaterialTheme.colorScheme.primary

@Composable
fun appButtonOnGreen(): Color = colorResource(R.color.card_color)

@Composable
fun appAccentBorder(): Color = appGreen()
