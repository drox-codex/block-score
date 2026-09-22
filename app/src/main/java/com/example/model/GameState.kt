package com.example.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

enum class GameMode {
    CLASSIC,
    ADVENTURE,
    BLITZ,
    ZEN,
    DAILY
}

data class Particle(
    val id: Long,
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val size: Float,
    val alpha: Float = 1f,
    val lifeMs: Long = 600L
)

data class FloatingText(
    val id: Long,
    val text: String,
    val x: Float,
    val y: Float,
    val color: Color,
    val alpha: Float = 1f,
    val scale: Float = 1f
)

data class GameState(
    val board: List<List<Int>> = List(8) { List(8) { 0 } },
    val trayPieces: List<BlockPiece?> = listOf(null, null, null),
    val score: Int = 0,
    val bestScore: Int = 0,
    val currentCombo: Int = 0,
    val highestCombo: Int = 0,
    val totalLinesCleared: Int = 0,
    val totalBlocksPlaced: Int = 0,
    val isGameOver: Boolean = false,
    val isLevelWon: Boolean = false,
    val isPaused: Boolean = false,
    val gameMode: GameMode = GameMode.CLASSIC,
    val currentAdventureLevel: Int = 1,
    val adventureProgress: Int = 0,
    val adventureStars: Int = 0,
    val blitzTimeRemainingSec: Int = 90,
    val dailyTargetLines: Int = 15,
    val dailyCurrentLines: Int = 0,
    val dailyMovesRemaining: Int = 25,
    val canUndo: Boolean = false,
    val clearingRows: Set<Int> = emptySet(),
    val clearingCols: Set<Int> = emptySet(),
    val particles: List<Particle> = emptyList(),
    val floatingTexts: List<FloatingText> = emptyList(),
    val lastMoveClearedLines: Int = 0
) {
    val isClassic: Boolean get() = gameMode == GameMode.CLASSIC
    val isAdventure: Boolean get() = gameMode == GameMode.ADVENTURE
    val isBlitz: Boolean get() = gameMode == GameMode.BLITZ
    val isZen: Boolean get() = gameMode == GameMode.ZEN
    val isDaily: Boolean get() = gameMode == GameMode.DAILY

    companion object {
        const val BOARD_SIZE = 8
    }
}
