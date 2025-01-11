package com.reysl.uroboros.view.pages

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
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
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.reysl.uroboros.DataStoreManager
import com.reysl.uroboros.R
import com.reysl.uroboros.ui.theme.UroborosTheme
import com.reysl.uroboros.view.screens.acherusFeral
import com.reysl.uroboros.view.screens.saveUsernameToFirebase
import com.reysl.uroboros.viewmodel.AuthViewModel
import com.reysl.uroboros.viewmodel.AuthViewModel.AuthState
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch


@Composable
fun ProfilePage(authViewModel: AuthViewModel, navController: NavController) {

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    val dataStoreManager = DataStoreManager(context)

    val savedName by dataStoreManager.getProfileName().collectAsState(initial = "")

    var name by remember {
        mutableStateOf(savedName ?: "")
    }

    val initialName by remember { mutableStateOf("user_001") }

    var oldPassword by remember {
        mutableStateOf("")
    }

    var newPassword by remember {
        mutableStateOf("")
    }

    var isPasswordShow by remember {
        mutableStateOf(false)
    }

    var isNewPasswordShow by remember {
        mutableStateOf(false)
    }

    var showSignoutConfirmationDialog by remember {
        mutableStateOf(false)
    }

    var showInfoDialog by remember {
        mutableStateOf(false)
    }

    var showSuccessDialog by remember {
        mutableStateOf(false)
    }

    var showErrorDialog by remember {
        mutableStateOf<String?>(null)
    }

    var showSuccessChangeNameDialog by remember {
        mutableStateOf(false)
    }

    var showErrorChangeNameDialog by remember {
        mutableStateOf<String?>(null)
    }


    var isLoadingImage by remember { mutableStateOf(false) }
    var isLoadingInitial by remember { mutableStateOf(true) }

    val coroutine = rememberCoroutineScope()

    val currentUser = FirebaseAuth.getInstance().currentUser
    var avatarUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                coroutine.launch {
                    dataStoreManager.saveProfileImage(uri.toString())
                }
                uploadImageToFirebase(uri, context) { downloadUri ->
                    avatarUri = downloadUri
                    isLoadingImage = false
                }
            }
            isLoadingImage = true
        }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            val cachedAvatarUrl = dataStoreManager.getProfileImage().firstOrNull()
            val cachedUsername = dataStoreManager.getProfileName().firstOrNull()

            if (cachedAvatarUrl != null && cachedUsername != null) {
                avatarUri = Uri.parse(cachedAvatarUrl)
                name = cachedUsername
                isLoadingInitial = false
            } else {
                val userRef =
                    FirebaseFirestore.getInstance().collection("users").document(currentUser.uid)
                userRef.get().addOnSuccessListener { document ->
                    if (document != null && document.contains("avatarUrl")) {
                        val avatarUrl = document.getString("avatarUrl")
                        avatarUrl?.let {
                            avatarUri = Uri.parse(it)
                            coroutine.launch {
                                dataStoreManager.saveProfileImage(it)
                            }
                        }
                    }
                    if (document != null && document.contains("username")) {
                        val username = document.getString("username")
                        username?.let {
                            name = it
                            coroutine.launch {
                                dataStoreManager.saveProfileName(it)
                            }
                        }
                    }
                    isLoadingInitial = false
                }.addOnFailureListener {
                    isLoadingInitial = false
                }
            }
        } else {
            isLoadingInitial = false
        }
    }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            is AuthState.Success -> {
                showSuccessDialog = true
            }

            is AuthState.Error -> {
                showErrorDialog = (authState.value as AuthState.Error).message
            }

            else -> Unit
        }
    }

    UroborosTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, start = 10.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { showInfoDialog = !showInfoDialog }) {
                    Image(
                        painter = painterResource(id = R.drawable.info),
                        contentDescription = "About",
                        modifier = Modifier.size(32.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                    )
                }
                IconButton(onClick = { showSignoutConfirmationDialog = true }) {
                    Image(
                        painter = painterResource(id = R.drawable.log_out),
                        contentDescription = "Log out",
                        modifier = Modifier.size(28.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                    )
                }

            }

            if (showSignoutConfirmationDialog) {
                SignoutConfirmationDialog(onSignoutConfirmed = {
                    authViewModel.signout()
                    showSignoutConfirmationDialog = false
                }, onDismiss = {
                    showSignoutConfirmationDialog = false
                })
            }

            if (showInfoDialog) {
                InfoDialog(
                    showDialog = showInfoDialog,
                    onDismiss = {
                        showInfoDialog = false
                    }
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                when {
                    isLoadingInitial -> {
                        CircularProgressIndicator(modifier = Modifier.size(120.dp))
                    }
                    else -> {
                        Box(
                            contentAlignment = Alignment.BottomEnd,
                            modifier = Modifier.size(120.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray)
                                    .border(2.dp, colorResource(id = R.color.green), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                ProfileImage(imageUrl = avatarUri)
                            }
                            IconButton(
                                onClick = { imagePickerLauncher.launch("image/*") },
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color.White, CircleShape)
                                    .border(1.dp, Color.Gray, CircleShape)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.camera),
                                    contentDescription = "Edit",
                                    tint = Color.Black,
                                )
                            }
                        }
                    }
                }
                if (isLoadingImage) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(Color(0xAA000000), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.username),
                        fontFamily = acherusFeral,
                        fontWeight = FontWeight.Light,
                        fontSize = 16.sp,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { newName ->
                            name = newName
                        }
                    )
                }

            }
            Spacer(modifier = Modifier.height(24.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.change_password),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = acherusFeral,
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.old_password),
                        fontFamily = acherusFeral,
                        fontWeight = FontWeight.Light,
                        fontSize = 16.sp,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
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
                        visualTransformation = if (isPasswordShow) VisualTransformation.None else PasswordVisualTransformation()
                    )
                }

            }
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.new_password),
                        fontFamily = acherusFeral,
                        fontWeight = FontWeight.Light,
                        fontSize = 16.sp,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        trailingIcon = {
                            IconButton(onClick = { isNewPasswordShow = !isNewPasswordShow }) {
                                if (isNewPasswordShow) {
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
                        visualTransformation = if (isNewPasswordShow) VisualTransformation.None else PasswordVisualTransformation(),
                    )
                }

            }
            Spacer(modifier = Modifier.height(30.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Button(
                    onClick = {
                        if (newPassword.isNotEmpty() && oldPassword.isNotEmpty()) {
                            authViewModel.changePassword(oldPassword, newPassword)
                        }
                        if (initialName != name) {
                            saveUsernameToFirebase(name)
                            coroutine.launch {
                                dataStoreManager.saveProfileName(name)
                            }
                        }
                        if (avatarUri != null) {
                            avatarUri.let { uri ->
                                uploadImageToFirebase(uri, context) { downloadUri ->
                                    avatarUri = downloadUri
                                    saveAvatarUrlToDatabase(uri.toString())
                                }
                                coroutine.launch {
                                    dataStoreManager.saveProfileImage(uri.toString())
                                }
                            }
                        }
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(
                            id = R.color.green
                        ),
                        contentColor = colorResource(
                            id = R.color.white
                        )
                    ),
                    modifier = Modifier
                        .width(350.dp)
                        .height(55.dp)
                ) {
                    Text(
                        text = stringResource(R.string.save_password),
                        fontFamily = acherusFeral,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                if (showSuccessDialog) {
                    AlertDialog(
                        onDismissRequest = { showSuccessDialog = false },
                        title = { Text(text = stringResource(R.string.success)) },
                        text = {
                            Text(
                                text = stringResource(R.string.password_was_change)
                            )
                        },
                        confirmButton = {
                            Button(onClick = { showSuccessDialog = false }) {
                                Text(text = "OK")
                            }
                        }
                    )
                }

                if (showErrorDialog != null) {
                    AlertDialog(
                        onDismissRequest = { showErrorDialog = null },
                        title = { Text(text = stringResource(R.string.error)) },
                        text = { Text(text = stringResource(R.string.error_changing_password)) },
                        confirmButton = {
                            Button(onClick = { showErrorDialog = null }) {
                                Text(text = "ОК")
                            }
                        }
                    )
                }

                if (showSuccessChangeNameDialog) {
                    AlertDialog(onDismissRequest = { showSuccessChangeNameDialog = false },
                        title = {
                            Text(
                                text = stringResource(R.string.success)
                            )
                        },
                        text = { Text(text = stringResource(R.string.username_was_change)) },
                        confirmButton = {
                            Button(onClick = { showSuccessChangeNameDialog = false }) {
                                Text(text = "OK")
                            }
                        }
                    )
                }

                if (showErrorChangeNameDialog != null) {
                    AlertDialog(
                        onDismissRequest = { showErrorChangeNameDialog = null },
                        title = { Text(text = stringResource(R.string.error)) },
                        text = { Text(text = stringResource(R.string.error_changing_username)) },
                        confirmButton = {
                            Button(onClick = { showErrorChangeNameDialog = null }) {
                                Text(text = "OK")
                            }
                        }
                    )
                }
            }


        }
    }
}

