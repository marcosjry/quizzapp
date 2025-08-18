package com.marcos.quizapplication.ui.viewmodel

import com.marcos.quizapplication.model.QuizInfo

data class HomeUiState(
    val userName: String = "Guest",
    val quizzes: List<QuizInfo> = emptyList(),
    val isLoadingQuizzes: Boolean = false,
    val quizzesErrorMessage: String? = null
)