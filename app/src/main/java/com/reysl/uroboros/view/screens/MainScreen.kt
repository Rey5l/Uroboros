package com.reysl.uroboros.view.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.reysl.uroboros.R
import com.reysl.uroboros.ui.theme.UroborosTheme
import com.reysl.uroboros.ui.theme.acherusFeral
import com.reysl.uroboros.ui.theme.appGreen
import com.reysl.uroboros.ui.theme.appNavSelectedIcon
import com.reysl.uroboros.ui.theme.appNavbarBackground
import com.reysl.uroboros.utils.tabTransitionSpec
import com.reysl.uroboros.view.navigation.NavItem
import com.reysl.uroboros.view.pages.SettingsPage
import com.reysl.uroboros.view.pages.home_page.HomePage
import com.reysl.uroboros.view.pages.NotesPage
import com.reysl.uroboros.viewmodel.NoteViewModel
import com.reysl.uroboros.viewmodel.SettingsViewModel
import com.reysl.uroboros.viewmodel.TagViewModel

@Composable
fun MainScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
) {
    val noteViewModel: NoteViewModel = viewModel()
    val tagViewModel: TagViewModel = viewModel()

    val initialTabIndex = remember { settingsViewModel.startTab.value.tabIndex }
    var selectedIndex by remember { mutableIntStateOf(initialTabIndex) }

    val navItemList = listOf(
        NavItem(stringResource(R.string.notes), painterResource(id = R.drawable.notes), selectedIndex == 0),
        NavItem(stringResource(R.string.home), painterResource(id = R.drawable.home2), selectedIndex == 1),
        NavItem(stringResource(R.string.settings), null, selectedIndex == 2, Icons.Default.Settings),
    )

    val imeBottom = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    val showBottomBar = !(imeBottom > 0.dp && selectedIndex == 0)

    UroborosTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        modifier = Modifier.background(appNavbarBackground())
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
                                    if (navItem.vectorIcon != null) {
                                        Icon(
                                            imageVector = navItem.vectorIcon,
                                            contentDescription = navItem.label,
                                            modifier = Modifier.size(23.dp),
                                        )
                                    } else {
                                        Icon(
                                            painter = navItem.icon!!,
                                            contentDescription = navItem.label,
                                            modifier = Modifier.size(23.dp),
                                        )
                                    }
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
                                    selectedIconColor = appNavSelectedIcon(),
                                    unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                    unselectedTextColor = MaterialTheme.colorScheme.onBackground,
                                    indicatorColor = appGreen()
                                ),
                                interactionSource = interactionSource
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            ContentScreen(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                index = selectedIndex,
                navController = navController,
                noteViewModel = noteViewModel,
                tagViewModel = tagViewModel,
                settingsViewModel = settingsViewModel,
            )
        }
    }
}

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    index: Int,
    navController: NavController,
    noteViewModel: NoteViewModel,
    tagViewModel: TagViewModel,
    settingsViewModel: SettingsViewModel,
) {
    AnimatedContent(
        targetState = index,
        transitionSpec = { tabTransitionSpec(initialState, targetState) },
        modifier = modifier,
        label = "main_tab_content",
    ) { tabIndex ->
        Box(Modifier.fillMaxSize()) {
            when (tabIndex) {
                0 -> NotesPage(noteViewModel = noteViewModel)
                1 -> HomePage(navController, noteViewModel = noteViewModel, tagViewModel = tagViewModel)
                2 -> SettingsPage(
                    noteViewModel = noteViewModel,
                    settingsViewModel = settingsViewModel,
                )
            }
        }
    }
}
