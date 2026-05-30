package com.reysl.uroboros.view.navigation

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.reysl.uroboros.R
import com.reysl.uroboros.data.Note
import com.reysl.uroboros.ui.theme.appGreen
import com.reysl.uroboros.ui.theme.appLightGreenSurface
import com.reysl.uroboros.ui.theme.acherusFeral
import com.reysl.uroboros.utils.SharePayload
import com.reysl.uroboros.utils.noteScreenEnterTransition
import com.reysl.uroboros.utils.noteScreenExitTransition
import com.reysl.uroboros.utils.noteScreenPopEnterTransition
import com.reysl.uroboros.utils.noteScreenPopExitTransition
import com.reysl.uroboros.view.components.AddMaterialDialog
import com.reysl.uroboros.view.pages.home_page.HomePage
import com.reysl.uroboros.view.pages.NotesPage
import com.reysl.uroboros.view.screens.MainScreen
import com.reysl.uroboros.view.screens.NoteScreen
import com.reysl.uroboros.viewmodel.NoteViewModel
import com.reysl.uroboros.viewmodel.ReminderNavigationViewModel
import com.reysl.uroboros.viewmodel.SettingsViewModel
import com.reysl.uroboros.viewmodel.ShareImportViewModel
import com.reysl.uroboros.viewmodel.TagViewModel

@Composable
fun AppNavigation(
    noteViewModel: NoteViewModel,
    shareImportViewModel: ShareImportViewModel,
    settingsViewModel: SettingsViewModel,
    reminderNavigationViewModel: ReminderNavigationViewModel,
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val pendingShare = shareImportViewModel.pendingShare.observeAsState()
    val isLoadingPage = shareImportViewModel.isLoadingPage.observeAsState(false)
    val pendingNoteOpen = reminderNavigationViewModel.pendingNoteOpen.observeAsState()
    val openRequestId = reminderNavigationViewModel.openRequestId.observeAsState(0L)
    val noteNotFound = reminderNavigationViewModel.noteNotFound.observeAsState(false)
    var showShareDialog by remember { mutableStateOf(false) }
    var activeSharePayload by remember { mutableStateOf<SharePayload?>(null) }

    LaunchedEffect(openRequestId.value) {
        if (openRequestId.value == 0L) return@LaunchedEffect
        val note = pendingNoteOpen.value ?: return@LaunchedEffect
        navController.navigate(noteRoute(note)) {
            launchSingleTop = true
        }
        reminderNavigationViewModel.consumePendingOpen()
    }

    LaunchedEffect(noteNotFound.value) {
        if (!noteNotFound.value) return@LaunchedEffect
        Toast.makeText(
            context,
            context.getString(R.string.reminder_note_not_found),
            Toast.LENGTH_SHORT,
        ).show()
        reminderNavigationViewModel.consumeNoteNotFound()
    }

    LaunchedEffect(pendingShare.value, isLoadingPage.value) {
        val payload = pendingShare.value
        if (payload != null && !isLoadingPage.value) {
            activeSharePayload = payload
            showShareDialog = true
            navController.navigate("main_screen") {
                launchSingleTop = true
            }
        }
    }

    if (isLoadingPage.value) {
        AlertDialog(
            onDismissRequest = { shareImportViewModel.consumeShare() },
            containerColor = appLightGreenSurface(),
            title = {
                Text(
                    text = stringResource(R.string.share_loading_page),
                    fontFamily = acherusFeral,
                    fontWeight = FontWeight.Bold,
                    color = appGreen()
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(36.dp),
                        color = appGreen()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.share_loading_page_hint),
                        fontFamily = acherusFeral,
                        fontSize = 14.sp
                    )
                }
            },
            confirmButton = {}
        )
    }

    if (showShareDialog && activeSharePayload != null && !isLoadingPage.value) {
        val payload = activeSharePayload!!
        AddMaterialDialog(
            initialTitle = payload.suggestedTitle,
            initialDescription = payload.suggestedDescription,
            initialTag = payload.suggestedTag,
            onDismissRequest = {
                showShareDialog = false
                activeSharePayload = null
                shareImportViewModel.consumeShare()
            },
            onAddMaterial = { title, description, tag ->
                noteViewModel.addNote(
                    title = title,
                    description = description,
                    tag = tag,
                    markdownText = payload.toStoredContent(title, description),
                    context = context
                )
                showShareDialog = false
                activeSharePayload = null
                shareImportViewModel.consumeShare()
            }
        )
    }

    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") {
            MainScreen(
                navController = navController,
                settingsViewModel = settingsViewModel,
            )
        }
        composable("notes") {
            NotesPage(noteViewModel = noteViewModel)
        }
        composable("home") {
            HomePage(
                navController = navController,
                noteViewModel = noteViewModel,
                tagViewModel = TagViewModel()
            )
        }
        composable(
            route = "note_screen/{noteId}/{noteTitle}/{noteContent}/{noteTag}",
            enterTransition = { noteScreenEnterTransition() },
            exitTransition = { noteScreenExitTransition() },
            popEnterTransition = { noteScreenPopEnterTransition() },
            popExitTransition = { noteScreenPopExitTransition() },
        ) { backStackEntry ->
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
                    noteTag = noteTag,
                )
            } else {
                navController.navigate("main_screen")
            }
        }
    }
}

private fun noteRoute(note: Note): String =
    "note_screen/${note.id}/${Uri.encode(note.title)}/${Uri.encode(note.styledText)}/${Uri.encode(note.tag)}"
