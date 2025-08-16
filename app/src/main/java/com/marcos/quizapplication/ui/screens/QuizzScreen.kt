package com.marcos.quizapplication.ui.screens


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.marcos.quizapplication.domain.model.QuizUiState
import com.marcos.quizapplication.ui.viewmodel.QuizViewModel

@Composable
fun QuizRoute(
    viewModel: QuizViewModel,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isFinished) {
        QuizResultsScreen(
            state = uiState,
            onBackToHome = onNavigateHome
        )
    } else {
        QuizScreen(
            uiState = uiState,
            onAnswerSelected = viewModel::onAnswerSelected,
            onNextClicked = viewModel::onNextClicked,
            onNavigateBack = onNavigateBack
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizScreen(
    uiState: QuizUiState,
    onAnswerSelected: (String) -> Unit,
    onNextClicked: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val currentQuestion = uiState.currentQuestion

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mathematics Basics") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (currentQuestion != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                QuizHeader(
                    progress = uiState.progress,
                    questionNumber = uiState.currentQuestionIndex + 1,
                    totalQuestions = uiState.questions.size
                )
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = currentQuestion.text,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))

                currentQuestion.options.forEach { option ->
                    AnswerOption(
                        text = option,
                        isSelected = uiState.selectedAnswer == option,
                        onOptionSelected = { onAnswerSelected(option) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(onClick = { /* TODO: Skip logic */ }) {
                        Text("Skip")
                    }
                    Button(
                        onClick = onNextClicked,
                        enabled = uiState.selectedAnswer != null
                    ) {
                        Text("Next")
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun QuizHeader(progress: Float, questionNumber: Int, totalQuestions: Int) {
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progressAnimation")

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Question $questionNumber of $totalQuestions", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "${(animatedProgress * 100).toInt()}% Complete",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        )
    }
}

@Composable
fun AnswerOption(
    text: String,
    isSelected: Boolean,
    onOptionSelected: () -> Unit
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onOptionSelected)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onOptionSelected
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, fontSize = 16.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun QuizScreenPreview() {
    MaterialTheme {
        QuizHeader(progress = 0.4f, questionNumber = 2, totalQuestions = 5)
    }
}