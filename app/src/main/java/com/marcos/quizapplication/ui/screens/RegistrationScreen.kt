package com.marcos.quizapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marcos.quizapplication.ui.viewmodel.RegistrationUiState
import kotlinx.coroutines.launch // Necessário para coroutineScope.launch se ainda for usado, mas o LaunchedEffect é o principal aqui.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    uiState: RegistrationUiState,
    onRegisterClick: (username: String, email: String, password: String, confirmPassword: String) -> Unit,
    onNavigateBack: () -> Unit,
    onRegistrationSuccess: () -> Unit,
    onErrorMessageShown: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Estado para o SnackbarHost
    val snackbarHostState = remember { SnackbarHostState() }

    // Efeito para mostrar a Snackbar quando houver uma mensagem de erro
    if (uiState.errorMessage != null) {
        val currentErrorMessage = uiState.errorMessage // Captura a mensagem atual
        LaunchedEffect(currentErrorMessage, snackbarHostState) {
            snackbarHostState.showSnackbar(
                message = currentErrorMessage,
                duration = SnackbarDuration.Short // Duração aumentada
            )
            onErrorMessageShown() // Limpa a mensagem após a Snackbar ser dispensada
        }
    }

    LaunchedEffect(uiState.registrationSuccess) {
        if (uiState.registrationSuccess) {
            onRegistrationSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Configura o SnackbarHost
        topBar = {
            TopAppBar(
                title = { Text("Create Account") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Create a New Account", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.usernameError != null,
                supportingText = { uiState.usernameError?.let { Text(it) } }
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.emailError != null,
                supportingText = { uiState.emailError?.let { Text(it) } }
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.passwordError != null,
                supportingText = { uiState.passwordError?.let { Text(it) } },
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else
                        Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Hide password" else "Show password"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.confirmPasswordError != null,
                supportingText = { uiState.confirmPasswordError?.let { Text(it) } }
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { onRegisterClick(username, email, password, confirmPassword) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = username.isNotBlank() && email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()
                ) {
                    Text("Register")
                }
            }

            // A exibição antiga da mensagem de erro como Text foi removida daqui.
            // A Snackbar cuidará disso agora.
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    RegistrationScreen(
        uiState = RegistrationUiState(isLoading = false, usernameError = null, emailError = null, passwordError = null, confirmPasswordError = null, errorMessage = null, registrationSuccess = false),
        onRegisterClick = { _, _, _, _ -> },
        onNavigateBack = {},
        onRegistrationSuccess = {},
        onErrorMessageShown = {}
    )
}
