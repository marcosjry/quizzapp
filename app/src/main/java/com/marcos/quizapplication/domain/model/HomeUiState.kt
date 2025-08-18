package com.marcos.quizapplication.ui.viewmodel

import com.marcos.quizapplication.domain.model.RankedUser
import com.marcos.quizapplication.model.QuizInfo

data class HomeUiState(
    val userName: String = "",
    val quizzes: List<QuizInfo> = emptyList(),
    val isLoadingQuizzes: Boolean = false,
    val quizzesErrorMessage: String? = null,
    val topPerformers: List<RankedUser> = emptyList()
)