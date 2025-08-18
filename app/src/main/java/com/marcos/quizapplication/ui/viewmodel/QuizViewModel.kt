package com.marcos.quizapplication.ui.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcos.quizapplication.domain.contracts.AuthRepository
import com.marcos.quizapplication.domain.contracts.QuizRepository
import com.marcos.quizapplication.domain.contracts.RankingRepository
import com.marcos.quizapplication.domain.model.QuizAttempt
import com.marcos.quizapplication.domain.model.QuizUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val rankingRepository: RankingRepository,  // Adicione essa dependência
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val quizId: String = savedStateHandle.get<String>("quizId") ?: ""
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

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
            quizRepository.getQuizInfo(id).onSuccess { quizInfo ->
                _uiState.update { it.copy(quizTitle = quizInfo.title) }
                quizRepository.getQuestionsForQuiz(id)
                    .onSuccess { questions ->
                        if (questions.isNotEmpty()) {
                            _uiState.update { currentState: QuizUiState ->
                                currentState.copy(
                                    questions = questions,
                                    isLoading = false,

                                    startTime = System.currentTimeMillis(),
                                    totalTimeInSeconds = 300, // 5 minutos de padrão,, pode ser configurável.
                                    remainingTimeInSeconds = 300
                                )
                            }
                            startTimer()
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
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val currentState = _uiState.value

                if (currentState.isFinished || currentState.isTimeUp) {
                    break
                }

                val newRemainingTime = currentState.remainingTimeInSeconds - 1
                val elapsedTime = ((System.currentTimeMillis() - currentState.startTime) / 1000).toInt()

                if (newRemainingTime <= 0) {
                    _uiState.update {
                        it.copy(
                            isTimeUp = true,
                            isFinished = true,
                            remainingTimeInSeconds = 0,
                            elapsedTime = elapsedTime
                        )
                    }
                    calculateResults()
                    break
                } else {
                    _uiState.update {
                        it.copy(
                            remainingTimeInSeconds = newRemainingTime,
                            elapsedTime = elapsedTime
                        )
                    }
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
        val currentQuestion = currentState.currentQuestion ?: return

        val newScore = if (currentQuestion.correctAnswer == currentState.selectedAnswer) {

            currentState.score + 1
        } else {
            currentState.score
        }
        Log.w("QuizViewModel", "Ponto atual: ${newScore}" )
        val newUserAnswers = currentState.userAnswers + (currentState.currentQuestionIndex to (currentState.selectedAnswer ?: ""))

        if (currentState.currentQuestionIndex < currentState.questions.size - 1) {
            val nextIndex = currentState.currentQuestionIndex + 1
            _uiState.update {
                it.copy(
                    currentQuestionIndex = nextIndex,
                    selectedAnswer = null,
                    score = newScore,
                    userAnswers = newUserAnswers
                )
            }
        } else {

            _uiState.update {
                it.copy(
                    isFinished = true,
                    score = newScore,
                    userAnswers = newUserAnswers
                )
            }
            timerJob?.cancel()
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
                    isLoading = false,
                    errorMessage = null
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
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
        updateUserRanking(correctAnswers)
        Log.d("Pontos Final", "${correctAnswers}")
    }

    private fun updateUserRanking(score: Int) {
        viewModelScope.launch {
            val currentUser = authRepository.getAuthState().value.user

            if (currentUser != null) {
                val pointsToAdd =   score
                val currentState = _uiState.value

                rankingRepository.updateUserPoints(currentUser.uid, pointsToAdd)
                    .onSuccess {
                        Log.d("QuizViewModel", "Ranking atualizado com sucesso: +$pointsToAdd pontos")
                    }
                    .onFailure { error ->
                        Log.e("QuizViewModel", "Falha ao atualizar ranking", error)
                    }

                val quizAttempt = QuizAttempt(
                    quizId = quizId,
                    quizTitle = currentState.quizTitle,
                    score = pointsToAdd,
                    totalQuestions = currentState.questions.size,
                    correctAnswers = pointsToAdd,
                    timeSpentInSeconds = currentState.elapsedTime
                )

                rankingRepository.saveQuizAttempt(currentUser.uid, quizAttempt)
                    .onSuccess {
                        Log.d("QuizViewModel", "Histórico de quiz salvo com sucesso")
                    }
                    .onFailure { error ->
                        Log.e("QuizViewModel", "Falha ao salvar histórico do quiz", error)
                    }
            } else {
                Log.w("QuizViewModel", "Não foi possível atualizar o ranking: usuário não autenticado")
            }
        }
    }
}
