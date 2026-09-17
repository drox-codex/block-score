package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.security.ScoreValidator
import com.example.security.SecurityUtils

/**
 * Manages offline game persistence for BLOCK SCORE.
 * Hardened with SHA-256 integrity checksums, bounds validation, and safe error handling.
 */
class GamePreferences(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("block_score_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_BEST_SCORE = "best_score"
        private const val KEY_UNLOCKED_LEVEL = "unlocked_adventure_level"
        private const val KEY_LEVEL_STARS_PREFIX = "level_stars_"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_MUSIC_ENABLED = "music_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_TOTAL_LINES = "total_lines"
        private const val KEY_TOTAL_BLOCKS = "total_blocks"
        private const val KEY_MAX_COMBO = "max_combo"
        private const val SIG_PREFIX = "sig_"
    }

    private fun getSecureInt(key: String, defaultValue: Int, maxAllowed: Int): Int {
        return try {
            val rawValue = prefs.getInt(key, defaultValue)
            if (rawValue < 0 || rawValue > maxAllowed) {
                // Out of range or corrupted
                return defaultValue
            }
            if (rawValue == defaultValue) {
                return defaultValue
            }
            val storedSig = prefs.getString("$SIG_PREFIX$key", null)
            if (storedSig != null && SecurityUtils.verifyChecksum(key, rawValue.toString(), storedSig, context)) {
                rawValue
            } else {
                // Tampered or corrupted save data: fail safe and fallback
                defaultValue
            }
        } catch (_: Exception) {
            defaultValue
        }
    }

    private fun putSecureInt(key: String, value: Int) {
        try {
            val sig = SecurityUtils.computeChecksum(key, value.toString(), context)
            prefs.edit()
                .putInt(key, value)
                .putString("$SIG_PREFIX$key", sig)
                .apply()
        } catch (_: Exception) {
            // Gracefully handle any disk/storage exception
        }
    }

    var bestScore: Int
        get() = getSecureInt(KEY_BEST_SCORE, 0, ScoreValidator.MAX_POSSIBLE_SCORE)
        set(value) {
            val sanitized = ScoreValidator.sanitizeScore(value)
            putSecureInt(KEY_BEST_SCORE, sanitized)
        }

    fun updateBestScore(newScore: Int): Boolean {
        val sanitized = ScoreValidator.sanitizeScore(newScore)
        if (sanitized > bestScore) {
            bestScore = sanitized
            return true
        }
        return false
    }

    var unlockedLevel: Int
        get() = getSecureInt(KEY_UNLOCKED_LEVEL, 1, 100)
        set(value) {
            val sanitized = ScoreValidator.sanitizeLevel(value)
            putSecureInt(KEY_UNLOCKED_LEVEL, sanitized)
        }

    fun unlockNextLevel(completedLevel: Int) {
        val sanitized = ScoreValidator.sanitizeLevel(completedLevel)
        if (sanitized >= unlockedLevel && unlockedLevel < 100) {
            unlockedLevel = sanitized + 1
        }
    }

    fun getLevelStars(level: Int): Int {
        return try {
            val safeLevel = ScoreValidator.sanitizeLevel(level)
            val stars = prefs.getInt("$KEY_LEVEL_STARS_PREFIX$safeLevel", 0)
            ScoreValidator.sanitizeStars(stars)
        } catch (_: Exception) {
            0
        }
    }

    fun saveLevelStars(level: Int, stars: Int) {
        try {
            val safeLevel = ScoreValidator.sanitizeLevel(level)
            val safeStars = ScoreValidator.sanitizeStars(stars)
            val current = getLevelStars(safeLevel)
            if (safeStars > current) {
                prefs.edit().putInt("$KEY_LEVEL_STARS_PREFIX$safeLevel", safeStars).apply()
            }
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    var soundEnabled: Boolean
        get() = try { prefs.getBoolean(KEY_SOUND_ENABLED, true) } catch (_: Exception) { true }
        set(value) = try { prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply() } catch (_: Exception) {}

    var musicEnabled: Boolean
        get() = try { prefs.getBoolean(KEY_MUSIC_ENABLED, true) } catch (_: Exception) { true }
        set(value) = try { prefs.edit().putBoolean(KEY_MUSIC_ENABLED, value).apply() } catch (_: Exception) {}

    var vibrationEnabled: Boolean
        get() = try { prefs.getBoolean(KEY_VIBRATION_ENABLED, true) } catch (_: Exception) { true }
        set(value) = try { prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, value).apply() } catch (_: Exception) {}

    var totalLines: Int
        get() = getSecureInt(KEY_TOTAL_LINES, 0, ScoreValidator.MAX_LIFETIME_STAT)
        set(value) = putSecureInt(KEY_TOTAL_LINES, ScoreValidator.sanitizeStat(value))

    var totalBlocks: Int
        get() = getSecureInt(KEY_TOTAL_BLOCKS, 0, ScoreValidator.MAX_LIFETIME_STAT)
        set(value) = putSecureInt(KEY_TOTAL_BLOCKS, ScoreValidator.sanitizeStat(value))

    var maxCombo: Int
        get() = getSecureInt(KEY_MAX_COMBO, 0, ScoreValidator.MAX_COMBO)
        set(value) = putSecureInt(KEY_MAX_COMBO, ScoreValidator.sanitizeCombo(value))

    fun addStats(linesCleared: Int, blocksPlaced: Int, combo: Int) {
        try {
            val safeLines = ScoreValidator.sanitizeStat(linesCleared)
            val safeBlocks = ScoreValidator.sanitizeStat(blocksPlaced)
            val safeCombo = ScoreValidator.sanitizeCombo(combo)

            totalLines += safeLines
            totalBlocks += safeBlocks
            if (safeCombo > maxCombo) {
                maxCombo = safeCombo
            }
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    fun resetAll() {
        try {
            prefs.edit().clear().apply()
            soundEnabled = true
            musicEnabled = true
            vibrationEnabled = true
            unlockedLevel = 1
        } catch (_: Exception) {
            // Graceful fallback
        }
    }
}
