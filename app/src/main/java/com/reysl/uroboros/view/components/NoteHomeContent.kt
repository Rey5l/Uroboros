package com.reysl.uroboros.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.reysl.uroboros.R
import com.reysl.uroboros.ui.theme.appGreen
import com.reysl.uroboros.ui.theme.acherusFeral
import com.reysl.uroboros.utils.MaterialSort
import com.reysl.uroboros.utils.sortMaterials
import com.reysl.uroboros.viewmodel.NoteViewModel

@Composable
fun NoteHomeContent(
    modifier: Modifier = Modifier,
    viewModel: NoteViewModel,
    navController: NavController,
    sort: MaterialSort = MaterialSort.NEWEST,
) {
    val notes by viewModel.noteList.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(true)
    val sortedNotes = remember(notes, sort) {
        notes?.sortMaterials(sort).orEmpty()
    }

    Box(modifier = modifier.fillMaxHeight()) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.Center),
                    color = appGreen(),
                )
            }
            sortedNotes.isNotEmpty() -> {
                LazyColumn(
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp,
                    ),
                    modifier = Modifier.fillMaxSize(),
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
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.empty),
                        contentDescription = "List is empty",
                        modifier = Modifier.size(200.dp),
                    )
                    Text(
                        text = "Материалы не добавлены",
                        fontFamily = acherusFeral,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
