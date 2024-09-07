package com.reysl.uroboros

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    var label: String,
    val icon: Painter,
    val isSelected: Boolean
)
