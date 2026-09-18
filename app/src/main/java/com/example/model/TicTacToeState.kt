package com.example.model

enum class TicTacToePlayer {
    X, O, NONE
}

enum class TicTacToeDifficulty {
    EASY, MEDIUM, HARD
}

data class TicTacToeState(
    val board: List<TicTacToePlayer> = List(9) { TicTacToePlayer.NONE },
    val currentPlayer: TicTacToePlayer = TicTacToePlayer.X,
    val winner: TicTacToePlayer? = null,
    val isDraw: Boolean = false,
    val difficulty: TicTacToeDifficulty = TicTacToeDifficulty.MEDIUM,
    val isGameOver: Boolean = false,
    val playerIsX: Boolean = true // Player is usually X
)
