package com.reysl.uroboros.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.reysl.uroboros.R
import com.reysl.uroboros.acherusFeral

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    navController: NavController,
    noteTitle: String,
    noteContent: String,
    noteTag: String,
) {
    val annotatedContent = parseHtmlToAnnotatedString(noteContent)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = noteTitle,
                        fontFamily = acherusFeral,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
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
        floatingActionButton = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FloatingActionButton(
                        onClick = { /* Handle left action */ },
                        containerColor = colorResource(id = R.color.green),
                        contentColor = colorResource(id = R.color.white)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.text),
                            tint = colorResource(id = R.color.card_color),
                            contentDescription = "Left FAB"
                        )
                    }

                    FloatingActionButton(
                        onClick = { /* Handle right action */ },
                        containerColor = colorResource(id = R.color.green),
                        contentColor = colorResource(id = R.color.white)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.success),
                            tint = colorResource(id = R.color.card_color),
                            contentDescription = "Right FAB"
                        )
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
        ) {
            TagSection(tag = noteTag)
            Spacer(modifier = Modifier.height(10.dp))
            TextContent(annotatedContent)
        }
    }
}

@Composable
fun TextContent(annotatedContent: AnnotatedString) {
    Text(
        text = annotatedContent,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        textAlign = TextAlign.Start,
        fontFamily = acherusFeral,
        fontWeight = FontWeight.Light
    )
}




@Composable
fun TagSection(tag: String) {
    Row(
        modifier = Modifier
            .background(Color.Transparent)
            .border(
                BorderStroke(1.dp, colorResource(id = R.color.green)),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = tag,
            color = colorResource(id = R.color.green),
            fontWeight = FontWeight.Bold,
            fontFamily = acherusFeral
        )
    }
}


fun parseHtmlToAnnotatedString(htmlContent: String): AnnotatedString {
    val builder = AnnotatedString.Builder()
    var currentIndex = 0

    while (currentIndex < htmlContent.length) {
        val boldStartIndex = htmlContent.indexOf("<b>", currentIndex)
        val boldEndIndex = htmlContent.indexOf("</b>", currentIndex)

        val italicStartIndex = htmlContent.indexOf("<i>", currentIndex)
        val italicEndIndex = htmlContent.indexOf("</i>", currentIndex)

        val nextTagIndex = listOfNotNull(
            boldStartIndex.takeIf { it != -1 },
            italicStartIndex.takeIf { it != -1 }
        ).minOrNull()

        if (nextTagIndex != null && nextTagIndex > currentIndex) {
            builder.append(htmlContent.substring(currentIndex, nextTagIndex))
            currentIndex = nextTagIndex
        }

        if (boldStartIndex != -1 && boldStartIndex == currentIndex) {
            if (boldEndIndex != -1 && boldEndIndex > boldStartIndex) {
                builder.withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    builder.append(htmlContent.substring(boldStartIndex + 3, boldEndIndex))
                }
                currentIndex = boldEndIndex + 4
            } else {
                throw IllegalArgumentException("Unmatched <b> tag")
            }
        }
        else if (italicStartIndex != -1 && italicStartIndex == currentIndex) {
            if (italicEndIndex != -1 && italicEndIndex > italicStartIndex) {
                builder.withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    builder.append(htmlContent.substring(italicStartIndex + 3, italicEndIndex))
                }
                currentIndex = italicEndIndex + 4
            } else {
                throw IllegalArgumentException("Unmatched <i> tag")
            }
        }
    }

    if (currentIndex < htmlContent.length) {
        builder.append(htmlContent.substring(currentIndex))
    }

    return builder.toAnnotatedString()
}







