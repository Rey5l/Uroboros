package com.reysl.uroboros.view.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.reysl.uroboros.R
import com.reysl.uroboros.ui.theme.UroborosTheme
import com.reysl.uroboros.viewmodel.AuthViewModel
import com.reysl.uroboros.viewmodel.AuthViewModel.AuthState

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

    var isPasswordShow by remember { mutableStateOf(false) }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser

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
            IconButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier.padding(top = 30.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.scrim
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter,

                ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ThemedLogo()
                    Text(
                        text = stringResource(R.string.uroboros),
                        color = MaterialTheme.colorScheme.scrim,
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
                text = stringResource(R.string.registration),
                fontFamily = acherusFeral,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                modifier = Modifier.padding(start = 50.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.username),
                fontFamily = acherusFeral,
                fontWeight = FontWeight.Light,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 50.dp),
                color = colorResource(id = R.color.registration_color_gray)
            )
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(text = stringResource(R.string.enter_username)) },
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
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(text = stringResource(R.string.enter_password))
                },
                trailingIcon = {
                    IconButton(onClick = { isPasswordShow = !isPasswordShow }) {
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
                        stringResource(R.string.create_new_account),
                        fontFamily = acherusFeral,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            if (username.isNotEmpty() && currentUser != null) {
                saveUsernameToFirebase(username)
            }

        }
    }
}

@Composable
private fun ThemedLogo() {
    val isDark = isSystemInDarkTheme()

    if (isDark) {
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

fun saveUsernameToFirebase(username: String) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    val userRef = db.collection("users").document(currentUser!!.uid)
    val userData = hashMapOf("username" to username)

    userRef.set(userData, SetOptions.merge())
}