package com.reysl.uroboros.view.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reysl.uroboros.R
import com.reysl.uroboros.ui.theme.appGreen
import com.reysl.uroboros.ui.theme.acherusFeral
import com.reysl.uroboros.utils.performHapticTick

@Composable
fun AddMaterialDialog(
    onDismissRequest: () -> Unit,
    onAddMaterial: (String, String, String) -> Unit,
    initialTitle: String = "",
    initialDescription: String = "",
    initialTag: String = "",
) {
    var title by remember { mutableStateOf(initialTitle) }
    var description by remember { mutableStateOf(initialDescription) }
    var tag by remember { mutableStateOf(initialTag) }
    val context = LocalContext.current
    val fieldColors = addMaterialFieldColors()
    val fieldTextStyle = TextStyle(
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onSurface,
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    performHapticTick(context)
                    onAddMaterial(title, description, tag)
                    onDismissRequest()
                },
                colors = ButtonDefaults.textButtonColors(contentColor = appGreen()),
            ) {
                Text(
                    stringResource(R.string.add),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = acherusFeral,
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                colors = ButtonDefaults.textButtonColors(contentColor = appGreen()),
            ) {
                Text(
                    stringResource(R.string.cancel),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = acherusFeral,
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.add_material),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = acherusFeral,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        if (it.length <= 25) {
                            title = it
                        }
                    },
                    label = {
                        Text(
                            text = stringResource(R.string.material_name),
                            fontFamily = acherusFeral,
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textStyle = fieldTextStyle.copy(fontFamily = acherusFeral),
                    colors = fieldColors,
                    trailingIcon = {
                        Text(
                            text = "${title.length}/25",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = acherusFeral,
                            ),
                        )
                    },
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = {
                        Text(
                            text = stringResource(R.string.material_description),
                            fontFamily = acherusFeral,
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textStyle = fieldTextStyle.copy(fontFamily = acherusFeral),
                    colors = fieldColors,
                )
                OutlinedTextField(
                    value = tag,
                    onValueChange = { tag = it },
                    label = {
                        Text(
                            text = stringResource(R.string.material_tag),
                            fontFamily = acherusFeral,
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textStyle = fieldTextStyle.copy(fontFamily = acherusFeral),
                    colors = fieldColors,
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun addMaterialFieldColors(): TextFieldColors {
    val accent = appGreen()
    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorTextColor = MaterialTheme.colorScheme.error,
        focusedLabelColor = accent,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorLabelColor = MaterialTheme.colorScheme.error,
        cursorColor = accent,
        focusedBorderColor = accent,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.38f),
        errorBorderColor = MaterialTheme.colorScheme.error,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
    )
}
