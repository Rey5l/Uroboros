package com.reysl.uroboros.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthViewModelTest {

    @Test
    fun `AuthState sealed class has correct types`() {
        val authenticated = AuthViewModel.AuthState.Authenticated
        val unauthenticated = AuthViewModel.AuthState.Unauthenticated
        val loading = AuthViewModel.AuthState.Loading
        val success = AuthViewModel.AuthState.Success("Success message")
        val error = AuthViewModel.AuthState.Error("Error message")

        assertTrue(authenticated is AuthViewModel.AuthState.Authenticated)
        assertTrue(unauthenticated is AuthViewModel.AuthState.Unauthenticated)
        assertTrue(loading is AuthViewModel.AuthState.Loading)
        assertTrue(success is AuthViewModel.AuthState.Success)
        assertTrue(error is AuthViewModel.AuthState.Error)
        assertEquals("Success message", success.message)
        assertEquals("Error message", error.message)
    }

    @Test
    fun `AuthState Success message is correct`() {
        val successState = AuthViewModel.AuthState.Success("Password changed successfully")

        assertEquals("Password changed successfully", successState.message)
    }

    @Test
    fun `AuthState Error message is correct`() {
        val errorState = AuthViewModel.AuthState.Error("Invalid credentials")

        assertEquals("Invalid credentials", errorState.message)
    }
}
