package com.reysl.uroboros

import android.app.TimePickerDialog
import android.widget.TimePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

@Composable
fun AddMaterialDialog(
    onDismissRequest: () -> Unit,
    onAddMaterial: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    var notificationTime by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val timePickerDialog = TimePickerDialog(
        context,
        { _: TimePicker, hour: Int, minute: Int ->
            notificationTime = String.format("%02d:%02d", hour, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    onAddMaterial(title, description, tag)
                    onDismissRequest()
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = colorResource(id = R.color.green)
                )
            ) {
                Text(
                    "Добавить",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = acherusFeral
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = colorResource(id = R.color.green)
                )
            ) {
                Text("Отмена", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = acherusFeral)
            }
        },
        title = {
            Text(
                text = "Добавить материал",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = acherusFeral,
                color = colorResource(id = R.color.black)
            )
        },
        text = {
            Column(modifier = Modifier.background(colorResource(id = R.color.light_green))) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название", fontFamily = acherusFeral) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = colorResource(id = R.color.black)
                    )
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание", fontFamily = acherusFeral) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = colorResource(id = R.color.black)
                    )
                )
                OutlinedTextField(
                    value = tag,
                    onValueChange = { tag = it },
                    label = { Text("Метка", fontFamily = acherusFeral) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = colorResource(id = R.color.black)
                    )
                )
                OutlinedTextField(
                    value = notificationTime,
                    onValueChange = { notificationTime = it },
                    label = { Text("Время уведомления", fontFamily = acherusFeral) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable { timePickerDialog.show() },
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = colorResource(id = R.color.black)
                    ),
                    enabled = false
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = colorResource(id = R.color.light_green)
    )
}
