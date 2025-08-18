package com.marcos.quizapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marcos.quizapplication.model.QuizInfo
import com.marcos.quizapplication.ui.viewmodel.HomeUiState

fun Color(hexString: String): Color {
    return try {
        Color(android.graphics.Color.parseColor("#$hexString"))
    } catch (e: IllegalArgumentException) {
        Color.Gray // Cor padrão em caso de erro
    }
}

data class Stat(
    val icon: ImageVector,
    val title: String,
    val value: String,
    val backgroundColor: Color
)


val sampleStats = listOf(
    Stat(Icons.Default.CheckCircle, "Quizzes Completed", "12", Color(0xFFE0F7FA)),
    Stat(Icons.Default.TrendingUp, "Accuracy Rate", "78%", Color(0xFFE8F5E9)),
    Stat(Icons.Default.Schedule, "Average Time", "5m 24s", Color(0xFFFFF3E0))
)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onLogout: () -> Unit,
    onStartQuizClick: (quizId: String) -> Unit,
    onQuizzesErrorMessageShown: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(uiState.quizzesErrorMessage) {
        // ... (código do Toast) ...
    }

    Scaffold(
        topBar = {
            TopAppBar( // <<<--- RESTAURE ESTE BLOCO
                title = { Text("QuizMaster", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Profile") },
                            onClick = {
                                // TODO: Implementar Profile
                                showMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.Person, "Profile") }
                        )
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                // TODO: Implementar Settings
                                showMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.Settings, "Settings") }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                showMenu = false
                                onLogout()
                            },
                            leadingIcon = { Icon(Icons.Default.ExitToApp, "Logout") }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF3B82F6), // Exemplo de cor, ajuste conforme necessário
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            ) // <<<--- FIM DO BLOCO TopAppBar
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
                    text = "Welcome, ${uiState.userName}!", // Usar uiState.userName
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // item { // Seção de Stats (se ainda for usar sampleStats)
            // Text(text = "Your Stats", ...)
            // sampleStats.forEach { stat -> StatCard(stat = stat) }
            // }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Available Quizzes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (uiState.isLoadingQuizzes) {
                item {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (uiState.quizzes.isEmpty() && uiState.quizzesErrorMessage == null) {
                item {
                    Text(
                        text = "No quizzes available at the moment. Check back later!",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(uiState.quizzes) { quizInfo -> // Iterar sobre uiState.quizzes
                    QuizCard( // Passar QuizInfo
                        quizInfo = quizInfo,
                        onStartClick = { onStartQuizClick(quizInfo.id) } // Usar quizInfo.id
                    )
                }
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
fun QuizCard( // Modificar para aceitar QuizInfo
    quizInfo: QuizInfo,
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
                    text = quizInfo.title, // Usar quizInfo.title
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = quizInfo.difficulty, // Usar quizInfo.difficulty
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(quizInfo.difficultyColorHex)) // Converter HEX para Color
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = quizInfo.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray) // Usar quizInfo.description
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = "Time", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = quizInfo.time, color = Color.Gray) // Usar quizInfo.time
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
        // Atualizar o preview para usar HomeUiState e dados de exemplo para QuizInfo
        val previewQuizzes = listOf(
            QuizInfo("1", "Math Preview", "Easy math questions", "5 min", "Easy", "FFC8E6C9"),
            QuizInfo("2", "Science Preview", "Basic science", "10 min", "Medium", "FFFFECB3")
        )
        HomeScreen(
            uiState = HomeUiState(userName = "John Doe", quizzes = previewQuizzes),
            onLogout = {},
            onStartQuizClick = {},
            onQuizzesErrorMessageShown = {}
        )
    }
}