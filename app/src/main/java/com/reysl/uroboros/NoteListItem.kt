package com.reysl.uroboros

import android.content.ContentProvider
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.reysl.uroboros.data.Note
import com.reysl.uroboros.data.db.note_db.NoteViewModel
import java.text.SimpleDateFormat

@Composable
fun NoteListItem(note: Note, viewModel: NoteViewModel, navController: NavController) {

    val context = LocalContext.current

    var isFilledStar by remember {
        mutableStateOf(false)
    }

    var showDeleteConfirmationDialog by remember {
        mutableStateOf(false)
    }


    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable {
                try {
                    navController.navigate("note_screen/${note.title}/${note.styledText}/${note.tag}")
                } catch (e: Exception) {
                    Toast
                        .makeText(context, "Не получилось открыть материал", Toast.LENGTH_SHORT)
                        .show()
                }
            },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.card_color)),
        shape = RoundedCornerShape(corner = CornerSize(14.dp)),
    ) {
        Row {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        showDeleteConfirmationDialog = true
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.delete),
                            tint = colorResource(id = R.color.green),
                            contentDescription = "Delete",
                            modifier = Modifier.size(25.dp),
                        )
                    }
                    if (showDeleteConfirmationDialog) {
                        DeleteConfirmationDialog(onDeleteConfirmed = {
                            viewModel.deleteAndCleaning(note, context)
                            showDeleteConfirmationDialog = false
                        }, onDismiss = {
                            showDeleteConfirmationDialog = false
                        })
                    }
                    IconButton(onClick = { isFilledStar = !isFilledStar }) {
                        if (isFilledStar) {
                            Icon(
                                painter = painterResource(id = R.drawable.star_filled),
                                tint = colorResource(id = R.color.green),
                                contentDescription = "Favourite",
                                modifier = Modifier.size(23.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.star),
                                tint = colorResource(id = R.color.green),
                                contentDescription = "Favourite",
                                modifier = Modifier.size(23.dp)
                            )
                        }

                    }
                }

                Text(
                    text = note.title,
                    fontFamily = acherusFeral,
                    fontWeight = FontWeight.Bold,
                    fontSize = 23.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = note.description,
                    fontFamily = acherusFeral,
                    fontWeight = FontWeight.Light,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Card(
                        modifier = Modifier.padding(top = 30.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, colorResource(id = R.color.green)),
                        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.card_color))
                    ) {
                        Text(
                            text = note.tag,
                            modifier = Modifier
                                .padding(start = 13.dp)
                                .padding(end = 13.dp)
                                .padding(top = 7.dp)
                                .padding(bottom = 7.dp),
                            fontFamily = acherusFeral,
                            fontWeight = FontWeight.Light,
                            fontSize = 16.sp
                        )
                    }
                    Card(
                        modifier = Modifier.padding(top = 30.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.green)),
                    ) {
                        Text(
                            text = formatTime(note.time),
                            fontFamily = acherusFeral,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colorResource(id = R.color.white),
                            modifier = Modifier
                                .padding(start = 13.dp)
                                .padding(end = 13.dp)
                                .padding(top = 7.dp)
                                .padding(bottom = 7.dp)
                        )
                    }

                }
            }
        }
    }

}


fun formatTime(time: java.util.Date): String {
    val simpleDateFormat = SimpleDateFormat("dd.MM")
    return simpleDateFormat.format(time)
}

@Composable
fun DeleteConfirmationDialog(
    onDeleteConfirmed: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Удалить материал",  
                fontFamily = acherusFeral,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Text(
                "Вы уверены, что хотите удалить материал?",
                fontFamily = acherusFeral,
                fontWeight = FontWeight.Light,
                fontSize = 16.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onDeleteConfirmed, colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.green)
                )
            ) {
                Text(
                    "Удалить",
                    color = colorResource(R.color.card_color),
                    fontFamily = acherusFeral,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss, colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.light_green)
                )
            ) {
                Text(
                    "Отмена",
                    color = colorResource(R.color.green),
                    fontFamily = acherusFeral,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}