package com.marcos.quizapplication.model // Ou o seu pacote de modelos

data class QuizInfo(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val time: String = "",
    val difficulty: String = "",
    val difficultyColorHex: String = ""
)