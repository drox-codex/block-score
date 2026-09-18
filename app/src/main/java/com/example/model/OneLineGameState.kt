package com.example.model

data class OneLineGameState(
    val currentLevel: OneLineLevel,
    val path: List<Pair<Int, Int>> = emptyList(), // Sequence of drawn cells
    val isLevelComplete: Boolean = false
) {
    val maxPathLength: Int get() = currentLevel.validCells.size
}
