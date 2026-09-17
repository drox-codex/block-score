package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.GamePreferences
import com.example.security.ScoreValidator
import com.example.security.SecurityUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var context: Context
    private lateinit var prefs: GamePreferences

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        prefs = GamePreferences(context)
        prefs.resetAll()
    }

    @Test
    fun `read string from context`() {
        val appName = context.getString(R.string.app_name)
        assertEquals("BLOCK SCORE", appName)
    }

    @Test
    fun `checksum generation and verification works offline`() {
        val key = "best_score"
        val value = "1250"
        val checksum = SecurityUtils.computeChecksum(key, value, context)

        assertTrue(checksum.isNotEmpty())
        assertTrue(SecurityUtils.verifyChecksum(key, value, checksum, context))
        assertFalse(SecurityUtils.verifyChecksum(key, "999999", checksum, context))
    }

    @Test
    fun `preferences store and retrieve verified best score`() {
        prefs.bestScore = 2400
        assertEquals(2400, prefs.bestScore)

        // Lower score should not overwrite
        val updated = prefs.updateBestScore(1500)
        assertFalse(updated)
        assertEquals(2400, prefs.bestScore)

        // Higher score should overwrite
        val updatedHigher = prefs.updateBestScore(3200)
        assertTrue(updatedHigher)
        assertEquals(3200, prefs.bestScore)
    }

    @Test
    fun `corrupted preference data gracefully falls back without crashing`() {
        // Artificially modify shared prefs without updating checksum
        val rawPrefs = context.getSharedPreferences("block_score_prefs", Context.MODE_PRIVATE)
        rawPrefs.edit().putInt("best_score", 999999).apply()

        // Reading should detect signature mismatch and safely fall back to 0 without crashing
        val score = prefs.bestScore
        assertEquals(0, score)
    }

    @Test
    fun `score validator prevents impossible moves and values`() {
        // Valid move: 4 tiles, 1 line cleared, combo 1, 140 pts
        assertTrue(ScoreValidator.validateMoveScore(tilesPlaced = 4, linesCleared = 1, combo = 1, claimedPoints = 140))

        // Impossible move: negative points
        assertFalse(ScoreValidator.validateMoveScore(tilesPlaced = 4, linesCleared = 1, combo = 1, claimedPoints = -100))

        // Impossible move: 100 tiles placed in single 8x8 move
        assertFalse(ScoreValidator.validateMoveScore(tilesPlaced = 100, linesCleared = 1, combo = 1, claimedPoints = 500))

        // Sanitization bounds
        assertEquals(10_000_000, ScoreValidator.sanitizeScore(999_999_999))
        assertEquals(0, ScoreValidator.sanitizeScore(-500))
        assertEquals(50, ScoreValidator.sanitizeCombo(120))
        assertEquals(1, ScoreValidator.sanitizeLevel(-5))
        assertEquals(100, ScoreValidator.sanitizeLevel(250))
        assertEquals(3, ScoreValidator.sanitizeStars(10))
    }

    @Test
    fun `offline package validation returns valid`() {
        assertTrue(SecurityUtils.isPackageValid(context))
    }
}
