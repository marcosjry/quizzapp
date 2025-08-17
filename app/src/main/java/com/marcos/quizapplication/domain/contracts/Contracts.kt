package com.marcos.quizapplication.domain.contracts

import com.marcos.quizapplication.domain.model.User


data class AuthState(
    val user: User? = null,
    val isInitialLoading: Boolean = true
)

