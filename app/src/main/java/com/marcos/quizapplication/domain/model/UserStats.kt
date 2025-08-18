package com.marcos.quizapplication.domain.model

data class UserStats(
    val totalQuizzes: Int = 0,
    val totalCorrectAnswers: Int = 0,
    val totalQuestions: Int = 0,
    val averageScore: Float = 0f,
    val averageTimePerQuizInSeconds: Float = 0f
)