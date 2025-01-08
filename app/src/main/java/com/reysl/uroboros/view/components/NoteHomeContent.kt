package com.reysl.uroboros.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.reysl.uroboros.R
import com.reysl.uroboros.view.screens.acherusFeral
import com.reysl.uroboros.viewmodel.NoteViewModel

@Composable
fun NoteHomeContent(
    modifier: Modifier = Modifier,
    viewModel: NoteViewModel,
    navController: NavController
) {

    val notes by viewModel.noteList.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(true)

    AnimatedVisibility(visible = isLoading) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

        }
    }

    if (!isLoading && !notes.isNullOrEmpty()) {
        LazyColumn(
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            modifier = modifier.fillMaxHeight()
        ) {
            items(
                items = notes!!,
                itemContent = { note ->
                    NoteListItem(note = note, viewModel = viewModel, navController)
                }
            )
        }
    } else if (!isLoading) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.empty),
                contentDescription = "List is empty",
                modifier = Modifier.size(200.dp)
            )
            Text(
                text = "Материалы не добавлены",
                fontFamily = acherusFeral,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }

}