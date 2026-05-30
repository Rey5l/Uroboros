package com.reysl.uroboros.view.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.reysl.uroboros.utils.MaterialSort
import com.reysl.uroboros.utils.sortMaterials
import com.reysl.uroboros.viewmodel.NoteViewModel

@Composable
fun NoteSearchHomeContent(
    modifier: Modifier = Modifier,
    viewModel: NoteViewModel,
    navController: NavController,
    query: String,
    sort: MaterialSort = MaterialSort.NEWEST,
) {
    val notes by viewModel.searchNote(query).observeAsState(listOf())
    val sortedNotes = remember(notes, sort) { notes.sortMaterials(sort) }

    LazyColumn(
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        modifier = modifier
            .fillMaxHeight()
    ) {
        items(
            items = sortedNotes,
            key = { it.id },
        ) { note ->
            NoteListItem(
                note = note,
                viewModel = viewModel,
                navController = navController,
            )
        }
    }

}
