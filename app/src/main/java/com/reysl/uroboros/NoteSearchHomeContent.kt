package com.reysl.uroboros

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
import com.reysl.uroboros.data.db.note_db.NoteViewModel

@Composable
fun NoteSearchHomeContent(
    modifier: Modifier = Modifier,
    viewModel: NoteViewModel,
    navController: NavController,
    query: String
) {
    val notes by viewModel.searchNote(query).observeAsState(listOf())

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
