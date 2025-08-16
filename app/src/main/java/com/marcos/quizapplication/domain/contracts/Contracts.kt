package com.marcos.quizapplication.domain.contracts

data class User(
    val uid: String,
    val email: String?
)

data class AuthState(
    val user: User? = null,
    val isInitialLoading: Boolean = true
)

