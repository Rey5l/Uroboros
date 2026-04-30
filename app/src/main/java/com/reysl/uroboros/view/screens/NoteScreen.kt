package com.reysl.uroboros.view.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import com.reysl.uroboros.R
import com.reysl.uroboros.ui.theme.UroborosTheme
import com.reysl.uroboros.view.components.RichTextToolbar
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
    val state = remember { RichTextState().apply { setMarkdown(noteContent) } }

    var boldSelected by rememberSaveable { mutableStateOf(false) }
    var italicSelected by rememberSaveable { mutableStateOf(false) }
    var underlineSelected by rememberSaveable { mutableStateOf(false) }
    var strikethroughSelected by rememberSaveable { mutableStateOf(false) }
    var titleSelected by rememberSaveable { mutableStateOf(false) }
    var subtitleSelected by rememberSaveable { mutableStateOf(false) }
    var textColorSelected by rememberSaveable { mutableStateOf(false) }
    var linkSelected by rememberSaveable { mutableStateOf(false) }
    var codeSelected by rememberSaveable { mutableStateOf(false) }
    var quoteSelected by rememberSaveable { mutableStateOf(false) }
    var bulletListSelected by rememberSaveable { mutableStateOf(false) }
    var numberListSelected by rememberSaveable { mutableStateOf(false) }
    var alignmentSelected by rememberSaveable { mutableIntStateOf(0) }

    var showLinkDialog by remember { mutableStateOf(false) }
    var linkText by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }

    val titleSize = MaterialTheme.typography.titleLarge.fontSize
    val subtitleSize = MaterialTheme.typography.titleMedium.fontSize

    UroborosTheme {
        if (showLinkDialog) {
            AlertDialog(
                onDismissRequest = {
                    showLinkDialog = false
                    linkSelected = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            state.addLink(text = linkText, url = link)
                            showLinkDialog = false
                            linkSelected = false
                            linkText = ""
                            link = ""
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = colorResource(id = R.color.green))
                    ) {
                        Text("Confirm", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = acherusFeral)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showLinkDialog = false
                            linkSelected = false
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = colorResource(id = R.color.green))
                    ) {
                        Text("Cancel", fontFamily = acherusFeral, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                },
                title = {
                    Text(text = "Add Link", fontFamily = acherusFeral, color = colorResource(R.color.green), fontWeight = FontWeight.Bold)
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
                containerColor = colorResource(id = R.color.light_green)
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = noteTitle,
                            fontFamily = acherusFeral,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = colorResource(id = R.color.white)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate("home") }) {
                            Icon(
                                painter = painterResource(id = R.drawable.back),
                                contentDescription = "Back",
                                tint = colorResource(id = R.color.card_color)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(id = R.color.green))
                )
            },
            bottomBar = {
                RichTextToolbar(
                    onBoldClick = {
                        state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold))
                        boldSelected = !boldSelected
                    },
                    onItalicClick = {
                        state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic))
                        italicSelected = !italicSelected
                    },
                    onUnderlineClick = {
                        state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                        underlineSelected = !underlineSelected
                    },
                    onStrikethroughClick = {
                        state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                        strikethroughSelected = !strikethroughSelected
                    },
                    onTitleClick = {
                        state.toggleSpanStyle(SpanStyle(fontSize = titleSize))
                        titleSelected = !titleSelected
                    },
                    onSubtitleClick = {
                        state.toggleSpanStyle(SpanStyle(fontSize = subtitleSize))
                        subtitleSelected = !subtitleSelected
                    },
                    onTextColorClick = {
                        state.toggleSpanStyle(SpanStyle(color = Color.Red))
                        textColorSelected = !textColorSelected
                    },
                    onLinkClick = {
                        showLinkDialog = true
                        linkSelected = true
                    },
                    onCodeClick = {
                        state.toggleCodeSpan()
                        codeSelected = !codeSelected
                    },
                    onQuoteClick = {
                        quoteSelected = !quoteSelected
                    },
                    onBulletListClick = {
                        state.toggleUnorderedList()
                        bulletListSelected = !bulletListSelected
                    },
                    onNumberListClick = {
                        state.toggleOrderedList()
                        numberListSelected = !numberListSelected
                    },
                    onAlignLeftClick = {
                        state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.Start))
                        alignmentSelected = 0
                    },
                    onAlignCenterClick = {
                        state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.Center))
                        alignmentSelected = 1
                    },
                    onAlignRightClick = {
                        state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.End))
                        alignmentSelected = 2
                    },
                    onUndoClick = { },
                    onRedoClick = { },
                    boldSelected = boldSelected,
                    italicSelected = italicSelected,
                    underlineSelected = underlineSelected,
                    strikethroughSelected = strikethroughSelected,
                    titleSelected = titleSelected,
                    subtitleSelected = subtitleSelected,
                    textColorSelected = textColorSelected,
                    linkSelected = linkSelected,
                    codeSelected = codeSelected,
                    quoteSelected = quoteSelected,
                    bulletListSelected = bulletListSelected,
                    numberListSelected = numberListSelected,
                    alignmentSelected = alignmentSelected
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        val updatedContent = state.toMarkdown()
                        coroutineScope.launch {
                            noteViewModel.noteDao.updateNoteContent(id = noteId, updatedContent)
                        }
                        navController.navigate("home")
                    },
                    containerColor = colorResource(id = R.color.green),
                    contentColor = colorResource(id = R.color.white)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.success),
                        tint = colorResource(id = R.color.card_color),
                        contentDescription = "Save"
                    )
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

                val isDark = isSystemInDarkTheme()
                RichTextEditor(
                    colors = RichTextEditorDefaults.richTextEditorColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        cursorColor = colorResource(R.color.green),
                        selectionColors = TextSelectionColors(
                            handleColor = colorResource(R.color.green),
                            backgroundColor = colorResource(if (isDark) R.color.green else R.color.card_color)
                        )
                    ),
                    modifier = Modifier.fillMaxSize(),
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

@Composable
fun TagSection(tag: String) {
    val isDark = isSystemInDarkTheme()
    val color = if (isDark) R.color.card_color else R.color.green

    UroborosTheme {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = androidx.compose.foundation.BorderStroke(1.dp, colorResource(id = color)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = tag,
                    color = colorResource(id = color),
                    fontWeight = FontWeight.Bold,
                    fontFamily = acherusFeral
                )
            }
        }
    }
}
