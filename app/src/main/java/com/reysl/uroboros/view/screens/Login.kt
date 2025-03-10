package com.reysl.uroboros.view.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.reysl.uroboros.R
import com.reysl.uroboros.ui.theme.UroborosTheme
import com.reysl.uroboros.viewmodel.AuthViewModel
import com.reysl.uroboros.viewmodel.AuthViewModel.AuthState

@Composable
fun Login(navController: NavController, authViewModel: AuthViewModel) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var isPasswordShow by remember { mutableStateOf(false) }

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
    UroborosTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ThemedLogo()
                    Text(
                        text = stringResource(R.string.uroboros),
                        fontFamily = acherusFeral,
                        fontWeight = FontWeight.Bold,
                        fontSize = 35.sp,
                        color = MaterialTheme.colorScheme.scrim,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }

            }
            Spacer(modifier = Modifier.height(58.dp))
            Text(
                text = stringResource(R.string.login),
                color = MaterialTheme.colorScheme.scrim,
                fontFamily = acherusFeral,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                modifier = Modifier.padding(start = 50.dp)
            )
            Spacer(modifier = Modifier.height(45.dp))
            Text(
                text = "Email",
                fontFamily = acherusFeral,
                fontWeight = FontWeight.Light,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 50.dp),
                color = colorResource(id = R.color.registration_color_gray)
            )
            Spacer(modifier = Modifier.height(3.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(text = stringResource(R.string.enter_email))
                },
                modifier = Modifier
                    .padding(start = 50.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.password),
                fontFamily = acherusFeral,
                fontWeight = FontWeight.Light,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 50.dp),
                color = colorResource(id = R.color.registration_color_gray)
            )
            Spacer(modifier = Modifier.height(3.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(text = stringResource(R.string.enter_password))
                },
                trailingIcon = {
                    IconButton(onClick = {isPasswordShow = !isPasswordShow}) {
                        if (isPasswordShow) {
                            Icon(
                                painter = painterResource(id = R.drawable.close_eye),
                                contentDescription = "show"
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.eye),
                                contentDescription = "don't show"
                            )
                        }
                    }
                },
                visualTransformation = if (isPasswordShow) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .padding(start = 50.dp),
            )
            Spacer(modifier = Modifier.height(25.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center

            ) {
                Text(
                    text = stringResource(R.string.dont_have_account),
                    color = MaterialTheme.colorScheme.scrim,
                    modifier = Modifier.clickable { navController.navigate("registration") },
                    textAlign = TextAlign.Center,
                    fontFamily = acherusFeral,
                    fontWeight = FontWeight.Light,
                    fontSize = 16.sp
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { authViewModel.login(email, password) },
                    enabled = authState.value != AuthState.Loading,
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
                        stringResource(R.string.login),
                        fontFamily = acherusFeral,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }

}

@Composable
private fun ThemedLogo() {
    val isDarkTheme = isSystemInDarkTheme()

    if (isDarkTheme) {
        Image(
            painter = painterResource(id = R.drawable.uroboros_logo_dark),
            contentDescription = "Logo",
            modifier = Modifier
                .padding(end = 10.dp)
                .padding(bottom = 10.dp)
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.uroboros_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(85.dp)
        )
    }
}
