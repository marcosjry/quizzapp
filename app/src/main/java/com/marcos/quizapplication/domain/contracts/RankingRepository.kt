package com.marcos.quizapplication.domain.contracts

import com.marcos.quizapplication.domain.model.QuizAttempt
import com.marcos.quizapplication.domain.model.RankedUser
import com.marcos.quizapplication.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

interface RankingRepository {
    fun getTopPerformers(): Flow<List<RankedUser>>
    suspend fun updateUserPoints(userId: String, pointsToAdd: Int): Result<Unit>
    suspend fun saveQuizAttempt(userId: String, quizAttempt: QuizAttempt): Result<Unit>
    fun getUserQuizAttempts(userId: String): Flow<List<QuizAttempt>>
    fun getUserStats(userId: String): Flow<UserStats>
}