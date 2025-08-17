package com.marcos.quizapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.marcos.quizapplication.ui.screens.HomeScreen
import com.marcos.quizapplication.ui.screens.LoginScreen
import com.marcos.quizapplication.ui.screens.QuizRoute
import com.marcos.quizapplication.ui.screens.RegistrationScreen
import com.marcos.quizapplication.ui.viewmodel.HomeViewModel
import com.marcos.quizapplication.ui.viewmodel.LoginViewModel
import com.marcos.quizapplication.ui.viewmodel.QuizViewModel
import com.marcos.quizapplication.ui.viewmodel.RegistrationViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object Home : Screen("home_screen")
    object Quiz : Screen("quiz_screen/{quizId}") {
        fun createRoute(quizId: String) = "quiz_screen/$quizId"
    }
    object Registration : Screen("registration_screen")
}

@Composable
fun NavGraph(navController: NavHostController, startDestination: String) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Login.route) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()

            LoginScreen(
                uiState = uiState,
                onSignInClick = loginViewModel::signIn,
                onSignUpClick = {
                    navController.navigate(Screen.Registration.route)
                },
                onErrorMessageShown = loginViewModel::onErrorMessageShown,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }

                }
            )
        }

        composable(route = Screen.Registration.route) {
            val registrationViewModel: RegistrationViewModel = hiltViewModel()
            val uiState by registrationViewModel.uiState.collectAsStateWithLifecycle()

            RegistrationScreen(
                uiState = uiState,

                onRegisterClick = { username, email, password, confirmPassword ->
                    registrationViewModel.signUp(username, email, password, confirmPassword)
                },
                onRegistrationSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Registration.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                    registrationViewModel.onRegistrationHandled()
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onErrorMessageShown = registrationViewModel::onErrorMessageShown
            )
        }

        composable(route = Screen.Home.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

            HomeScreen(
                userName = uiState.userName,
                onLogout = {
                    homeViewModel.onLogout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onStartQuizClick = { quizId ->
                    navController.navigate(Screen.Quiz.createRoute(quizId))
                }
            )
        }

        composable(route = Screen.Quiz.route) {
            val quizViewModel: QuizViewModel = hiltViewModel()
            QuizRoute(
                viewModel = quizViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Quiz.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}