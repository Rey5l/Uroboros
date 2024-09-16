package com.reysl.uroboros.view

import android.content.Context
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.reysl.uroboros.R
import com.reysl.uroboros.acherusFeral
import com.reysl.uroboros.data.Note
import com.reysl.uroboros.data.db.note_db.NoteViewModel
import com.reysl.uroboros.scheduleReminder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    navController: NavController,
    noteTitle: String,
    noteContent: String,
    noteTag: String,
) {


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
            TextContent(noteContent)
        }
    }
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

@Composable
fun TextContent(noteContent: String) {
    Text(
        text = noteContent,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        textAlign = TextAlign.Start,
        fontFamily = acherusFeral,
        fontWeight = FontWeight.Light
    )
}



