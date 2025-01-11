package com.reysl.uroboros.view.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.reysl.uroboros.view.pages.HomePage
import com.reysl.uroboros.view.pages.NotesPage
import com.reysl.uroboros.view.screens.Login
import com.reysl.uroboros.view.screens.MainScreen
import com.reysl.uroboros.view.screens.NoteScreen
import com.reysl.uroboros.view.screens.Registration
import com.reysl.uroboros.view.screens.StartScreen
import com.reysl.uroboros.viewmodel.AuthViewModel
import com.reysl.uroboros.viewmodel.NoteViewModel
import com.reysl.uroboros.viewmodel.TagViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    noteViewModel: NoteViewModel,
) {
    val navController = rememberNavController()
    val showBottomBar = remember {
        mutableStateOf(true)
    }
    val authState = authViewModel.authState.observeAsState()
    val startDestination = if (authState.value is AuthViewModel.AuthState.Authenticated) {
        "main_screen"
    } else {
        "start_screen"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("start_screen") {
            showBottomBar.value = false
            StartScreen(navController)
        }
        composable("login") {
            showBottomBar.value = false
            Login(navController, authViewModel)
        }
        composable("registration") {
            showBottomBar.value = false
            Registration(navController, authViewModel)
        }
        composable("main_screen") {
            showBottomBar.value = true
            MainScreen(navController, authViewModel)
        }
        composable("notes") {
            showBottomBar.value = true
            NotesPage(
                noteViewModel = NoteViewModel(),
            )
        }
        composable("home") {
            showBottomBar.value = true
            HomePage(
                authViewModel = authViewModel,
                navController = navController,
                noteViewModel = noteViewModel,
                tagViewModel = TagViewModel()
            )
        }
        composable("note_screen/{noteId}/{noteTitle}/{noteContent}/{noteTag}") { backStackEntry ->
            showBottomBar.value = false
            val noteId = backStackEntry.arguments?.getString("noteId")?.toLongOrNull() ?: -1L
            val noteTitle =
                Uri.decode(backStackEntry.arguments?.getString("noteTitle")) ?: "Неизвестное имя"
            val noteContent =
                Uri.decode(backStackEntry.arguments?.getString("noteContent"))
                    ?: "Контент недоступен"
            val noteTag = Uri.decode(backStackEntry.arguments?.getString("noteTag")) ?: "Метка"
            if (noteId != -1L) {
                NoteScreen(
                    navController = navController,
                    noteViewModel = noteViewModel,
                    noteId = noteId,
                    noteTitle = noteTitle,
                    noteContent = noteContent,
                    noteTag,
                )
            } else {
                navController.navigate("main_screen")
            }
        }
    }

    if (showBottomBar.value) {
        MainScreen(navController = navController, authViewModel = authViewModel)
    }

}