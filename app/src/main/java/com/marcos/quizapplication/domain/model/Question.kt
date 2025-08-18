package com.marcos.quizapplication.domain.model

data class Question(
    val id: String = "", // ID do documento da pergunta
    val text: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: String = ""
)