package com.marcos.quizapplication.ui.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcos.quizapplication.domain.contracts.QuizRepository
import com.marcos.quizapplication.domain.model.QuizUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val quizId: String = savedStateHandle.get<String>("quizId") ?: ""

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        if (quizId.isNotBlank()) {
            Log.d("QuizViewModel", "Initializing QuizViewModel for quizId: $quizId")
            loadQuestions(quizId)
        } else {
            Log.e("QuizViewModel", "QuizId is blank in init!")
            _uiState.update { it.copy(isLoading = false, errorMessage = "Quiz ID not found.") }
        }
    }

    private fun loadQuestions(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            quizRepository.getQuestionsForQuiz(id)
                .onSuccess { questions ->
                    if (questions.isNotEmpty()) {
                        _uiState.update { currentState: QuizUiState ->
                            currentState.copy(
                                questions = questions,
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update { it: QuizUiState ->
                            it.copy(
                                questions = emptyList(),
                                isLoading = false,
                                errorMessage = "No questions found for this quiz."
                            )
                        }
                    }
                }
                .onFailure { exception ->
                    Log.e("QuizViewModel", "Failed to load questions for quizId $id", exception)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Failed to load questions."
                        )
                    }
                }
        }
    }

    fun onAnswerSelected(answer: String) {
        // Se sua lógica de userAnswers precisa ser atualizada aqui, faça-o
        _uiState.update {
            it.copy(
                selectedAnswer = answer,
                // Se você quiser manter o score ou outras informações, faça isso aqui
                // Mas, por enquanto, vamos apenas atualizar a resposta selecionada.
            )
        }
    }

    fun onNextClicked() {
        val currentState = _uiState.value
        val currentQuestion = currentState.currentQuestion ?: return // Usando seu getter!

        if (currentState.selectedAnswer == null && !currentState.isFinished) {
            // Poderia adicionar uma lógica para não permitir avançar sem resposta,
            // ou apenas não computar o score para a pergunta atual.
            // Por enquanto, vamos permitir avançar.
        }

        // Verifica a resposta e atualiza o score
        val newScore = if (currentQuestion.correctAnswer == currentState.selectedAnswer) {
            currentState.score + 1
        } else {
            currentState.score
        }

        val newUserAnswers = currentState.userAnswers + (currentState.currentQuestionIndex to (currentState.selectedAnswer ?: ""))

        if (currentState.currentQuestionIndex < currentState.questions.size - 1) {
            // Próxima pergunta
            val nextIndex = currentState.currentQuestionIndex + 1
            _uiState.update {
                it.copy(
                    currentQuestionIndex = nextIndex,
                    selectedAnswer = null, // Reseta a seleção para a próxima pergunta
                    score = newScore,
                    userAnswers = newUserAnswers
                )
            }
        } else {
            // Quiz finalizado
            _uiState.update {
                it.copy(
                    isFinished = true,
                    score = newScore,
                    userAnswers = newUserAnswers
                    // selectedAnswer pode ser mantido ou resetado aqui, dependendo da UI de resultado
                )
            }
            calculateResults()
            Log.d("QuizViewModel", "Quiz finished. Score: $newScore / ${currentState.questions.size}")

        }


    }

    fun restartQuiz() {
        if (quizId.isNotBlank()) {

            _uiState.update {

                it.copy(
                    currentQuestionIndex = 0,
                    selectedAnswer = null,
                    userAnswers = emptyMap(),
                    score = 0,
                    isFinished = false,
                    isLoading = false, // Se as perguntas já estão lá
                    errorMessage = null
                )
            }
            // Se optou por recarregar, chame:
            // loadQuestions(quizId)
        }
    }

    private fun calculateResults() {
        var correctAnswers = 0
        val userAnswers = _uiState.value.userAnswers
        val questions = _uiState.value.questions

        questions.forEachIndexed { index, question ->
            if (question.correctAnswer == userAnswers[index]) {
                correctAnswers++
            }
        }

        val finalScore = (correctAnswers.toFloat() / questions.size * 100).toInt()

        _uiState.update {
            it.copy(
                isFinished = true,
                score = finalScore
            )
        }
    }
}
