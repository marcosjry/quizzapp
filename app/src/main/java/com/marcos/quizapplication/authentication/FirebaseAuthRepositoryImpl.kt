package com.marcos.quizapplication.authentication

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.marcos.quizapplication.domain.contracts.AuthRepository
import com.marcos.quizapplication.domain.contracts.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    private val _authState = MutableStateFlow(AuthState(isInitialLoading = true))

    init {
        firebaseAuth.addAuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                firestore.collection("users").document(firebaseUser.uid).get()
                    .addOnSuccessListener { document ->
                        val usernameFromFirestore = if (document != null && document.exists()) {
                            document.getString("username")
                        } else {
                            null
                        }
                        _authState.update {
                            it.copy(
                                user = firebaseUser.toDomainUser(usernameFromFirestore),
                                isInitialLoading = false
                            )
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w("FirebaseAuthRepo", "Error fetching user details from Firestore", e)
                        _authState.update {
                            it.copy(
                                user = firebaseUser.toDomainUser(null),
                                isInitialLoading = false
                            )
                        }
                    }
            } else {
                _authState.update {
                    it.copy(
                        user = null,
                        isInitialLoading = false
                    )
                }
            }
        }
    }

    override fun getAuthState(): StateFlow<AuthState> = _authState.asStateFlow()

    override suspend fun signUp(username: String, email: String, password: String): Result<Unit> {
        return try {
            // Verifica se o nome de usuário é único no firestore
            val usernameQuery = firestore.collection("users")
                .whereEqualTo("username", username)
                .get()
                .await()

            if (!usernameQuery.isEmpty) {
                Log.w("FirebaseAuthRepo", "Sign up failed: Username already taken.")
                return Result.failure(Exception("Username already taken. Please choose another one."))
            }

            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val createdFirebaseUser = authResult.user

            if (createdFirebaseUser != null) {
                val userDocument = mapOf(
                    "uid" to createdFirebaseUser.uid,
                    "username" to username,
                    "email" to email
                )
                firestore.collection("users").document(createdFirebaseUser.uid)
                    .set(userDocument)
                    .await()

                _authState.update {
                    it.copy(
                        user = createdFirebaseUser.toDomainUser(username),
                        isInitialLoading = false
                    )
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to create user account (Firebase Auth user is null)."))
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            Log.w("FirebaseAuthRepo", "Sign up failed: Email already in use.", e)
            Result.failure(Exception("The email address is already in use by another account."))
        } catch (e: FirebaseAuthWeakPasswordException) {
            Log.w("FirebaseAuthRepo", "Sign up failed: Weak password.", e)
            Result.failure(Exception("The password is too weak. It should be at least 6 characters."))
        } catch (e: Exception) {
            Log.e("FirebaseAuthRepo", "Sign up failed: ${e.message}", e)
            Result.failure(Exception("An unexpected error occurred during sign up: ${e.message}"))
        }
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w("FirebaseAuthRepo", "Sign in failed", e)
            Result.failure(e)
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}


private fun FirebaseUser.toDomainUser(usernameFromFirestore: String?): com.marcos.quizapplication.domain.model.User {
    return com.marcos.quizapplication.domain.model.User(
        uid = this.uid,
        email = this.email,
        username = usernameFromFirestore
    )
}
