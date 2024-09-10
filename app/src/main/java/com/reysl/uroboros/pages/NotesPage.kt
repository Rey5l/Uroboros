package com.reysl.uroboros.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.reysl.uroboros.AddMaterialDialog
import com.reysl.uroboros.AuthViewModel
import com.reysl.uroboros.data.db.note_db.NoteViewModel
import com.reysl.uroboros.R
import com.reysl.uroboros.acherusFeral
import com.reysl.uroboros.data.db.tag_db.TagViewModel
import java.sql.Date
import java.text.SimpleDateFormat

@Composable
fun NotesPage(
    authViewModel: AuthViewModel,
    navController: NavController,
    noteViewModel: NoteViewModel,
    tagViewModel: TagViewModel
) {

    var showDialog by remember { mutableStateOf(false) }
    var latestTitle by remember { mutableStateOf("") }
    var styledText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxHeight(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .background(colorResource(id = R.color.green))
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 20.dp, top = 30.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    IconButton(onClick = { showDialog = true }) {
                        Image(
                            painter = painterResource(id = R.drawable.adding),
                            contentDescription = "Add note",
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
                if (latestTitle == "") {
                    Text(
                        text = "New",
                        fontFamily = acherusFeral,
                        fontWeight = FontWeight.Bold,
                        fontSize = 39.sp,
                        modifier = Modifier.padding(start = 30.dp),
                        color = colorResource(id = R.color.white),
                        style = TextStyle(lineHeight = 50.sp)
                    )
                } else {
                    Text(
                        text = latestTitle,
                        fontFamily = acherusFeral,
                        fontWeight = FontWeight.Bold,
                        fontSize = 39.sp,
                        modifier = Modifier.padding(start = 30.dp),
                        color = colorResource(id = R.color.white),
                        style = TextStyle(lineHeight = 50.sp)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 20.dp, bottom = 20.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
//                    Card(
//                        shape = RoundedCornerShape(8.dp),
//                        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.light_green)),
//                    ) {
//                        Text(
//                            text = formatTime(java.util.Date()),
//                            fontFamily = acherusFeral,
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 16.sp,
//                            color = colorResource(id = R.color.green),
//                            modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp)
//                        )
//                    } // card with time
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(colorResource(id = R.color.white))
        ) {
            RichTextEditor(
                initialText = styledText,
                onTextChange = { updatedText ->
                    styledText = updatedText
                }
            )
        }
    }
    if (showDialog) {
        AddMaterialDialog(
            onDismissRequest = { showDialog = false },
            onAddMaterial = { title, description, tag ->
                noteViewModel.addNote(title, description, tag, styledText, context)
                showDialog = false
                latestTitle = title
            }
        )
    }
}

@Composable
fun RichTextEditor(
    onTextChange: (String) -> Unit,
    initialText: String = ""
) {
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = initialText,
                selection = TextRange(0, 0)
            )
        )
    }

    var styledText by remember {
        mutableStateOf(
            AnnotatedString(textFieldValue.text)
        )
    }

    val applyStyleToSelection = { style: SpanStyle ->
        val start = textFieldValue.selection.min.coerceIn(0, styledText.length)
        val end = textFieldValue.selection.max.coerceIn(0, styledText.length)

        val newAnnotatedString = buildAnnotatedString {
            append(styledText)
            if (start < end) {
                addStyle(style, start, end)
            }
        }
        styledText = newAnnotatedString
        textFieldValue = textFieldValue.copy(
            annotatedString = newAnnotatedString,
            selection = textFieldValue.selection
        )
        onTextChange(styledText.text)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Button(
                onClick = {
                    applyStyleToSelection(SpanStyle(fontWeight = FontWeight.Bold))
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.green))
            ) {
                Text(
                    "Bold",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = acherusFeral
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    applyStyleToSelection(SpanStyle(fontStyle = FontStyle.Italic))
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.green))
            ) {
                Text(
                    "Italic",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = acherusFeral
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    applyStyleToSelection(SpanStyle(fontSize = 20.sp))
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.green))
            ) {
                Text(
                    "Large",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = acherusFeral
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            BasicTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    textFieldValue = newValue.copy(
                        annotatedString = buildAnnotatedString {
                            append(newValue.text)
                            styledText.spanStyles.forEach { spanStyle ->
                                addStyle(
                                    spanStyle.item,
                                    spanStyle.start.coerceIn(0, newValue.text.length),
                                    spanStyle.end.coerceIn(0, newValue.text.length)
                                )
                            }
                        }
                    )
                    styledText = buildAnnotatedString {
                        append(newValue.text)
                        styledText.spanStyles.forEach { spanStyle ->
                            addStyle(
                                spanStyle.item,
                                spanStyle.start.coerceIn(0, newValue.text.length),
                                spanStyle.end.coerceIn(0, newValue.text.length)
                            )
                        }
                    }
                    onTextChange(styledText.text)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(5.dp),
                textStyle = TextStyle(
                    color = Color.Black,
                    fontFamily = acherusFeral,
                    fontSize = 16.sp
                ),
                decorationBox = { innerTextField ->
                    if (textFieldValue.text.isEmpty()) {
                        Text(
                            text = "Напишите здесь текст",
                            color = Color.Gray,
                            fontFamily = acherusFeral,
                            fontSize = 18.sp
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}


fun formatTime(time: Date): String {
    val simpleDateFormat = SimpleDateFormat("dd.MM")
    return simpleDateFormat.format(time)
}

fun AnnotatedString.toHtml(): String {
    val sb = StringBuilder()

    var currentPosition = 0
    for (styleRange in this.spanStyles) {
        sb.append(this.text.substring(currentPosition, styleRange.start))
        val styledText = this.text.substring(styleRange.start, styleRange.end)
        when {
            styleRange.item.fontWeight == FontWeight.Bold -> sb.append("<b>$styledText</b>")
            styleRange.item.fontStyle == FontStyle.Italic -> sb.append("<i>$styledText</i>")
            else -> sb.append(styledText)
        }
        currentPosition = styleRange.end
    }
    if (currentPosition < this.text.length) {
        sb.append(this.text.substring(currentPosition))
    }

    return sb.toString()
}

fun String.fromHtmlToAnnotatedString(): AnnotatedString {
    val annotatedStringBuilder = AnnotatedString.Builder()
    var currentPosition = 0
    var remainingText = this

    while (remainingText.isNotEmpty()) {
        when {
            remainingText.startsWith("<b>") -> {
                val boldText = remainingText.substringAfter("<b>").substringBefore("</b>")
                annotatedStringBuilder.append(
                    boldText,
                    SpanStyle(fontWeight = FontWeight.Bold).toString()
                )
                remainingText = remainingText.substringAfter("</b>")
            }
            remainingText.startsWith("<i>") -> {
                val italicText = remainingText.substringAfter("<i>").substringBefore("</i>")
                annotatedStringBuilder.append(
                    italicText,
                    SpanStyle(fontStyle = FontStyle.Italic).toString()
                )
                remainingText = remainingText.substringAfter("</i>")
            }
            else -> {
                val normalText = remainingText.takeWhile { it != '<' }
                annotatedStringBuilder.append(normalText)
                remainingText = remainingText.drop(normalText.length)
            }
        }
    }

    return annotatedStringBuilder.toAnnotatedString()
}