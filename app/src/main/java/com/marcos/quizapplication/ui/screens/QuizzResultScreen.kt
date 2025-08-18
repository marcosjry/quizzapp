package com.marcos.quizapplication.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marcos.quizapplication.domain.model.Question
import com.marcos.quizapplication.domain.model.QuizUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultsScreen(
    state: QuizUiState,
    onBackToHome: () -> Unit
) {
    val correctAnswers = state.userAnswers.count { (index, answer) ->
        state.questions[index].correctAnswer == answer
    }
    val incorrectAnswers = state.questions.size - correctAnswers

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Results") },
                navigationIcon = {
                    IconButton(onClick = onBackToHome) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Home")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Mathematics Basics", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier.size(150.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { state.score / 100f },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 12.dp,
                    trackColor = Color.LightGray.copy(alpha = 0.5f)
                )
                Text(
                    text = "${state.score}%",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text("Your Score", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Performance Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(16.dp))
            PerformanceDetailCard(
                icon = Icons.Default.CheckCircle,
                label = "Correct Answers",
                value = "$correctAnswers / ${state.questions.size}",
                backgroundColor = Color(0xFFE8F5E9),
                iconColor = Color(0xFF4CAF50)
            )
            Spacer(modifier = Modifier.height(12.dp))
            PerformanceDetailCard(
                icon = Icons.Default.Cancel,
                label = "Incorrect Answers",
                value = "$incorrectAnswers / ${state.questions.size}",
                backgroundColor = Color(0xFFFFEBEE),
                iconColor = Color(0xFFF44336)
            )
            Spacer(modifier = Modifier.height(12.dp))
            PerformanceDetailCard(
                icon = Icons.Default.Schedule,
                label = "Time Spent",
                value = state.formatElapsedTime(), // Usando a função formatElapsedTime
                backgroundColor = Color(0xFFE3F2FD),
                iconColor = Color(0xFF2196F3)
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onBackToHome, modifier = Modifier.weight(1f)) {
                    Text("Back to Home")
                }
                Button(onClick = { /*TODO*/ }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.BarChart, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Statistics")
                }
            }
        }
    }
}

@Composable
fun PerformanceDetailCard(
    icon: ImageVector,
    label: String,
    value: String,
    backgroundColor: Color,
    iconColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = iconColor)
            Spacer(modifier = Modifier.width(16.dp))
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun QuizResultsScreenPreview() {

    val previewSampleQuestions = listOf(
        Question(id = "q1", text = "What is 2 + 2?", options = listOf("3", "4", "5", "6"), correctAnswer = "4"),
        Question(id = "q2", text = "What is 5 x 7?", options = listOf("30", "35", "40", "45"), correctAnswer = "35"),
        Question(id = "q3", text = "Capital of France?", options = listOf("London", "Paris"), correctAnswer = "Paris")

    )
    val previewUserAnswers = mapOf(0 to "4", 1 to "30", 2 to "London") // Exemplo de respostas
    val totalQuestions = previewSampleQuestions.size
    val correctAnswers = previewUserAnswers.count { (index, answer) ->
        previewSampleQuestions.getOrNull(index)?.correctAnswer == answer
    }
    val previewScore = if (totalQuestions > 0) (correctAnswers * 100) / totalQuestions else 0

    MaterialTheme {
        QuizResultsScreen(
            state = QuizUiState( // Use seu QuizUiState de domain.model
                questions = previewSampleQuestions,
                userAnswers = previewUserAnswers,
                score = previewScore,
                isFinished = true
            ),
            onBackToHome = {}
        )
    }
}

