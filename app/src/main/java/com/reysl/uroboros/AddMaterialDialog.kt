package com.reysl.uroboros

import com.reysl.uroboros.ui.theme.UroborosTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddMaterialDialog(
    onDismissRequest: () -> Unit,
    onAddMaterial: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }

    UroborosTheme {
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
                        label = { Text("Название", fontFamily = acherusFeral, color = Color.Black) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(R.color.green),
                            unfocusedBorderColor = Color.Gray,
                            disabledBorderColor = Color.Gray,
                            cursorColor = Color.Black
                        )
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Описание", fontFamily = acherusFeral, color = Color.Black) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(R.color.green),
                            unfocusedBorderColor = Color.Gray,
                            disabledBorderColor = Color.Gray,
                            cursorColor = Color.Black
                        )
                    )
                    OutlinedTextField(
                        value = tag,
                        onValueChange = { tag = it },
                        label = { Text("Метка", fontFamily = acherusFeral, color = Color.Black) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(R.color.green),
                            unfocusedBorderColor = Color.Gray,
                            disabledBorderColor = Color.Gray,
                            cursorColor = Color.Black
                        )
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = colorResource(id = R.color.light_green)
        )
    }
}
