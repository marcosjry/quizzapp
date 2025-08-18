package com.marcos.quizapplication.domain.model

data class QuizAttempt(
    val quizId: String,
    val quizTitle: String,
    val score: Int,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val timeSpentInSeconds: Int,
    val completedAt: Long = System.currentTimeMillis()
)
