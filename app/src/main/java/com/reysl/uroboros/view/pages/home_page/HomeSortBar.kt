package com.reysl.uroboros.view.pages.home_page

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reysl.uroboros.R
import com.reysl.uroboros.utils.MaterialSort
import com.reysl.uroboros.ui.theme.appChipSelectedText
import com.reysl.uroboros.ui.theme.appGreen
import com.reysl.uroboros.ui.theme.appSortAccent
import com.reysl.uroboros.ui.theme.appSortContainer
import com.reysl.uroboros.ui.theme.acherusFeral

@Composable
fun FilterChipCard(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val green = appGreen()
    val containerColor by animateColorAsState(
        targetValue = if (selected) green else MaterialTheme.colorScheme.surface,
        animationSpec = tween(200),
        label = "chip_bg",
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) {
            appChipSelectedText()
        } else {
            MaterialTheme.colorScheme.onBackground
        },
        animationSpec = tween(200),
        label = "chip_text",
    )
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, green),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Text(
            text = label,
            fontFamily = acherusFeral,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 15.sp,
            color = textColor,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        )
    }
}

@Composable
fun HomeSortMenu(
    sort: MaterialSort,
    onSortChange: (MaterialSort) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(
        MaterialSort.NEWEST to R.string.sort_newest,
        MaterialSort.OLDEST to R.string.sort_oldest,
        MaterialSort.TITLE_ASC to R.string.sort_title_asc,
        MaterialSort.TITLE_DESC to R.string.sort_title_desc,
    )
    val currentLabel = stringResource(
        options.first { it.first == sort }.second
    )
    val accentColor = appSortAccent()
    val containerColor = appSortContainer()

    Box(modifier = modifier) {
        Card(
            modifier = Modifier.clickable { expanded = true },
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(1.dp, accentColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Crossfade(
                    targetState = currentLabel,
                    animationSpec = tween(180),
                    label = "sort_label",
                ) { label ->
                    Text(
                        text = label,
                        fontFamily = acherusFeral,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = accentColor,
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.home_sort),
                    tint = accentColor,
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { (option, labelRes) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(labelRes),
                            fontFamily = acherusFeral,
                            fontWeight = if (sort == option) FontWeight.Bold else FontWeight.Normal,
                            color = if (sort == option) accentColor else MaterialTheme.colorScheme.onSurface,
                        )
                    },
                    onClick = {
                        onSortChange(option)
                        expanded = false
                    },
                )
            }
        }
    }
}
