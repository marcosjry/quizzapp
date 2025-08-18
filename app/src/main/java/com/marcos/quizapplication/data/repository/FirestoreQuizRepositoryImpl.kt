
package com.marcos.quizapplication.data.repository

import android.util.Log // IMPORTAR Log
import com.google.firebase.firestore.FirebaseFirestore
import com.marcos.quizapplication.domain.contracts.QuizRepository
import com.marcos.quizapplication.domain.model.Question // IMPORTAR
import com.marcos.quizapplication.model.QuizInfo
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreQuizRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : QuizRepository {

    companion object {
        private const val QUIZZES_COLLECTION = "quizzes"
        private const val QUESTIONS_SUBCOLLECTION = "questions" // NOME DA SUBCOLEÇÃO
        private const val TAG = "QuizRepositoryImpl" // TAG para Logs
    }

    override suspend fun getAvailableQuizzes(): Result<List<QuizInfo>> {
        return try {
            val snapshot = firestore.collection(QUIZZES_COLLECTION).get().await()
            val quizzes = snapshot.documents.mapNotNull { document ->
                QuizInfo(
                    id = document.id,
                    title = document.getString("title") ?: "",
                    description = document.getString("description") ?: "",
                    time = document.getString("time") ?: "",
                    difficulty = document.getString("difficulty") ?: "",
                    difficultyColorHex = document.getString("difficultyColor") ?: "FFFFFFFF"
                )
            }
            Log.d(TAG, "Fetched ${quizzes.size} available quizzes.")
            Result.success(quizzes)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching available quizzes", e)
            Result.failure(e)
        }
    }

    override suspend fun getQuestionsForQuiz(quizId: String): Result<List<Question>> {
        Log.d(TAG, "Fetching questions for quizId: $quizId")
        if (quizId.isBlank()) {
            Log.w(TAG, "quizId is blank. Returning empty list.")
            return Result.success(emptyList())
        }
        return try {
            val snapshot = firestore.collection(QUIZZES_COLLECTION)
                .document(quizId)
                .collection(QUESTIONS_SUBCOLLECTION) // Acessa a subcoleção de perguntas
                .get()
                .await()

            val questions = snapshot.documents.mapNotNull { document ->
                val options = document.get("options") as? List<String> ?: emptyList() // Lida com o tipo List
                Question(
                    id = document.id,
                    text = document.getString("text") ?: "",
                    options = options,
                    correctAnswer = document.getString("correctAnswer") ?: ""
                )
            }
            Log.d(TAG, "Fetched ${questions.size} questions for quizId $quizId: $questions")
            Result.success(questions)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching questions for quizId $quizId", e)
            Result.failure(e)
        }
    }

    override suspend fun getQuizInfo(quizId: String): Result<QuizInfo> {
        return try {
            val documentSnapshot = firestore.collection(QUIZZES_COLLECTION)
                .document(quizId)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val quizInfo = QuizInfo(
                    id = documentSnapshot.id,
                    title = documentSnapshot.getString("title") ?: "",
                    description = documentSnapshot.getString("description") ?: "",
                    time = documentSnapshot.getString("time") ?: "",
                    difficulty = documentSnapshot.getString("difficulty") ?: "",
                    difficultyColorHex = documentSnapshot.getString("difficultyColor") ?: "FFFFFFFF"
                )
                Result.success(quizInfo)
            } else {
                Result.failure(IllegalStateException("Quiz não encontrado"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching quiz info for quizId $quizId", e)
            Result.failure(e)
        }
    }
}
