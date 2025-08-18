package com.marcos.quizapplication.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcos.quizapplication.domain.contracts.AuthRepository
import com.marcos.quizapplication.domain.contracts.RankingRepository
import com.marcos.quizapplication.domain.model.ProfileUiState
import com.marcos.quizapplication.domain.model.UserStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val rankingRepository: RankingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val currentUser = authRepository.getAuthState().value.user

                if (currentUser != null) {
                    val userId = currentUser.uid
                    val name = currentUser.username
                        ?: currentUser.email?.substringBefore('@')?.replaceFirstChar { it.titlecase() }
                        ?: "Usuário"

                    _uiState.update { it.copy(
                        displayName = name,
                        photoUrl = "",
                        userId = userId,
                        isLoading = true
                    )}

                    // Log para debug
                    Log.d("ProfileViewModel", "Carregando perfil para usuário: $name, ID: $userId")

                    // Coleta dados de histórico de quiz
                    rankingRepository.getUserQuizAttempts(userId)
                        .catch { e ->
                            Log.e("ProfileViewModel", "Erro ao carregar histórico de quiz", e)
                            _uiState.update { it.copy(
                                isLoading = false,
                                errorMessage = "Erro ao carregar histórico: ${e.message}"
                            )}
                        }
                        .collect { attempts ->
                            Log.d("ProfileViewModel", "Histórico de quiz carregado: ${attempts.size} tentativas")

                            // Se não houver tentativas, podemos mostrar um estado vazio
                            if (attempts.isEmpty()) {
                                _uiState.update { it.copy(
                                    quizHistory = emptyList(),
                                    userStats = UserStats(),
                                    isLoading = false
                                )}
                            } else {
                                // Calculamos as estatísticas manualmente se necessário
                                val totalQuizzes = attempts.size
                                val totalQuestions = attempts.sumOf { it.totalQuestions }
                                val totalCorrectAnswers = attempts.sumOf { it.correctAnswers }
                                val averageScore = if (totalQuestions > 0)
                                    totalCorrectAnswers.toFloat() / totalQuestions * 100
                                else 0f
                                val totalTimeSpent = attempts.sumOf { it.timeSpentInSeconds }
                                val averageTimePerQuiz = if (totalQuizzes > 0)
                                    totalTimeSpent.toFloat() / totalQuizzes
                                else 0f

                                val stats = UserStats(
                                    totalQuizzes = totalQuizzes,
                                    totalQuestions = totalQuestions,
                                    totalCorrectAnswers = totalCorrectAnswers,
                                    averageScore = averageScore,
                                    averageTimePerQuizInSeconds = averageTimePerQuiz
                                )

                                _uiState.update { it.copy(
                                    quizHistory = attempts,
                                    userStats = stats,
                                    isLoading = false
                                )}
                            }
                        }
                } else {
                    Log.e("ProfileViewModel", "Usuário não autenticado")
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = "Usuário não autenticado"
                    )}
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Erro ao carregar perfil", e)
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = "Erro ao carregar perfil: ${e.message}"
                )}
            }
        }
    }
}