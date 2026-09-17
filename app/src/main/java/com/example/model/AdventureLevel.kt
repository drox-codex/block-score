package com.example.model

enum class ObjectiveType {
    SCORE,
    LINES,
    COMBO
}

data class AdventureLevel(
    val levelNumber: Int,
    val objectiveType: ObjectiveType,
    val targetValue: Int,
    val maxMoves: Int = 0, // 0 means unlimited moves until target reached or board filled
    val oneStarTarget: Int,
    val twoStarTarget: Int,
    val threeStarTarget: Int
) {
    val title: String get() = "Level $levelNumber"

    val objectiveDescription: String get() = when (objectiveType) {
        ObjectiveType.SCORE -> "Reach $targetValue Score"
        ObjectiveType.LINES -> "Clear $targetValue Lines"
        ObjectiveType.COMBO -> "Get a ${targetValue}x Combo"
    }

    companion object {
        const val TOTAL_LEVELS = 100

        fun getLevel(levelNumber: Int): AdventureLevel {
            val num = levelNumber.coerceIn(1, TOTAL_LEVELS)
            val type = when {
                num % 5 == 0 -> ObjectiveType.COMBO
                num % 2 == 0 -> ObjectiveType.LINES
                else -> ObjectiveType.SCORE
            }

            return when (type) {
                ObjectiveType.SCORE -> {
                    val baseScore = 200 + (num * 60)
                    AdventureLevel(
                        levelNumber = num,
                        objectiveType = ObjectiveType.SCORE,
                        targetValue = baseScore,
                        oneStarTarget = baseScore,
                        twoStarTarget = (baseScore * 1.35).toInt(),
                        threeStarTarget = (baseScore * 1.8).toInt()
                    )
                }
                ObjectiveType.LINES -> {
                    val targetLines = 3 + (num / 4).coerceAtMost(25)
                    AdventureLevel(
                        levelNumber = num,
                        objectiveType = ObjectiveType.LINES,
                        targetValue = targetLines,
                        oneStarTarget = targetLines,
                        twoStarTarget = targetLines + 2,
                        threeStarTarget = targetLines + 5
                    )
                }
                ObjectiveType.COMBO -> {
                    val targetCombo = if (num < 20) 2 else if (num < 50) 3 else 4
                    AdventureLevel(
                        levelNumber = num,
                        objectiveType = ObjectiveType.COMBO,
                        targetValue = targetCombo,
                        oneStarTarget = targetCombo,
                        twoStarTarget = targetCombo + 1,
                        threeStarTarget = targetCombo + 2
                    )
                }
            }
        }
    }
}
