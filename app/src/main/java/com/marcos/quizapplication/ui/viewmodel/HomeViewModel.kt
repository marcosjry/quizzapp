package com.marcos.quizapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcos.quizapplication.domain.contracts.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class HomeUiState(
    val userName: String = "Guest"
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository
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
    }

    fun onLogout() {
        authRepository.signOut()
    }
}