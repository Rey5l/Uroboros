package com.reysl.uroboros.utils

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith

fun contentTransitionSpec(): ContentTransform =
    fadeIn(tween(200)) togetherWith fadeOut(tween(150))

fun tabTransitionSpec(
    initialIndex: Int,
    targetIndex: Int,
): ContentTransform {
    val direction = if (targetIndex > initialIndex) 1 else -1
    return slideInHorizontally { direction * it / 4 } + fadeIn(tween(220)) togetherWith
        slideOutHorizontally { -direction * it / 4 } + fadeOut(tween(160))
}

fun slideUpEnter(): EnterTransition =
    slideInVertically(spring(stiffness = Spring.StiffnessMediumLow)) { it } + fadeIn(tween(200))

fun slideDownExit(): ExitTransition =
    slideOutVertically(spring(stiffness = Spring.StiffnessMediumLow)) { it } + fadeOut(tween(160))

fun noteScreenEnterTransition(): EnterTransition =
    slideInHorizontally(tween(280)) { it / 3 } + fadeIn(tween(280))

fun noteScreenExitTransition(): ExitTransition =
    slideOutHorizontally(tween(240)) { -it / 3 } + fadeOut(tween(180))

fun noteScreenPopEnterTransition(): EnterTransition =
    slideInHorizontally(tween(280)) { -it / 3 } + fadeIn(tween(280))

fun noteScreenPopExitTransition(): ExitTransition =
    slideOutHorizontally(tween(240)) { it / 3 } + fadeOut(tween(180))
