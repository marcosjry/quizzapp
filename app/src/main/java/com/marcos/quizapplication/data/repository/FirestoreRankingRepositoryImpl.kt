package com.marcos.quizapplication.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.marcos.quizapplication.domain.contracts.RankingRepository
import com.marcos.quizapplication.domain.model.QuizAttempt
import com.marcos.quizapplication.domain.model.RankedUser
import com.marcos.quizapplication.domain.model.UserStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreRankingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RankingRepository {

    companion object {
        private const val RANKINGS_COLLECTION = "rankings"
        private const val QUIZ_HISTORY_COLLECTION = "quiz_history"
        private const val TAG = "RankingRepository"
    }

    override fun getTopPerformers(): Flow<List<RankedUser>> {
        val query = firestore.collection("rankings")
            .orderBy("totalPoints", Query.Direction.DESCENDING)
            .limit(5)
        return query.snapshots().map { snapshot ->
            snapshot.documents.mapIndexed { index, document ->
                RankedUser(
                    position = index + 1,
                    displayName = document.getString("displayName") ?: "Unknown User",
                    photoUrl = document.getString("photoUrl") ?: "",
                    totalPoints = document.getLong("totalPoints")?.toInt() ?: 0
                )
            }
        }
    }

    override suspend fun updateUserPoints(userId: String, pointsToAdd: Int): Result<Unit> {
        return try {
            val rankingRef = firestore.collection("rankings").document(userId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(rankingRef)
                val currentPoints = snapshot.getLong("totalPoints")?.toInt() ?: 0
                val newTotalPoints = currentPoints + pointsToAdd

                transaction.update(rankingRef, "totalPoints", newTotalPoints)
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("RankingRepository", "Error updating user points", e)
            Result.failure(e)
        }
    }

    override suspend fun saveQuizAttempt(userId: String, quizAttempt: QuizAttempt): Result<Unit> {
        return try {
            // Salvamos o histórico de quiz em uma subcoleção para cada usuário
            firestore.collection(RANKINGS_COLLECTION)
                .document(userId)
                .collection(QUIZ_HISTORY_COLLECTION)
                .add(quizAttempt)
                .await()

            Log.d(TAG, "Quiz attempt saved for user $userId: $quizAttempt")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving quiz attempt for user $userId", e)
            Result.failure(e)
        }
    }

    override fun getUserQuizAttempts(userId: String): Flow<List<QuizAttempt>> {
        return firestore.collection(RANKINGS_COLLECTION)
            .document(userId)
            .collection(QUIZ_HISTORY_COLLECTION)
            .orderBy("completedAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { document ->
                    try {
                        QuizAttempt(
                            quizId = document.getString("quizId") ?: "",
                            quizTitle = document.getString("quizTitle") ?: "",
                            score = document.getLong("score")?.toInt() ?: 0,
                            totalQuestions = document.getLong("totalQuestions")?.toInt() ?: 0,
                            correctAnswers = document.getLong("correctAnswers")?.toInt() ?: 0,
                            timeSpentInSeconds = document.getLong("timeSpentInSeconds")?.toInt() ?: 0,
                            completedAt = document.getLong("completedAt") ?: 0
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing quiz attempt", e)
                        null
                    }
                }
            }
    }

    override fun getUserStats(userId: String): Flow<UserStats> {
        return getUserQuizAttempts(userId).map { attempts ->
            if (attempts.isEmpty()) {
                UserStats()
            } else {
                val totalQuizzes = attempts.size
                val totalQuestions = attempts.sumOf { it.totalQuestions }
                val totalCorrectAnswers = attempts.sumOf { it.correctAnswers }
                val totalTimeSpent = attempts.sumOf { it.timeSpentInSeconds }

                UserStats(
                    totalQuizzes = totalQuizzes,
                    totalCorrectAnswers = totalCorrectAnswers,
                    totalQuestions = totalQuestions,
                    averageScore = if (totalQuestions > 0)
                        (totalCorrectAnswers.toFloat() / totalQuestions) * 100 else 0f,
                    averageTimePerQuizInSeconds = if (totalQuizzes > 0)
                        totalTimeSpent.toFloat() / totalQuizzes else 0f
                )
            }
        }
    }
}
