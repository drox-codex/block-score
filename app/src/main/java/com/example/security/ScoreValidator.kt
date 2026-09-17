package com.example.security

import com.example.model.AdventureLevel

/**
 * Validates and sanitizes score calculations, combos, level progressions, and stats.
 * Prevents memory manipulation, impossible values, and corrupted data states.
 */
object ScoreValidator {
    // Realistic boundaries for an 8x8 block puzzle game
    const val MAX_POSSIBLE_SCORE = 10_000_000
    const val MAX_COMBO = 50
    const val MAX_LINES_PER_MOVE = 16 // 8 horizontal + 8 vertical maximum theoretically
    const val MAX_POINTS_PER_SINGLE_MOVE = 50_000
    const val MAX_LIFETIME_STAT = 50_000_000

    /**
     * Sanitizes score values within safe bounds.
     */
    fun sanitizeScore(score: Int): Int {
        return score.coerceIn(0, MAX_POSSIBLE_SCORE)
    }

    /**
     * Sanitizes combo counter.
     */
    fun sanitizeCombo(combo: Int): Int {
        return combo.coerceIn(0, MAX_COMBO)
    }

    /**
     * Sanitizes adventure level index.
     */
    fun sanitizeLevel(level: Int): Int {
        return level.coerceIn(1, AdventureLevel.TOTAL_LEVELS)
    }

    /**
     * Sanitizes level stars (0 to 3).
     */
    fun sanitizeStars(stars: Int): Int {
        return stars.coerceIn(0, 3)
    }

    /**
     * Sanitizes lifetime statistics (blocks placed, lines cleared).
     */
    fun sanitizeStat(stat: Int): Int {
        return stat.coerceIn(0, MAX_LIFETIME_STAT)
    }

    /**
     * Verifies that a proposed move score increment conforms to puzzle game rules.
     */
    fun validateMoveScore(
        tilesPlaced: Int,
        linesCleared: Int,
        combo: Int,
        claimedPoints: Int
    ): Boolean {
        if (claimedPoints < 0 || claimedPoints > MAX_POINTS_PER_SINGLE_MOVE) {
            return false
        }
        if (tilesPlaced !in 1..9) { // Polyomino pieces in 8x8 are at most 3x3=9 cells
            return false
        }
        if (linesCleared !in 0..MAX_LINES_PER_MOVE) {
            return false
        }
        if (combo !in 0..MAX_COMBO) {
            return false
        }
        return true
    }
}
