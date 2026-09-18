package com.example.model

object OneLineLevels {
    val levels: List<OneLineLevel> = listOf(
        // Level 1: 3x3 square (9 cells)
        createLevel(
            levelNumber = 1,
            gridWidth = 3,
            gridHeight = 3,
            path = listOf(
                0 to 0, 0 to 1, 0 to 2,
                1 to 2, 1 to 1, 1 to 0,
                2 to 0, 2 to 1, 2 to 2
            )
        ),

        // Level 2: 4x3 hollow ring (10 cells)
        createLevel(
            levelNumber = 2,
            gridWidth = 4,
            gridHeight = 3,
            path = listOf(
                2 to 0, 1 to 0, 0 to 0, 0 to 1, 0 to 2, 0 to 3,
                1 to 3, 2 to 3, 2 to 2, 2 to 1
            )
        ),

        // Level 3: 4x4 rounded diamond (12 cells)
        createLevel(
            levelNumber = 3,
            gridWidth = 4,
            gridHeight = 4,
            path = listOf(
                3 to 1, 3 to 2, 2 to 2, 2 to 3, 1 to 3, 1 to 2,
                0 to 2, 0 to 1, 1 to 1, 1 to 0, 2 to 0, 2 to 1
            )
        ),

        // Level 4: 5x5 outer border loop (16 cells)
        createLevel(
            levelNumber = 4,
            gridWidth = 5,
            gridHeight = 5,
            path = listOf(
                0 to 0, 0 to 1, 0 to 2, 0 to 3, 0 to 4,
                1 to 4, 2 to 4, 3 to 4, 4 to 4,
                4 to 3, 4 to 2, 4 to 1, 4 to 0,
                3 to 0, 2 to 0, 1 to 0
            )
        ),

        // Level 5: 4x4 complete grid (16 cells)
        createLevel(
            levelNumber = 5,
            gridWidth = 4,
            gridHeight = 4,
            path = listOf(
                0 to 0, 0 to 1, 0 to 2, 0 to 3,
                1 to 3, 1 to 2, 1 to 1, 1 to 0,
                2 to 0, 2 to 1, 2 to 2, 2 to 3,
                3 to 3, 3 to 2, 3 to 1, 3 to 0
            )
        ),

        // Level 6: 5x4 "U" horseshoe maze (14 cells)
        createLevel(
            levelNumber = 6,
            gridWidth = 4,
            gridHeight = 5,
            path = listOf(
                0 to 0, 1 to 0, 2 to 0, 3 to 0, 4 to 0,
                4 to 1, 4 to 2, 4 to 3,
                3 to 3, 2 to 3, 1 to 3, 0 to 3,
                0 to 2, 1 to 2
            )
        ),

        // Level 7: 5x5 Spiral inward (21 cells)
        createLevel(
            levelNumber = 7,
            gridWidth = 5,
            gridHeight = 5,
            path = listOf(
                0 to 0, 0 to 1, 0 to 2, 0 to 3, 0 to 4,
                1 to 4, 2 to 4, 3 to 4, 4 to 4,
                4 to 3, 4 to 2, 4 to 1, 4 to 0,
                3 to 0, 2 to 0, 1 to 0,
                1 to 1, 1 to 2, 1 to 3,
                2 to 3, 3 to 3
            )
        ),

        // Level 8: 5x5 Cross Labyrinth (15 cells)
        createLevel(
            levelNumber = 8,
            gridWidth = 5,
            gridHeight = 5,
            path = listOf(
                0 to 2, 1 to 2, 1 to 1, 2 to 1, 2 to 0,
                3 to 0, 3 to 1, 4 to 1, 4 to 2, 4 to 3,
                3 to 3, 3 to 4, 2 to 4, 2 to 3, 1 to 3
            )
        ),

        // Level 9: 5x5 Zig-Zag (17 cells)
        createLevel(
            levelNumber = 9,
            gridWidth = 5,
            gridHeight = 5,
            path = listOf(
                0 to 0, 0 to 1, 0 to 2, 0 to 3, 0 to 4,
                1 to 4, 1 to 3, 1 to 2, 1 to 1,
                2 to 1, 2 to 2, 2 to 3, 2 to 4,
                3 to 4, 3 to 3, 3 to 2, 3 to 1
            )
        ),

        // Level 10: 5x5 Full Snake (25 cells)
        createLevel(
            levelNumber = 10,
            gridWidth = 5,
            gridHeight = 5,
            path = listOf(
                0 to 0, 0 to 1, 0 to 2, 0 to 3, 0 to 4,
                1 to 4, 1 to 3, 1 to 2, 1 to 1, 1 to 0,
                2 to 0, 2 to 1, 2 to 2, 2 to 3, 2 to 4,
                3 to 4, 3 to 3, 3 to 2, 3 to 1, 3 to 0,
                4 to 0, 4 to 1, 4 to 2, 4 to 3, 4 to 4
            )
        ),

        // Level 11: 6x5 Comb Pillars (21 cells)
        createLevel(
            levelNumber = 11,
            gridWidth = 6,
            gridHeight = 5,
            path = listOf(
                0 to 0, 1 to 0, 2 to 0, 3 to 0, 4 to 0,
                4 to 1, 3 to 1, 2 to 1, 1 to 1, 0 to 1,
                0 to 2, 0 to 3,
                1 to 3, 2 to 3, 3 to 3, 4 to 3,
                4 to 4, 3 to 4, 2 to 4, 1 to 4, 0 to 4
            )
        ),

        // Level 12: 6x5 Meander (22 cells)
        createLevel(
            levelNumber = 12,
            gridWidth = 6,
            gridHeight = 5,
            path = listOf(
                0 to 0, 1 to 0, 2 to 0, 3 to 0, 4 to 0,
                4 to 1, 4 to 2, 4 to 3, 4 to 4, 4 to 5,
                3 to 5, 2 to 5, 1 to 5, 0 to 5,
                0 to 4, 1 to 4, 2 to 4, 3 to 4,
                3 to 3, 2 to 3, 1 to 3, 0 to 3
            )
        ),

        // Level 13: 6x6 Fortress (24 cells)
        createLevel(
            levelNumber = 13,
            gridWidth = 6,
            gridHeight = 6,
            path = listOf(
                0 to 0, 0 to 1, 0 to 2, 0 to 3, 0 to 4, 0 to 5,
                1 to 5, 2 to 5, 3 to 5, 4 to 5, 5 to 5,
                5 to 4, 5 to 3, 5 to 2, 5 to 1, 5 to 0,
                4 to 0, 3 to 0, 2 to 0, 1 to 0,
                1 to 1, 1 to 2, 2 to 2, 2 to 1
            )
        ),

        // Level 14: 6x6 Serpentine (24 cells)
        createLevel(
            levelNumber = 14,
            gridWidth = 6,
            gridHeight = 6,
            path = listOf(
                0 to 0, 1 to 0, 2 to 0, 3 to 0, 4 to 0, 5 to 0,
                5 to 1, 4 to 1, 3 to 1, 2 to 1, 1 to 1, 0 to 1,
                0 to 2, 1 to 2, 2 to 2, 3 to 2, 4 to 2, 5 to 2,
                5 to 3, 4 to 3, 3 to 3, 2 to 3, 1 to 3, 0 to 3
            )
        ),

        // Level 15: 6x6 Spiral Inward (28 cells)
        createLevel(
            levelNumber = 15,
            gridWidth = 6,
            gridHeight = 6,
            path = listOf(
                0 to 0, 0 to 1, 0 to 2, 0 to 3, 0 to 4, 0 to 5,
                1 to 5, 2 to 5, 3 to 5, 4 to 5, 5 to 5,
                5 to 4, 5 to 3, 5 to 2, 5 to 1, 5 to 0,
                4 to 0, 3 to 0, 2 to 0, 1 to 0,
                1 to 1, 1 to 2, 1 to 3, 1 to 4,
                2 to 4, 3 to 4, 4 to 4, 4 to 3
            )
        ),

        // Level 16: 6x6 Five-Strip Ribbon (30 cells)
        createLevel(
            levelNumber = 16,
            gridWidth = 6,
            gridHeight = 6,
            path = listOf(
                0 to 0, 0 to 1, 0 to 2, 0 to 3, 0 to 4, 0 to 5,
                1 to 5, 1 to 4, 1 to 3, 1 to 2, 1 to 1, 1 to 0,
                2 to 0, 2 to 1, 2 to 2, 2 to 3, 2 to 4, 2 to 5,
                3 to 5, 3 to 4, 3 to 3, 3 to 2, 3 to 1, 3 to 0,
                4 to 0, 4 to 1, 4 to 2, 4 to 3, 4 to 4, 4 to 5
            )
        ),

        // Level 17: 6x6 Vertical Weaver (30 cells)
        createLevel(
            levelNumber = 17,
            gridWidth = 6,
            gridHeight = 6,
            path = listOf(
                5 to 0, 4 to 0, 3 to 0, 2 to 0, 1 to 0, 0 to 0,
                0 to 1, 1 to 1, 2 to 1, 3 to 1, 4 to 1, 5 to 1,
                5 to 2, 4 to 2, 3 to 2, 2 to 2, 1 to 2, 0 to 2,
                0 to 3, 1 to 3, 2 to 3, 3 to 3, 4 to 3, 5 to 3,
                5 to 4, 4 to 4, 3 to 4, 2 to 4, 1 to 4, 0 to 4
            )
        ),

        // Level 18: 6x6 Triple Coil (32 cells)
        createLevel(
            levelNumber = 18,
            gridWidth = 6,
            gridHeight = 6,
            path = listOf(
                0 to 0, 0 to 1, 0 to 2, 0 to 3, 0 to 4, 0 to 5,
                1 to 5, 2 to 5, 3 to 5, 4 to 5, 5 to 5,
                5 to 4, 5 to 3, 5 to 2, 5 to 1, 5 to 0,
                4 to 0, 3 to 0, 2 to 0, 1 to 0,
                1 to 1, 1 to 2, 1 to 3, 1 to 4,
                2 to 4, 3 to 4, 4 to 4,
                4 to 3, 4 to 2, 4 to 1,
                3 to 1, 3 to 2
            )
        ),

        // Level 19: 6x6 Full Grid Grand Master (36 cells)
        createLevel(
            levelNumber = 19,
            gridWidth = 6,
            gridHeight = 6,
            path = listOf(
                0 to 0, 0 to 1, 0 to 2, 0 to 3, 0 to 4, 0 to 5,
                1 to 5, 1 to 4, 1 to 3, 1 to 2, 1 to 1, 1 to 0,
                2 to 0, 2 to 1, 2 to 2, 2 to 3, 2 to 4, 2 to 5,
                3 to 5, 3 to 4, 3 to 3, 3 to 2, 3 to 1, 3 to 0,
                4 to 0, 4 to 1, 4 to 2, 4 to 3, 4 to 4, 4 to 5,
                5 to 5, 5 to 4, 5 to 3, 5 to 2, 5 to 1, 5 to 0
            )
        ),

        // Level 20: 7x6 Apex Labyrinth (38 cells)
        createLevel(
            levelNumber = 20,
            gridWidth = 7,
            gridHeight = 6,
            path = listOf(
                0 to 0, 0 to 1, 0 to 2, 0 to 3, 0 to 4, 0 to 5, 0 to 6,
                1 to 6, 2 to 6, 3 to 6, 4 to 6, 5 to 6,
                5 to 5, 5 to 4, 5 to 3, 5 to 2, 5 to 1, 5 to 0,
                4 to 0, 3 to 0, 2 to 0, 1 to 0,
                1 to 1, 1 to 2, 1 to 3, 1 to 4, 1 to 5,
                2 to 5, 3 to 5, 4 to 5,
                4 to 4, 4 to 3, 4 to 2, 4 to 1,
                3 to 1, 2 to 1,
                2 to 2, 3 to 2
            )
        )
    )

    fun getLevel(levelNumber: Int): OneLineLevel {
        val index = (levelNumber - 1).coerceIn(0, levels.size - 1)
        return levels[index]
    }

    private fun createLevel(
        levelNumber: Int,
        gridWidth: Int,
        gridHeight: Int,
        path: List<Pair<Int, Int>>
    ): OneLineLevel {
        return OneLineLevel(
            levelNumber = levelNumber,
            gridWidth = gridWidth,
            gridHeight = gridHeight,
            validCells = path.toSet(),
            startCell = path.first(),
            solutionPath = path
        )
    }
}
