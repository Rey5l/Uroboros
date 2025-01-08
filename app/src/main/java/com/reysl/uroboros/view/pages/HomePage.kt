package com.reysl.uroboros.view.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.reysl.uroboros.viewmodel.AuthViewModel
import com.reysl.uroboros.viewmodel.AuthViewModel.AuthState
import com.reysl.uroboros.view.components.NoteFavouriteHomeContent
import com.reysl.uroboros.view.components.NoteHomeContent
import com.reysl.uroboros.view.components.NoteSearchHomeContent
import com.reysl.uroboros.view.components.NoteTagSearchHomeContent
import com.reysl.uroboros.R
import com.reysl.uroboros.view.components.TagHomeContent
import com.reysl.uroboros.view.screens.acherusFeral
import com.reysl.uroboros.data.Tag
import com.reysl.uroboros.viewmodel.NoteViewModel
import com.reysl.uroboros.viewmodel.TagViewModel
import com.reysl.uroboros.ui.theme.UroborosTheme

@Composable
fun HomePage(
    authViewModel: AuthViewModel,
    navController: NavController,
    noteViewModel: NoteViewModel,
    tagViewModel: TagViewModel
) {

    val authState = authViewModel.authState.observeAsState()

    var isSearch by remember {
        mutableStateOf(false)
    }

    var searchQuery by remember {
        mutableStateOf("")
    }

    var tagName by remember {
        mutableStateOf<Tag?>(null)
    }

    var isFavouriteMaterial by remember {
        mutableStateOf<Boolean?>(null)
    }


    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }


    UroborosTheme {
        Scaffold(
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp, end = 30.dp, top = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ThemedLogo()
                        if (isSearch) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                trailingIcon = {
                                    IconButton(onClick = {
                                        isSearch = false
                                        searchQuery = ""
                                    }) {
                                        Image(
                                            painter = painterResource(id = R.drawable.search_icon),
                                            contentDescription = "Search",
                                            modifier = Modifier.size(35.dp),
                                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                                        )
                                    }
                                }
                            )
                        } else {
                            IconButton(onClick = { isSearch = true }) {
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
                    Text(
                        text = "Метки",
                        modifier = Modifier.padding(start = 43.dp),
                        fontFamily = acherusFeral,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                    Row(
                        modifier = Modifier.padding(start = 43.dp)
                    ) {
                        TagHomeContent(
                            tagViewModel = tagViewModel,
                            onTagSelected = { selectedTag ->
                                tagName = selectedTag
                            },
                            onFavouriteSelected = { isFavourite ->
                                isFavouriteMaterial = if (isFavourite) true else null
                            })
                    }
                    if (searchQuery.isNotEmpty()) {
                        NoteSearchHomeContent(
                            modifier = Modifier.padding(bottom = 60.dp),
                            viewModel = noteViewModel,
                            navController = navController,
                            query = searchQuery
                        )
                    } else if (tagName != null) {
                        NoteTagSearchHomeContent(
                            modifier = Modifier.padding(bottom = 60.dp),
                            viewModel = noteViewModel,
                            navController = navController,
                            tag = tagName!!
                        )
                    } else if (isFavouriteMaterial != null) {
                        NoteFavouriteHomeContent(
                            modifier = Modifier.padding(bottom = 60.dp),
                            noteViewModel = noteViewModel,
                            navController = navController,
                            isFavourite = isFavouriteMaterial!!
                        )
                    } else {
                        NoteHomeContent(
                            modifier = Modifier.padding(bottom = 60.dp),
                            viewModel = noteViewModel,
                            navController = navController
                        )
                    }

                }
            }
        )
    }
}


@Composable
private fun ThemedLogo() {
    val isDark = isSystemInDarkTheme()

    if (isDark) {
        Image(
            painter = painterResource(id = R.drawable.uroboros_logo_dark),
            contentDescription = "Logo",
            modifier = Modifier
                .padding(end = 10.dp, bottom = 10.dp, start = 13.dp)
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.uroboros_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(85.dp)
        )
    }
}


@Composable
fun ItemCard(
    item: Tag,
    currentItem: Tag?,
    onClick: (Tag) -> Unit
) {
    val isCurrent = item == currentItem

    UroborosTheme {
        Card(
            modifier = Modifier
                .padding(4.dp)
                .border(
                    BorderStroke(1.dp, colorResource(id = R.color.green)),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { onClick(item) },
            colors = CardDefaults.cardColors(containerColor = if (isCurrent) colorResource(id = R.color.green) else Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.tag,
                    fontFamily = acherusFeral,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Light,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.Center),
                    color = if (isCurrent) colorResource(R.color.card_color) else MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

