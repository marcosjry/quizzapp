package com.marcos.quizapplication.domain.model

data class RankedUser(
    val position: Int,
    val photoUrl: String,
    val displayName: String,
    val totalPoints: Int
)
