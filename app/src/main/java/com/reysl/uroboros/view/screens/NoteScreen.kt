package com.reysl.uroboros.view.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.width
import com.reysl.uroboros.ui.theme.appButtonOnGreen
import com.reysl.uroboros.ui.theme.appChipSelectedText
import com.reysl.uroboros.ui.theme.appGreen
import com.reysl.uroboros.ui.theme.appLightGreenSurface
import com.reysl.uroboros.ui.theme.appOnGreenIcon
import com.reysl.uroboros.ui.theme.appOnGreenTopBar
import com.reysl.uroboros.ui.theme.isAppInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.reysl.uroboros.R
import com.reysl.uroboros.ui.theme.UroborosTheme
import com.reysl.uroboros.ui.theme.acherusFeral
import com.reysl.uroboros.utils.KnowledgeCheckGenerator
import com.reysl.uroboros.utils.KnowledgeCheckSegment
import com.reysl.uroboros.utils.MarkdownEditorController
import com.reysl.uroboros.utils.MarkdownEditorState
import com.reysl.uroboros.utils.MarkdownStorage
import com.reysl.uroboros.utils.contentTransitionSpec
import com.reysl.uroboros.view.components.ConnectedMarkdownToolbar
import com.reysl.uroboros.view.components.KnowledgeCheckText
import com.reysl.uroboros.view.components.MarkdownEditor
import com.reysl.uroboros.view.components.MarkdownPreview
import com.reysl.uroboros.viewmodel.NoteViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    navController: NavController,
    noteViewModel: NoteViewModel,
    noteId: Long,
    noteTitle: String,
    noteContent: String,
    noteTag: String,
) {
    val coroutineScope = rememberCoroutineScope()
    val state = remember(noteContent) {
        MarkdownEditorState(MarkdownStorage.normalize(noteContent))
    }

    var showLinkDialog by remember { mutableStateOf(false) }
    var linkText by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }

    var isKnowledgeCheckMode by rememberSaveable { mutableStateOf(false) }
    var isReadMode by rememberSaveable { mutableStateOf(false) }
    var knowledgeCheckSegments by remember { mutableStateOf<List<KnowledgeCheckSegment>>(emptyList()) }
    var revealedWordIds by remember { mutableStateOf(setOf<Int>()) }

    UroborosTheme {
        if (showLinkDialog) {
            AlertDialog(
                onDismissRequest = { showLinkDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            MarkdownEditorController.insertLink(state, linkText, link)
                            showLinkDialog = false
                            linkText = ""
                            link = ""
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = appGreen())
                    ) {
                        Text("Confirm", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = acherusFeral)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showLinkDialog = false },
                        colors = ButtonDefaults.textButtonColors(contentColor = appGreen())
                    ) {
                        Text("Cancel", fontFamily = acherusFeral, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                },
                title = {
                    Text(text = "Add Link", fontFamily = acherusFeral, color = appGreen(), fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = linkText,
                            onValueChange = { linkText = it },
                            label = { Text("Text to display", fontFamily = acherusFeral) }
                        )
                        OutlinedTextField(
                            value = link,
                            onValueChange = { link = it },
                            label = { Text("Link URL", fontFamily = acherusFeral) }
                        )
                    }
                },
                containerColor = appLightGreenSurface()
            )
        }

        Scaffold(
            modifier = Modifier.imePadding(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = noteTitle,
                            fontFamily = acherusFeral,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = appOnGreenTopBar()
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.back),
                                contentDescription = "Back",
                                tint = appOnGreenIcon()
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                if (isKnowledgeCheckMode) {
                                    isKnowledgeCheckMode = false
                                    knowledgeCheckSegments = emptyList()
                                    revealedWordIds = emptySet()
                                } else {
                                    isReadMode = false
                                    val plainText = MarkdownStorage.plainText(state.text)
                                    knowledgeCheckSegments = KnowledgeCheckGenerator.generate(
                                        plainText = plainText,
                                        seed = noteId
                                    )
                                    revealedWordIds = emptySet()
                                    isKnowledgeCheckMode = true
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (isKnowledgeCheckMode) R.drawable.eye else R.drawable.instruction
                                ),
                                contentDescription = stringResource(
                                    if (isKnowledgeCheckMode) {
                                        R.string.knowledge_check_on
                                    } else {
                                        R.string.knowledge_check
                                    }
                                ),
                                tint = appOnGreenIcon()
                            )
                        }
                        IconButton(
                            onClick = {
                                if (isReadMode) {
                                    isReadMode = false
                                } else {
                                    isKnowledgeCheckMode = false
                                    knowledgeCheckSegments = emptyList()
                                    revealedWordIds = emptySet()
                                    isReadMode = true
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (isReadMode) R.drawable.edit else R.drawable.instruction
                                ),
                                contentDescription = stringResource(
                                    if (isReadMode) {
                                        R.string.markdown_edit_mode
                                    } else {
                                        R.string.markdown_read_mode
                                    }
                                ),
                                tint = appOnGreenIcon()
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = appGreen())
                )
            },
            bottomBar = {
                if (!isKnowledgeCheckMode && !isReadMode) {
                    ConnectedMarkdownToolbar(
                        state = state,
                        onLinkClick = { showLinkDialog = true }
                    )
                }
            },
            floatingActionButton = {
                if (!isKnowledgeCheckMode && !isReadMode) {
                    FloatingActionButton(
                        onClick = {
                            val updatedContent = MarkdownStorage.save(state)
                            coroutineScope.launch {
                                noteViewModel.noteDao.updateNoteContent(id = noteId, updatedContent)
                            }
                            navController.popBackStack()
                        },
                        containerColor = appGreen(),
                        contentColor = appOnGreenTopBar()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.success),
                            tint = appOnGreenIcon(),
                            contentDescription = "Save"
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                TagSection(tag = noteTag)
                Spacer(modifier = Modifier.height(10.dp))

                val editorMode = when {
                    isKnowledgeCheckMode -> NoteEditorMode.KnowledgeCheck
                    isReadMode -> NoteEditorMode.Read
                    else -> NoteEditorMode.Edit
                }
                AnimatedContent(
                    targetState = editorMode,
                    transitionSpec = { contentTransitionSpec() },
                    modifier = Modifier.fillMaxSize(),
                    label = "note_editor_mode",
                ) { mode ->
                    when (mode) {
                        NoteEditorMode.KnowledgeCheck -> {
                            Column(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = stringResource(R.string.knowledge_check_hint),
                                    fontFamily = acherusFeral,
                                    fontSize = 13.sp,
                                    color = appGreen(),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                KnowledgeCheckText(
                                    segments = knowledgeCheckSegments,
                                    revealedWordIds = revealedWordIds,
                                    onRevealWord = { wordId ->
                                        revealedWordIds = revealedWordIds + wordId
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                )
                            }
                        }
                        NoteEditorMode.Read -> {
                            MarkdownPreview(
                                markdown = state.text,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        NoteEditorMode.Edit -> {
                            MarkdownEditor(
                                state = state,
                                textStyle = TextStyle(
                                    fontFamily = acherusFeral,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

private enum class NoteEditorMode {
    Edit,
    Read,
    KnowledgeCheck,
}

@Composable
fun TagSection(tag: String) {
    val borderAndTextColor = if (isAppInDarkTheme()) appChipSelectedText() else appGreen()

    UroborosTheme {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = androidx.compose.foundation.BorderStroke(1.dp, borderAndTextColor),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = tag,
                    color = borderAndTextColor,
                    fontWeight = FontWeight.Bold,
                    fontFamily = acherusFeral
                )
            }
        }
    }
}
