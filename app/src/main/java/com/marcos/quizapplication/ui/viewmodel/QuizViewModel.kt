package com.marcos.quizapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.marcos.quizapplication.domain.model.Question
import com.marcos.quizapplication.domain.model.QuizUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class QuizViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        val sampleQuestions = listOf(
            Question("What is 2 + 2?", listOf("3", "4", "5", "6"), "4"),
            Question("What is 5 x 7?", listOf("30", "35", "40", "45"), "35"),
            Question("What is the capital of France?", listOf("London", "Berlin", "Paris", "Madrid"), "Paris"),
            Question("What is 10 / 2?", listOf("3", "4", "5", "6"), "5"),
            Question("What is the square root of 81?", listOf("7", "8", "9", "10"), "9")
        )
        _uiState.value = QuizUiState(questions = sampleQuestions)
    }

    fun onAnswerSelected(answer: String) {
        val currentIndex = _uiState.value.currentQuestionIndex
        _uiState.update {
            it.copy(
                selectedAnswer = answer,
                userAnswers = it.userAnswers + (currentIndex to answer)
            )
        }
    }

    fun onNextClicked() {
        val nextIndex = _uiState.value.currentQuestionIndex + 1
        if (nextIndex < _uiState.value.questions.size) {
            _uiState.update {
                it.copy(
                    currentQuestionIndex = nextIndex,
                    selectedAnswer = null
                )
            }
        } else {
            calculateResults()
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
