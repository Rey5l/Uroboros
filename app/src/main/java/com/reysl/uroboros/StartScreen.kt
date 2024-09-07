package com.reysl.uroboros

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun StartScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background))
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.start_screen_img),
                contentDescription = "MainImage",
                modifier = Modifier
                    .width(350.dp)
                    .height(350.dp)
            )
        }
        // logo
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.uroboros_logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(85.dp)
                )
                Text(
                    text = stringResource(R.string.uroboros),
                    fontFamily = acherusFeral,
                    fontWeight = FontWeight.Bold,
                    fontSize = 35.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }

        }
        // main text
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formattedText,
                textAlign = TextAlign.Center,
                fontFamily = acherusFeral,
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .width(330.dp),
                fontSize = 18.sp,
                color = colorResource(id = R.color.text_color)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(
                    onClick = { navController.navigate("login") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.green),
                        contentColor = colorResource(id = R.color.white)
                    ),
                    modifier = Modifier
                        .padding(top = 34.dp)
                        .width(330.dp)
                        .height(55.dp)
                ) {
                    Text(
                        "Login",
                        fontFamily = acherusFeral,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Button(
                    onClick = { navController.navigate("registration") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.light_green),
                        contentColor = colorResource(id = R.color.black)
                    ),
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .width(330.dp)
                        .height(55.dp)
                ) {
                    Text(
                        "Create a new account",
                        fontFamily = acherusFeral,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

        }

    }
}

val acherusFeral = FontFamily(
    Font(R.font.acherus_feral, FontWeight.Bold),
    Font(R.font.acherus_feral_light, FontWeight.Light)
)

val stSansTrial = FontFamily(
    Font(R.font.stsanstrial_regular, FontWeight.Normal),
    Font(R.font.stsanstrial_bold, FontWeight.Bold)
)

val formattedText = buildAnnotatedString {
    append("Добавляйте материал, который хотите запомнить, и ")
    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
        append("Uroboros ")
    }
    append("будет напоминать вам о нем на основе ")
    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
        append("метода интервальных повторений")
    }
}