@Composable
fun InfoDialog(showDialog: Boolean, onDismiss: () -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(stringResource(R.string.information))
            },
            text = {
                Column {
                    InfoItem(
                        icon = R.drawable.donation,
                        label = stringResource(R.string.support_the_author),
                        url = "https://www.donationalerts.com/r/reysl"
                    )
                    InfoItem(
                        icon = R.drawable.telegram,
                        label = stringResource(R.string.telegram_channel),
                        url = "https://t.me/reysldevblog"
                    )
                    InfoItem(
                        icon = R.drawable.help,
                        label = stringResource(R.string.feedback),
                        email = "Rey5l@yandex.ru"
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.close), color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        )
    }
}

@Composable
fun InfoItem(icon: Int, label: String, url: String? = null, email: String? = null) {
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                if (url != null) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                } else if (email != null) {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:$email")
                    }
                    context.startActivity(intent)
                }
            }
    ) {
        if (icon == R.drawable.donation) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = colorResource(R.color.orange_donation),
                modifier = Modifier.size(24.dp),
            )
        } else {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = colorResource(R.color.green),
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        if (label == stringResource(R.string.support_the_author)) {
            Text(
                text = label,
                color = colorResource(R.color.orange_donation),
                fontWeight = FontWeight.Bold
            )
        } else {
            Text(text = label)
        }
    }
}


