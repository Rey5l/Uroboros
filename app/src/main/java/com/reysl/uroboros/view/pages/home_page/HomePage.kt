package com.reysl.uroboros.view.pages.home_page

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import com.reysl.uroboros.ui.theme.appLogoRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.reysl.uroboros.R
import com.reysl.uroboros.data.Tag
import com.reysl.uroboros.ui.theme.UroborosTheme
import com.reysl.uroboros.utils.MaterialSort
import com.reysl.uroboros.utils.contentTransitionSpec
import com.reysl.uroboros.view.components.NoteFavouriteHomeContent
import com.reysl.uroboros.view.components.NoteHomeContent
import com.reysl.uroboros.view.components.NoteSearchHomeContent
import com.reysl.uroboros.view.components.NoteTagSearchHomeContent
import com.reysl.uroboros.view.components.TagHomeContent
import com.reysl.uroboros.ui.theme.acherusFeral
import com.reysl.uroboros.viewmodel.NoteViewModel
import com.reysl.uroboros.viewmodel.TagViewModel

@Composable
fun HomePage(
    navController: NavController,
    noteViewModel: NoteViewModel,
    tagViewModel: TagViewModel,
) {
    var isSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTag by rememberSaveable { mutableStateOf<String?>(null) }
    var favouritesOnly by rememberSaveable { mutableStateOf(false) }
    var sortName by rememberSaveable { mutableStateOf(MaterialSort.NEWEST.name) }
    val sort = remember(sortName) { MaterialSort.valueOf(sortName) }

    UroborosTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp, top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!isSearch) {
                    ThemedLogo(
                        modifier = Modifier
                            .size(60.dp)
                            .padding(end = 8.dp),
                    )
                }
                HomeSearchBar(
                    isSearch = isSearch,
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    onToggleSearch = { isSearch = it },
                    modifier = if (isSearch) {
                        Modifier.fillMaxWidth()
                    } else {
                        Modifier.weight(1f)
                    },
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 43.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.home_materials),
                    fontFamily = acherusFeral,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                )
                HomeSortMenu(
                    sort = sort,
                    onSortChange = { selected -> sortName = selected.name },
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.padding(start = 30.dp)) {
                TagHomeContent(
                    tagViewModel = tagViewModel,
                    selectedTag = selectedTag,
                    favouritesOnly = favouritesOnly,
                    onTagSelected = { tag ->
                        selectedTag = tag?.tag
                        favouritesOnly = false
                    },
                    onFavouritesToggle = {
                        favouritesOnly = !favouritesOnly
                        if (favouritesOnly) selectedTag = null
                    },
                    onShowAll = {
                        selectedTag = null
                        favouritesOnly = false
                    },
                )
            }
            val listKey = remember(searchQuery, favouritesOnly, selectedTag) {
                when {
                    searchQuery.isNotEmpty() -> "search:$searchQuery"
                    favouritesOnly -> "favourites"
                    selectedTag != null -> "tag:$selectedTag"
                    else -> "all"
                }
            }
            AnimatedContent(
                targetState = listKey,
                transitionSpec = { contentTransitionSpec() },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                label = "home_materials_list",
            ) { key ->
                when {
                    key.startsWith("search:") -> {
                        NoteSearchHomeContent(
                            modifier = Modifier.padding(bottom = 8.dp),
                            viewModel = noteViewModel,
                            navController = navController,
                            query = key.removePrefix("search:"),
                            sort = sort,
                        )
                    }
                    key == "favourites" -> {
                        NoteFavouriteHomeContent(
                            modifier = Modifier.padding(bottom = 8.dp),
                            noteViewModel = noteViewModel,
                            navController = navController,
                            isFavourite = true,
                            sort = sort,
                        )
                    }
                    key.startsWith("tag:") -> {
                        NoteTagSearchHomeContent(
                            modifier = Modifier.padding(bottom = 8.dp),
                            viewModel = noteViewModel,
                            navController = navController,
                            tag = Tag(tag = key.removePrefix("tag:")),
                            sort = sort,
                        )
                    }
                    else -> {
                        NoteHomeContent(
                            modifier = Modifier.padding(bottom = 8.dp),
                            viewModel = noteViewModel,
                            navController = navController,
                            sort = sort,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemedLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = appLogoRes()),
        contentDescription = "Logo",
        modifier = modifier,
    )
}

@Composable
fun ItemCard(
    item: Tag,
    currentItem: Tag?,
    onClick: (Tag) -> Unit,
) {
    FilterChipCard(
        label = item.tag,
        selected = item == currentItem,
        onClick = { onClick(item) },
    )
}
