package com.marcos.quizapplication.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcos.quizapplication.domain.contracts.AuthRepository
import com.marcos.quizapplication.domain.contracts.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        authRepository.getAuthState().onEach { authState ->
            authState.user?.let { user ->
                val name = user.email?.substringBefore('@')?.replaceFirstChar { it.titlecase() } ?: "User"
                _uiState.value = HomeUiState(userName = name)
            }
        }.launchIn(viewModelScope)
        loadAvailableQuizzes()
    }

    fun onLogout() {
        authRepository.signOut()
    }

    private fun loadAvailableQuizzes() {
        Log.d("HomeViewModel", "loadAvailableQuizzes called")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingQuizzes = true, quizzesErrorMessage = null) }
            quizRepository.getAvailableQuizzes()
                .onSuccess { quizzes ->
                    Log.d("HomeViewModel", "Quizzes loaded successfully: ${quizzes.size} quizzes. Data: $quizzes")
                    _uiState.update {
                        it.copy(
                            quizzes = quizzes,
                            isLoadingQuizzes = false
                        )
                    }
                }
                .onFailure { exception ->
                    Log.e("HomeViewModel", "Failed to load quizzes", exception) // Use Log.e para erros e passe a exceção
                    _uiState.update {
                        it.copy(
                            quizzesErrorMessage = exception.message ?: "Failed to load quizzes",
                            isLoadingQuizzes = false
                        )
                    }
                }
        }
    }

    fun onQuizzesErrorMessageShown() {
        _uiState.update { it.copy(quizzesErrorMessage = null) }
    }
}