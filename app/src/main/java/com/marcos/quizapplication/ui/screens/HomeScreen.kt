package com.marcos.quizapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Stat(
    val icon: ImageVector,
    val title: String,
    val value: String,
    val backgroundColor: Color
)

data class Quiz(
    val title: String,
    val description: String,
    val time: String,
    val difficulty: String,
    val difficultyColor: Color
)

val sampleStats = listOf(
    Stat(Icons.Default.CheckCircle, "Quizzes Completed", "12", Color(0xFFE0F7FA)),
    Stat(Icons.Default.TrendingUp, "Accuracy Rate", "78%", Color(0xFFE8F5E9)),
    Stat(Icons.Default.Schedule, "Average Time", "5m 24s", Color(0xFFFFF3E0))
)

val sampleQuizzes = listOf(
    Quiz("Mathematics Basics", "Test your knowledge of basic mathematics concepts", "10 min", "Easy", Color(0xFFC8E6C9)),
    Quiz("Science Fundamentals", "Explore basic scientific concepts and principles", "15 min", "Medium", Color(0xFFFFECB3))
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userName: String,
    onLogout: () -> Unit,
    onStartQuizClick: (quizId: String) -> Unit
    // Adicionar callbacks para Profile e Settings se for implementá-los agora
    // onProfileClick: () -> Unit,
    // onSettingsClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) } // Estado para controlar a visibilidade do menu

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QuizMaster", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) { // Alterna a visibilidade do menu
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false } // Fecha o menu se clicar fora
                    ) {
                        // Exemplo de item de Perfil (Profile)
                        DropdownMenuItem(
                            text = { Text("Profile") },
                            onClick = {
                                // onProfileClick()
                                showMenu = false
                                // TODO: Implementar navegação para tela de perfil ou ação
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Profile"
                                )
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                showMenu = false
                                // TODO: Implementar navegação para tela de configurações ou ação
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = "Settings"
                                )
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                showMenu = false // Fecha o menu
                                onLogout()     // Chama a função de logout existente
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.ExitToApp,
                                    contentDescription = "Logout"
                                )
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF3B82F6),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Welcome, $userName!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ready to test your knowledge? Choose a quiz below to get started.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Your Stats",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    sampleStats.forEach { stat ->
                        StatCard(stat = stat)
                    }
                }
            }

            // Seção de Quizzes Disponíveis
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Available Quizzes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(sampleQuizzes) { quiz ->
                QuizCard(
                    quiz = quiz,
                    onStartClick = { onStartQuizClick(quiz.title) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun StatCard(stat: Stat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = stat.backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = stat.icon,
                contentDescription = stat.title,
                tint = Color.DarkGray,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = stat.title, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                Text(
                    text = stat.value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun QuizCard(
    quiz: Quiz,
    onStartClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = quiz.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = quiz.difficulty,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(quiz.difficultyColor)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = quiz.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = "Time", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = quiz.time, color = Color.Gray)
                }
                Button(onClick = onStartClick) {
                    Text("Start Quiz")
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Home Screen Preview")
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            userName = "John Doe",
            onLogout = {},
            onStartQuizClick = {}
            // onProfileClick = {},
            // onSettingsClick = {}
        )
    }
}