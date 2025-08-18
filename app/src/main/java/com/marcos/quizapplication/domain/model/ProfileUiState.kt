package com.marcos.quizapplication.domain.model

data class ProfileUiState(
    val displayName: String = "",
    val photoUrl: String = "",
    val userId: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val quizHistory: List<QuizAttempt> = emptyList(),
    val userStats: UserStats = UserStats()
)