@Composable
fun SignoutConfirmationDialog(
    onSignoutConfirmed: () -> Unit,
    onDismiss: () -> Unit
) {
    UroborosTheme {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = stringResource(R.string.exit),
                    fontFamily = acherusFeral,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.confirm_exit),
                    fontFamily = acherusFeral,
                    fontWeight = FontWeight.Light,
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = onSignoutConfirmed,
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.green))
                ) {
                    Text(
                        text = stringResource(R.string.exit_2),
                        color = colorResource(id = R.color.card_color),
                        fontFamily = acherusFeral,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.card_color))
                ) {
                    Text(text = stringResource(R.string.cancel), color = colorResource(id = R.color.green))
                }
            }
        )
    }
}

@Composable
fun ProfileImage(imageUrl: Uri?) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl ?: R.drawable.avatar)
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
    )

    Image(
        painter = painter,
        contentDescription = "Profile Image",
        modifier = Modifier.size(100.dp),
        contentScale = ContentScale.Crop
    )
}


fun uploadImageToFirebase(uri: Uri?, context: Context, onImageUpload: (Uri) -> Unit) {
    val storage = FirebaseStorage.getInstance()
    val storageReference = storage.reference
    val imageReference = storageReference.child("images/" + uri!!.lastPathSegment)

    val uploadTask = uri.let { imageReference.putFile(it) }
    uploadTask.addOnSuccessListener {
        imageReference.downloadUrl.addOnSuccessListener { downloadUri ->
            onImageUpload(downloadUri)
            saveAvatarUrlToDatabase(downloadUri.toString())
            Toast.makeText(context, "Фото профиля успешно изменено", Toast.LENGTH_SHORT).show()
        }
    }.addOnFailureListener {
        Toast.makeText(context, "Не получилось обновить фото профиля", Toast.LENGTH_SHORT).show()
        Log.e("ErrorUpdateImage", it.toString())

    }
}

fun saveAvatarUrlToDatabase(avatarUrl: String) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    if (currentUser != null) {
        val userRef = db.collection("users").document(currentUser.uid)
        val userData = hashMapOf(
            "avatarUrl" to avatarUrl,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        userRef.set(userData, SetOptions.merge())
    }
}


