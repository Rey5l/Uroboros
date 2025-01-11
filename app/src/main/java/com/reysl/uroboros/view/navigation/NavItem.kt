package com.reysl.uroboros.view.navigation

import androidx.compose.ui.graphics.painter.Painter

data class NavItem(
    var label: String,
    val icon: Painter,
    val isSelected: Boolean
)
