package com.reysl.uroboros.view.pages

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reysl.uroboros.R
import com.reysl.uroboros.ui.theme.appEditorSurface
import com.reysl.uroboros.ui.theme.appGreen
import com.reysl.uroboros.ui.theme.appOnGreenIcon
import com.reysl.uroboros.ui.theme.appOnGreenTopBar
import com.reysl.uroboros.ui.theme.appSecondaryText
import com.reysl.uroboros.utils.MarkdownEditorController
import com.reysl.uroboros.utils.MarkdownStorage
import com.reysl.uroboros.utils.performHapticTick
import com.reysl.uroboros.utils.rememberMarkdownEditorState
import com.reysl.uroboros.view.components.AddMaterialDialog
import com.reysl.uroboros.view.components.ConnectedMarkdownToolbar
import com.reysl.uroboros.view.components.MarkdownEditor
import com.reysl.uroboros.view.components.MarkdownPreview
import com.reysl.uroboros.ui.theme.UroborosTheme
import com.reysl.uroboros.ui.theme.acherusFeral
import com.reysl.uroboros.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesPage(
    noteViewModel: NoteViewModel,
) {
    val context = LocalContext.current
    val state = rememberMarkdownEditorState()
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var showLinkDialog by rememberSaveable { mutableStateOf(false) }
    var isReadMode by rememberSaveable { mutableStateOf(false) }
    var lastSavedTitle by rememberSaveable { mutableStateOf("") }
    var linkText by rememberSaveable { mutableStateOf("") }
    var link by rememberSaveable { mutableStateOf("") }

    val hasContent = state.text.isNotBlank()
    val charCount = state.text.length

    UroborosTheme {
        if (showLinkDialog) {
            LinkInputDialog(
                linkText = linkText,
                link = link,
                onLinkTextChange = { linkText = it },
                onLinkChange = { link = it },
                onDismiss = { showLinkDialog = false },
                onConfirm = {
                    MarkdownEditorController.insertLink(state, linkText, link)
                    showLinkDialog = false
                    linkText = ""
                    link = ""
                }
            )
        }

        if (showDialog) {
            AddMaterialDialog(
                onDismissRequest = { showDialog = false },
                onAddMaterial = { title, description, tag ->
                    noteViewModel.addNote(
                        title = title,
                        description = description,
                        tag = tag,
                        markdownText = MarkdownStorage.save(state),
                        context = context,
                    )
                    performHapticTick(context)
                    lastSavedTitle = title
                    state.clearContent()
                    isReadMode = false
                    showDialog = false
                }
            )
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                NotesPageTopBar(
                    lastSavedTitle = lastSavedTitle,
                    isReadMode = isReadMode,
                    onToggleReadMode = {
                        isReadMode = !isReadMode
                    },
                    onSaveClick = {
                        if (hasContent) {
                            performHapticTick(context)
                            showDialog = true
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.notes_empty_content),
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    },
                )
            },
            bottomBar = {
                if (!isReadMode) {
                    ConnectedMarkdownToolbar(
                        state = state,
                        onLinkClick = { showLinkDialog = true },
                        modifier = Modifier.imePadding(),
                    )
                }
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
            ) {
                NotesEditorCard(
                    isReadMode = isReadMode,
                    hasContent = hasContent,
                    state = state,
                    modifier = Modifier.weight(1f),
                )

                Text(
                    text = stringResource(R.string.notes_char_count, charCount),
                    fontFamily = acherusFeral,
                    fontSize = 12.sp,
                    color = appSecondaryText(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotesPageTopBar(
    lastSavedTitle: String,
    isReadMode: Boolean,
    onToggleReadMode: () -> Unit,
    onSaveClick: () -> Unit,
) {
    TopAppBar(
        windowInsets = WindowInsets(0, 0, 0, 0),
        title = {
            Column {
                Text(
                    text = stringResource(R.string.new_material),
                    fontFamily = acherusFeral,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = appOnGreenTopBar(),
                )
                Text(
                    text = if (lastSavedTitle.isBlank()) {
                        stringResource(R.string.notes_page_subtitle)
                    } else {
                        stringResource(R.string.notes_last_saved, lastSavedTitle)
                    },
                    fontFamily = acherusFeral,
                    fontSize = 12.sp,
                    color = appOnGreenIcon(),
                    lineHeight = 16.sp,
                )
            }
        },
        actions = {
            IconButton(onClick = onToggleReadMode) {
                Icon(
                    painter = painterResource(
                        if (isReadMode) R.drawable.edit_mode else R.drawable.read_mode
                    ),
                    contentDescription = stringResource(
                        if (isReadMode) R.string.markdown_edit_mode else R.string.markdown_read_mode
                    ),
                    tint = appOnGreenIcon(),
                )
            }
            IconButton(onClick = onSaveClick) {
                Icon(
                    painter = painterResource(R.drawable.success),
                    contentDescription = stringResource(R.string.notes_save_material),
                    tint = appOnGreenIcon(),
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = appGreen(),
        ),
    )
}

@Composable
private fun NotesEditorCard(
    isReadMode: Boolean,
    hasContent: Boolean,
    state: com.reysl.uroboros.utils.MarkdownEditorState,
    modifier: Modifier = Modifier,
) {
    val borderColor = appGreen()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = appEditorSurface(),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, borderColor.copy(alpha = 0.25f)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            when {
                isReadMode -> {
                    if (hasContent) {
                        MarkdownPreview(
                            markdown = state.text,
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        NotesEmptyHint()
                    }
                }
                else -> {
                    MarkdownEditor(
                        state = state,
                        modifier = Modifier.fillMaxSize(),
                        textStyle = TextStyle(
                            fontFamily = acherusFeral,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = 24.sp,
                        ),
                        placeholder = stringResource(R.string.notes_empty_hint),
                        placeholderStyle = TextStyle(
                            fontFamily = acherusFeral,
                            fontSize = 15.sp,
                            color = appSecondaryText(),
                            lineHeight = 22.sp,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun NotesEmptyHint() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.notes_empty_hint),
            fontFamily = acherusFeral,
            fontSize = 15.sp,
            color = colorResource(R.color.text_color),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

@Composable
private fun LinkInputDialog(
    linkText: String,
    link: String,
    onLinkTextChange: (String) -> Unit,
    onLinkChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = appGreen()),
            ) {
                Text(
                    text = stringResource(R.string.add),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = acherusFeral,
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = appGreen()),
            ) {
                Text(
                    text = stringResource(R.string.cancel),
                    fontFamily = acherusFeral,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        title = {
            Text(
                text = "Add Link",
                fontFamily = acherusFeral,
                color = colorResource(R.color.green),
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = linkText,
                    onValueChange = onLinkTextChange,
                    label = { Text("Text to display", fontFamily = acherusFeral) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = link,
                    onValueChange = onLinkChange,
                    label = { Text("Link URL", fontFamily = acherusFeral) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        containerColor = colorResource(R.color.light_green),
    )
}
