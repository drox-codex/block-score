package com.example.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DailyChallenge(
    val dateString: String,
    val targetLines: Int,
    val targetScore: Int,
    val maxMoves: Int,
    val isCompleted: Boolean = false,
    val currentLines: Int = 0,
    val currentScore: Int = 0,
    val movesMade: Int = 0
) {
    val isSuccess: Boolean get() = currentLines >= targetLines && currentScore >= targetScore && movesMade <= maxMoves
    val isFailed: Boolean get() = movesMade >= maxMoves && !isSuccess

    companion object {
        fun getTodayDate(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return sdf.format(Date())
        }

        fun generateForDate(dateStr: String): DailyChallenge {
            val hash = dateStr.hashCode().let { if (it < 0) -it else it }
            val lines = 12 + (hash % 10) // 12-21 lines
            val score = 800 + (hash % 600) // 800-1400 pts
            val moves = 22 + (hash % 8) // 22-30 moves
            return DailyChallenge(
                dateString = dateStr,
                targetLines = lines,
                targetScore = score,
                maxMoves = moves
            )
        }
    }
}

data class SpinReward(
    val id: Int,
    val label: String,
    val iconName: String,
    val rewardValue: Int
)

object SpinWheelConfig {
    val REWARDS = listOf(
        SpinReward(0, "+3 Hints", "hint", 3),
        SpinReward(1, "+100 Score", "star", 100),
        SpinReward(2, "Aqua Glow", "palette", 1),
        SpinReward(3, "+5 Hints", "hint", 5),
        SpinReward(4, "+250 Score", "star", 250),
        SpinReward(5, "Lucky Spin", "refresh", 1)
    )
}
