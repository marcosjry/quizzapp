package com.marcos.quizapplication.domain.model

data class RankingUiState(
    val topPerformers: List<RankedUser> = emptyList()
) {
}