package com.reysl.uroboros.view.pages.home_page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.reysl.uroboros.R
import com.reysl.uroboros.utils.performHapticClick
import com.reysl.uroboros.utils.performHapticTick

@Composable
fun HomeSearchBar(
    isSearch: Boolean,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onToggleSearch: (Boolean) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        AnimatedVisibility(
            visible = isSearch,
            enter = fadeIn() + expandHorizontally(expandFrom = Alignment.End),
            exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.End)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                trailingIcon = {
                    IconButton(onClick = {
//                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        performHapticTick(context)
                        onToggleSearch(false)
                        onSearchChange("")
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.search_icon),
                            contentDescription = "Search",
                            modifier = Modifier.size(35.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        AnimatedVisibility(
            visible = !isSearch,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            IconButton(
                onClick = {
//                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    performHapticClick(context)
                    onToggleSearch(true)
                },
            ) {
                Image(
                    painter = painterResource(id = R.drawable.search_icon),
                    contentDescription = "Search",
                    modifier = Modifier
                        .size(35.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )
            }
        }
    }
}

