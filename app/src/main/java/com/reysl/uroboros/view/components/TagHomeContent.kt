package com.reysl.uroboros.view.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reysl.uroboros.R
import com.reysl.uroboros.ui.theme.appSecondaryText
import com.reysl.uroboros.data.Tag
import com.reysl.uroboros.view.pages.home_page.FilterChipCard
import com.reysl.uroboros.ui.theme.acherusFeral
import com.reysl.uroboros.viewmodel.TagViewModel

@Composable
fun TagHomeContent(
    tagViewModel: TagViewModel,
    selectedTag: String?,
    favouritesOnly: Boolean,
    onTagSelected: (Tag?) -> Unit,
    onFavouritesToggle: () -> Unit,
    onShowAll: () -> Unit,
) {
    val tagList by tagViewModel.tagList.observeAsState(emptyList())
    val showAllSelected = !favouritesOnly && selectedTag == null
    val starScale by animateFloatAsState(
        targetValue = if (favouritesOnly) 1.15f else 1f,
        animationSpec = spring(),
        label = "favourite_star_scale",
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onFavouritesToggle) {
            Image(
                painter = painterResource(
                    if (favouritesOnly) R.drawable.star_filled else R.drawable.star,
                ),
                contentDescription = stringResource(R.string.favourite),
                modifier = Modifier
                    .size(30.dp)
                    .scale(starScale),
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            item {
                FilterChipCard(
                    label = stringResource(R.string.home_filter_all),
                    selected = showAllSelected,
                    onClick = {
                        if (!showAllSelected) onShowAll()
                    },
                )
            }
            items(tagList, key = { it.tag }) { item ->
                FilterChipCard(
                    label = item.tag,
                    selected = !favouritesOnly && selectedTag == item.tag,
                    onClick = {
                        if (selectedTag == item.tag) {
                            onShowAll()
                        } else {
                            onTagSelected(item)
                        }
                    },
                )
            }
            if (tagList.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.home_no_tags),
                        fontFamily = acherusFeral,
                        fontSize = 14.sp,
                        color = appSecondaryText(),
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                    )
                }
            }
        }
    }
}
