package com.reysl.uroboros.view.pages.home_page

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.reysl.uroboros.R
import com.reysl.uroboros.utils.performHapticClick
import com.reysl.uroboros.utils.performHapticTick

private val SearchIconSize = 42.dp

@Composable
fun HomeSearchBar(
    isSearch: Boolean,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onToggleSearch: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val iconTint = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)

    if (isSearch) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            trailingIcon = {
                IconButton(onClick = {
                    performHapticTick(context)
                    onToggleSearch(false)
                    onSearchChange("")
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.search_icon),
                        contentDescription = "Search",
                        modifier = Modifier.size(SearchIconSize),
                        colorFilter = iconTint,
                    )
                }
            },
            modifier = modifier.fillMaxWidth(),
            singleLine = true,
        )
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = SearchIconSize),
            contentAlignment = Alignment.CenterEnd,
        ) {
            IconButton(onClick = {
                performHapticClick(context)
                onToggleSearch(true)
            }) {
                Image(
                    painter = painterResource(id = R.drawable.search_icon),
                    contentDescription = "Search",
                    modifier = Modifier.size(SearchIconSize),
                    colorFilter = iconTint,
                )
            }
        }
    }
}
