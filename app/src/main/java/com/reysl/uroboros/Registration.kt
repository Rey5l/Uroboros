package com.reysl.uroboros

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.reysl.uroboros.AuthViewModel.*

@Composable
fun Registration(navController: NavController, authViewModel: AuthViewModel) {

    var username by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> navController.navigate("main_screen")
            is AuthState.Error -> Toast.makeText(
                context,
                (authState.value as AuthState.Error).message,
                Toast.LENGTH_SHORT
            ).show()
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background)),

        ) {
        IconButton(
            onClick = { navController.navigate("login") },
            modifier = Modifier.padding(top = 30.dp)
        ) {
            Icon(painter = painterResource(id = R.drawable.back), contentDescription = "Back")
        }
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter,

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
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Sign Up",
            fontFamily = acherusFeral,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            modifier = Modifier.padding(start = 50.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Username",
            fontFamily = acherusFeral,
            fontWeight = FontWeight.Light,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 50.dp),
            color = colorResource(id = R.color.registration_color_gray)
        )
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(text = "Введите ваше имя") },
            modifier = Modifier.padding(start = 50.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Email",
            fontFamily = acherusFeral,
            fontWeight = FontWeight.Light,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 50.dp),
            color = colorResource(id = R.color.registration_color_gray)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = {
                Text(text = "Введите почту")
            },
            modifier = Modifier
                .padding(start = 50.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Password",
            fontFamily = acherusFeral,
            fontWeight = FontWeight.Light,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 50.dp),
            color = colorResource(id = R.color.registration_color_gray)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {
                Text(text = "Введите пароль")
            },
            modifier = Modifier
                .padding(start = 50.dp),
            visualTransformation = PasswordVisualTransformation()
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = { authViewModel.registration(email, password) },
                enabled = authState.value != AuthState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.green),
                    contentColor = colorResource(id = R.color.white)
                ),
                modifier = Modifier
                    .padding(top = 35.dp)
                    .width(330.dp)
                    .height(55.dp)
            ) {
                Text(
                    "Sign Up",
                    fontFamily = acherusFeral,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

    }
}