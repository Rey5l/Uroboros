package com.reysl.uroboros

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.reysl.uroboros.AuthViewModel.*
import com.reysl.uroboros.data.db.note_db.NoteViewModel
import com.reysl.uroboros.data.db.tag_db.TagViewModel
import com.reysl.uroboros.pages.HomePage
import com.reysl.uroboros.pages.NotesPage
import com.reysl.uroboros.pages.ProfilePage

@Composable
fun MainScreen(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    var selectedIndex by remember {
        mutableIntStateOf(1)
    }

    val navItemList = listOf(
        NavItem("Notes", painterResource(id = R.drawable.notes), selectedIndex == 0),
        NavItem("Home", painterResource(id = R.drawable.home2), selectedIndex == 1),
        NavItem("Profile", painterResource(id = R.drawable.profile), selectedIndex == 2)
    )



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.background(colorResource(id = R.color.navbar_bg))
            ) {
                navItemList.forEachIndexed { index, navItem ->
                    val interactionSource = remember { MutableInteractionSource() }
                    val isHovered by interactionSource.collectIsHoveredAsState()

                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                        },
                        icon = {
                            Icon(
                                painter = navItem.icon,
                                contentDescription = "${navItem.label}",
                                modifier = Modifier.size(23.dp)
                            )
                        },
                        label = {
                            Text(
                                text = navItem.label,
                                fontSize = 12.sp,
                                fontFamily = acherusFeral,
                                fontWeight = if (isHovered) FontWeight.Bold else FontWeight.Light
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = colorResource(id = R.color.white),
                            unselectedIconColor = colorResource(id = R.color.black),
                            selectedTextColor = colorResource(id = R.color.green),
                            unselectedTextColor = colorResource(id = R.color.black),
                            indicatorColor = colorResource(id = R.color.green)
                        ),
                        interactionSource = interactionSource
                    )
                }
            }
        }
    ) { innerPadding ->
        ContentScreen(modifier = Modifier.padding(innerPadding), index = selectedIndex, navController, authViewModel)
    }

}

@Composable
fun ContentScreen(modifier: Modifier, index: Int, navController: NavController, authViewModel: AuthViewModel) {
    when (index) {
        0 -> NotesPage(authViewModel, navController, noteViewModel = NoteViewModel(), tagViewModel = TagViewModel())
        1 -> HomePage(authViewModel, navController, noteViewModel = NoteViewModel(), tagViewModel = TagViewModel())
        2 -> ProfilePage(authViewModel, navController)
    }
}

