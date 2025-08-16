package com.marcos.quizapplication.domain.model

data class Question(
    val text: String,
    val options: List<String>,
    val correctAnswer: String
)