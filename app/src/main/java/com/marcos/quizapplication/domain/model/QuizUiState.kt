package com.marcos.quizapplication.domain.model

data class QuizUiState(
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswer: String? = null,
    val userAnswers: Map<Int, String> = emptyMap(),
    val score: Int = 0,
    val isFinished: Boolean = false,

    // CAMPOS ADICIONAIS QUE O VIEWMODEL PODE PRECISAR PARA GERENCIAR O ESTADO
    val quizTitle: String = "", // Adicionado para exibir o título do quiz
    val isLoading: Boolean = true, // Adicionado para feedback de carregamento
    val errorMessage: String? = null
) {
    val currentQuestion: Question?
        get() = questions.getOrNull(currentQuestionIndex)

    val progress: Float
        get() = if (questions.isEmpty()) 0f else (currentQuestionIndex + 1) / questions.size.toFloat()
}