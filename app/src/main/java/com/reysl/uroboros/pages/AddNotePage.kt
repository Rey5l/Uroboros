package com.reysl.uroboros.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.reysl.uroboros.R
import com.reysl.uroboros.acherusFeral

@Composable
fun AddNotePage(navController: NavController) {

    var tags by remember {
        mutableStateOf("")
    }
    var description by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .background(colorResource(id = R.color.light_green))
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, end = 20.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            IconButton(onClick = { navController.navigate("notes") }) {
                Image(
                    painter = painterResource(id = R.drawable.close_adding),
                    contentDescription = "Close Adding",
                    modifier = Modifier.size(64.dp)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "New",
                fontFamily = acherusFeral,
                fontSize = 55.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 30.dp)
            )
            Spacer(modifier = Modifier.width(15.dp))
            IconButton(onClick = { /*TODO*/ }) {
                Image(
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = "Edit Title",
                    modifier = Modifier.size(64.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
        Box(modifier = Modifier.padding(start = 30.dp)) {
            Column {
                Text(
                    text = "Select a category",
                    fontFamily = acherusFeral,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = tags, onValueChange = { tags = it }, label = {
                    Text(text = "Tags")
                })
                Spacer(modifier = Modifier.height(25.dp))
                Text(
                    text = "Description",
                    fontFamily = acherusFeral,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                BasicTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .padding(top = 16.dp, end = 16.dp)
                        .border(
                            width = 1.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp),
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontFamily = acherusFeral
                    ),
                    decorationBox = { innerTextField ->
                        if (description.isEmpty()) {
                            Text(
                                text = "Enter description here",
                                color = Color.Black,
                                fontSize = 16.sp,
                            )
                        }
                        innerTextField()
                    }
                )
                Spacer(modifier = Modifier.height(25.dp))
                Text(
                    text = "Reminder Time",
                    fontFamily = acherusFeral,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

    }
}