package com.reysl.uroboros.view.components
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.reysl.uroboros.data.Tag
import com.reysl.uroboros.viewmodel.NoteViewModel

@Composable
fun NoteTagSearchHomeContent(
    modifier: Modifier,
    viewModel: NoteViewModel,
    navController: NavController,
    tag: Tag
) {
    val notes by viewModel.filterNotesByTag(tag.tag).observeAsState(listOf())

    LazyColumn(
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        modifier = modifier
            .fillMaxHeight()
    ) {
        items(
            items = notes,
            itemContent = {
                NoteListItem(note = it, viewModel = viewModel, navController)
            }
        )
    }

}
