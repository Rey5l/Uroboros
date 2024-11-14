package com.reysl.uroboros

import com.reysl.uroboros.ui.theme.UroborosTheme
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.reysl.uroboros.data.db.note_db.NoteViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestNotificationPermission(this)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        val authViewModel: AuthViewModel by viewModels()
        val noteViewModel: NoteViewModel by viewModels()
        setContent {
            val navController = rememberNavController()
            val startDestination = handleIntent(intent)

            UroborosTheme {
                Scaffold { innerPadding ->
                    AppNavigation(
                        modifier = Modifier.padding(innerPadding),
                        authViewModel = authViewModel,
                        noteViewModel = noteViewModel,
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }

        }
    }

    private fun handleIntent(intent: Intent?): String {
        return if (intent?.action == "OPEN_NOTE") {
            val noteId = intent.getLongExtra("note_id", -1L)
            val noteTitle = intent.getStringExtra("note_title") ?: "Unknown title"
            val noteContent = intent.getStringExtra("note_content") ?: "No content available"
            val noteTag = intent.getStringExtra("note_tag") ?: "No tag available"
            if (noteId != -1L) {
                "note_screen/$noteId/$noteTitle/$noteContent/$noteTag"
            } else {
                "main_screen"
            }
        } else {
            "main_screen"
        }
    }
}


