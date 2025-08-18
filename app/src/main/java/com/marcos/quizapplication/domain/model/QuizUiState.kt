package com.marcos.quizapplication.domain.model

data class QuizUiState(
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswer: String? = null,
    val userAnswers: Map<Int, String> = emptyMap(),
    val score: Int = 0,
    val isFinished: Boolean = false,

    // CAMPOS ADICIONAIS QUE O VIEWMODEL PODE PRECISAR PARA GERENCIAR O ESTADO
    val quizTitle: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null,

    val totalTimeInSeconds: Int = 300, // 5 minutos por padrão
    val remainingTimeInSeconds: Int = 300,
    val isTimeUp: Boolean = false,
    val startTime: Long = 0,
    val elapsedTime: Int = 0
) {
    val currentQuestion: Question?
        get() = questions.getOrNull(currentQuestionIndex)

    val progress: Float
        get() = if (questions.isEmpty()) 0f else (currentQuestionIndex + 1) / questions.size.toFloat()

    val timeProgress: Float
        get() = if (totalTimeInSeconds <= 0) 1f else remainingTimeInSeconds.toFloat() / totalTimeInSeconds

    fun formatTime(): String {
        val minutes = remainingTimeInSeconds / 60
        val seconds = remainingTimeInSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun formatElapsedTime(): String {
        val minutes = elapsedTime / 60
        val seconds = elapsedTime % 60
        return String.format("%02dm %02ds", minutes, seconds)
    }
}