package com.example.model

data class OneLineLevel(
    val levelNumber: Int,
    val gridWidth: Int,
    val gridHeight: Int,
    val validCells: Set<Pair<Int, Int>>, // Set of valid (row, col) coordinates.
    val startCell: Pair<Int, Int>? = null, // If not null, player must start here.
    val solutionPath: List<Pair<Int, Int>> = emptyList() // Verified winnable path visiting all validCells
)
