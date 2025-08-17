package com.marcos.quizapplication.domain.contracts

import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    fun getAuthState(): StateFlow<AuthState>
    suspend fun signUp(username: String, email: String, password: String): Result<Unit>
    suspend fun signIn(email: String, password: String): Result<Unit>
    fun signOut()
}