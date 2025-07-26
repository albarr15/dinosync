package com.mobdeve.s18.group9.dinosync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.s18.group9.dinosync.repository.AuthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    data class Loading(val msg: String = "") : AuthState()
    data class Success(val uid: String) : AuthState()
    data class Error(val error: String) : AuthState()
}

class AuthViewModel @Inject constructor(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    fun register(
        email: String,
        password: String,
        userName: String,
        userBio: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.register(email, password, userName, userBio)
            result.fold(
                onSuccess = {
                    println("✅ AuthViewModel: Register success with UID: $it")
                    onSuccess()
                },
                onFailure = {
                    println("❌ AuthViewModel: Register error: ${it.message}")
                    onError(it.localizedMessage ?: "Unknown error")
                }
            )
        }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.login(email, password)
            if (result.isSuccess) {
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.localizedMessage ?: "Login failed")
            }
        }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }
}
