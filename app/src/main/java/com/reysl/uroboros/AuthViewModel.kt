package com.reysl.uroboros

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.reysl.uroboros.data.UserProfile

class AuthViewModel : ViewModel() {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
            loadUserProfile()
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Значения email или пароля не могут быть пустыми")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Что-то пошло не так")
                }
            }
    }

    fun registration(email: String, password: String, username: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Значения email или пароля не могут быть пустыми")
            return
        }

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val userProfile = UserProfile(uid = it.uid, username = username, email = email)
                        saveUserProfile(userProfile) { success ->
                            if (success) {
                                _authState.value = AuthState.Authenticated
                            } else {
                                _authState.value = AuthState.Error("Не удалось сохранить данные пользователя")
                            }
                        }
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Что-то пошло не так")
                }
            }
    }

    fun saveUserProfile(userProfile: UserProfile, onResult: (Boolean) -> Unit) {
        db.collection("users").document(userProfile.uid)
            .set(userProfile)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getUserProfile(uid: String, onResult: (UserProfile?) -> Unit) {
        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                val userProfile = document.toObject(UserProfile::class.java)
                onResult(userProfile)
            }
            .addOnFailureListener { onResult(null) }
    }

    fun loadUserProfile() {
        val currentUser = auth.currentUser
        currentUser?.let {
            getUserProfile(it.uid) { userProfile ->
                userProfile?.let { profile ->
                    _authState.value = AuthState.ProfileLoaded(profile)
                }
            }
        }
    }

    fun updateUserProfile(username: String, onResult: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userRef = db.collection("users").document(currentUser.uid)

            userRef.update("username", username)
                .addOnSuccessListener { onResult(true) }
                .addOnFailureListener { onResult(false) }
        } else {
            onResult(false)
        }
    }



    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        val user = auth.currentUser
        if (user != null && newPassword.isNotEmpty()) {
            val email = user.email
            if (email != null) {
                val credential = EmailAuthProvider.getCredential(email, oldPassword)
                user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                _authState.value = AuthState.Success("Пароль успешно изменен!")
                            } else {
                                _authState.value = AuthState.Error("Не удалось изменить пароль")
                            }
                        }
                    } else {
                        _authState.value = AuthState.Error("Неверный старый пароль")
                    }
                }
            }
        }
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()

    data class ProfileLoaded(val profile: UserProfile) : AuthState()
}
