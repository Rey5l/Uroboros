package com.reysl.uroboros

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.reysl.uroboros.data.db.note_db.NoteViewModel
import com.reysl.uroboros.data.db.tag_db.TagViewModel
import com.reysl.uroboros.pages.HomePage
import com.reysl.uroboros.pages.NotesPage
import com.reysl.uroboros.view.NoteScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
) {
    val navController = rememberNavController()
    val showBottomBar = remember {
        mutableStateOf(true)
    }

    NavHost(navController = navController, startDestination = "start_screen") {
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
            MainScreen(modifier, navController, authViewModel)
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
                noteViewModel = NoteViewModel(),
                tagViewModel = TagViewModel()
            )
        }
        composable("note_screen/{noteId}/{title}/{content}/{tag}") { backStackEntry ->
            showBottomBar.value = false
            val noteId = backStackEntry.arguments?.getString("noteId")?.toLongOrNull() ?: -1L
            val noteTitle = Uri.decode(backStackEntry.arguments?.getString("title")) ?: "UnknownTitle"
            val noteContent =
                Uri.decode(backStackEntry.arguments?.getString("content")) ?: "No content available"
            val noteTag = Uri.decode(backStackEntry.arguments?.getString("tag")) ?: "No content available"
            if (noteId != -1L) {
                NoteScreen(
                    navController = navController,
                    noteViewModel = NoteViewModel(),
                    noteId = noteId,
                    noteTitle = noteTitle,
                    noteContent = noteContent,
                    noteTag,
                )
            }
        }
    }

    if (showBottomBar.value) {
        MainScreen(navController = navController, authViewModel = authViewModel)
    }

}