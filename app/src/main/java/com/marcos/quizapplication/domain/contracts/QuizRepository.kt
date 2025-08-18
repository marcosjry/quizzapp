package com.marcos.quizapplication.domain.contracts

import com.marcos.quizapplication.domain.model.Question
import com.marcos.quizapplication.model.QuizInfo

interface QuizRepository {
    suspend fun getAvailableQuizzes(): Result<List<QuizInfo>>
    suspend fun getQuestionsForQuiz(quizId: String): Result<List<Question>>
}