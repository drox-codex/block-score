package com.example.model

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val currentProgress: Int,
    val targetProgress: Int,
    val isUnlocked: Boolean = currentProgress >= targetProgress,
    val iconName: String = "trophy"
) {
    val progressFraction: Float
        get() = (currentProgress.toFloat() / targetProgress.toFloat()).coerceIn(0f, 1f)
}
