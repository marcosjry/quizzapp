package com.marcos.quizapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcos.quizapplication.domain.contracts.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegistrationUiState(
    val isLoading: Boolean = false,
    val registrationSuccess: Boolean = false,
    val errorMessage: String? = null,
    val usernameError: String? = null, // Added usernameError
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState = _uiState.asStateFlow()

    fun signUp(username: String, email: String, password: String, confirmPassword: String) {

        if (username.isEmpty()) {
            _uiState.update { it.copy(usernameError = "Username cannot be empty", emailError = null, passwordError = null, confirmPasswordError = null) }
            return
        } else {
            _uiState.update { it.copy(usernameError = null) }
        }

        if (email.isEmpty()) {
            _uiState.update { it.copy(emailError = "Email cannot be empty", passwordError = null, confirmPasswordError = null) }
            return
        } else {
            _uiState.update { it.copy(emailError = null) }
        }

        if (password.isEmpty()) {
            _uiState.update { it.copy(passwordError = "Password cannot be empty", confirmPasswordError = null) }
            return
        } else if (password.length < 6) {
            _uiState.update { it.copy(passwordError = "Password must be at least 6 characters", confirmPasswordError = null) }
            return
        } else {
            _uiState.update { it.copy(passwordError = null) }
        }

        if (password != confirmPassword) {
            _uiState.update { it.copy(confirmPasswordError = "Passwords do not match") }
            return
        } else {
            _uiState.update { it.copy(confirmPasswordError = null) }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, registrationSuccess = false) }
            val result = authRepository.signUp(username, email, password)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, registrationSuccess = true) }
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, errorMessage = exception.message) }
            }
        }
    }

    fun onErrorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onRegistrationHandled() {
        _uiState.update { it.copy(registrationSuccess = false) }
    }
}