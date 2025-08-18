package com.marcos.quizapplication.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.marcos.quizapplication.data.repository.FirestoreQuizRepositoryImpl
import com.marcos.quizapplication.data.repository.authentication.FirebaseAuthRepositoryImpl
import com.marcos.quizapplication.domain.contracts.AuthRepository
import com.marcos.quizapplication.domain.contracts.QuizRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthRepository {
        return FirebaseAuthRepositoryImpl(firebaseAuth, firestore)
    }

    @Provides
    @Singleton
    fun provideQuizRepository(firestore: FirebaseFirestore): QuizRepository {
        return FirestoreQuizRepositoryImpl(firestore)
    }
}