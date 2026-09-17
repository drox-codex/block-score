package com.example.model

import androidx.compose.ui.graphics.Color

/**
 * 3D-styled block color palette with main, highlight, and shadow shades.
 */
enum class BlockColor(
    val id: Int,
    val mainColor: Color,
    val highlightColor: Color,
    val shadowColor: Color
) {
    BLUE(1, Color(0xFF2563EB), Color(0xFF60A5FA), Color(0xFF1D4ED8)),
    GREEN(2, Color(0xFF10B981), Color(0xFF34D399), Color(0xFF047857)),
    ORANGE(3, Color(0xFFF97316), Color(0xFFFB923C), Color(0xFFC2410C)),
    YELLOW(4, Color(0xFFEAB308), Color(0xFFFDE047), Color(0xFFCA8A04)),
    PURPLE(5, Color(0xFF8B5CF6), Color(0xFFA78BFA), Color(0xFF6D28D9)),
    CYAN(6, Color(0xFF06B6D4), Color(0xFF22D3EE), Color(0xFF0E7490)),
    RED(7, Color(0xFFEF4444), Color(0xFFF87171), Color(0xFFB91C1C)),
    PINK(8, Color(0xFFEC4899), Color(0xFFF472B6), Color(0xFFBE185D));

    companion object {
        fun fromId(id: Int): BlockColor = entries.find { it.id == id } ?: BLUE
    }
}

/**
 * Represents a piece that the player can place on the 8x8 grid.
 */
data class BlockPiece(
    val id: String,
    val shapeMatrix: List<List<Boolean>>,
    val color: BlockColor
) {
    val width: Int get() = if (shapeMatrix.isNotEmpty()) shapeMatrix[0].size else 0
    val height: Int get() = shapeMatrix.size
    val totalBlocks: Int get() = shapeMatrix.sumOf { row -> row.count { it } }

    companion object {
        // Shapes registry
        private val SHAPES = listOf(
            // 1x1 Single Dot
            listOf(listOf(true)) to BlockColor.YELLOW,

            // 2x1 & 1x2 Bars
            listOf(listOf(true, true)) to BlockColor.BLUE,
            listOf(listOf(true), listOf(true)) to BlockColor.BLUE,

            // 3x1 & 1x3 Bars
            listOf(listOf(true, true, true)) to BlockColor.CYAN,
            listOf(listOf(true), listOf(true), listOf(true)) to BlockColor.CYAN,

            // 4x1 & 1x4 Bars
            listOf(listOf(true, true, true, true)) to BlockColor.RED,
            listOf(listOf(true), listOf(true), listOf(true), listOf(true)) to BlockColor.RED,

            // 5x1 & 1x5 Bars
            listOf(listOf(true, true, true, true, true)) to BlockColor.PURPLE,
            listOf(listOf(true), listOf(true), listOf(true), listOf(true), listOf(true)) to BlockColor.PURPLE,

            // 2x2 Square
            listOf(
                listOf(true, true),
                listOf(true, true)
            ) to BlockColor.ORANGE,

            // 3x3 Square
            listOf(
                listOf(true, true, true),
                listOf(true, true, true),
                listOf(true, true, true)
            ) to BlockColor.GREEN,

            // Small Corners (2x2)
            listOf(
                listOf(true, true),
                listOf(true, false)
            ) to BlockColor.PINK,
            listOf(
                listOf(true, true),
                listOf(false, true)
            ) to BlockColor.PINK,
            listOf(
                listOf(true, false),
                listOf(true, true)
            ) to BlockColor.PINK,
            listOf(
                listOf(false, true),
                listOf(true, true)
            ) to BlockColor.PINK,

            // L-Shapes (3x2)
            listOf(
                listOf(true, false),
                listOf(true, false),
                listOf(true, true)
            ) to BlockColor.CYAN,
            listOf(
                listOf(false, true),
                listOf(false, true),
                listOf(true, true)
            ) to BlockColor.CYAN,
            listOf(
                listOf(true, true, true),
                listOf(true, false, false)
            ) to BlockColor.CYAN,
            listOf(
                listOf(true, true, true),
                listOf(false, false, true)
            ) to BlockColor.CYAN,

            // T-Shapes (3x2)
            listOf(
                listOf(true, true, true),
                listOf(false, true, false)
            ) to BlockColor.PURPLE,
            listOf(
                listOf(false, true, false),
                listOf(true, true, true)
            ) to BlockColor.PURPLE,
            listOf(
                listOf(true, false),
                listOf(true, true),
                listOf(true, false)
            ) to BlockColor.PURPLE,
            listOf(
                listOf(false, true),
                listOf(true, true),
                listOf(false, true)
            ) to BlockColor.PURPLE,

            // Z & S shapes
            listOf(
                listOf(true, true, false),
                listOf(false, true, true)
            ) to BlockColor.GREEN,
            listOf(
                listOf(false, true, true),
                listOf(true, true, false)
            ) to BlockColor.GREEN,
            listOf(
                listOf(true, false),
                listOf(true, true),
                listOf(false, true)
            ) to BlockColor.GREEN,
            listOf(
                listOf(false, true),
                listOf(true, true),
                listOf(true, false)
            ) to BlockColor.GREEN,

            // 3x3 Large Corner
            listOf(
                listOf(true, false, false),
                listOf(true, false, false),
                listOf(true, true, true)
            ) to BlockColor.ORANGE,
            listOf(
                listOf(false, false, true),
                listOf(false, false, true),
                listOf(true, true, true)
            ) to BlockColor.ORANGE,
            listOf(
                listOf(true, true, true),
                listOf(true, false, false),
                listOf(true, false, false)
            ) to BlockColor.ORANGE,
            listOf(
                listOf(true, true, true),
                listOf(false, false, true),
                listOf(false, false, true)
            ) to BlockColor.ORANGE
        )

        fun randomPiece(id: String = java.util.UUID.randomUUID().toString()): BlockPiece {
            val (shape, defaultColor) = SHAPES.random()
            // Pick a color: mostly the default, or occasional vibrant match
            val color = if (kotlin.random.Random.nextFloat() > 0.3f) defaultColor else BlockColor.entries.random()
            return BlockPiece(
                id = id,
                shapeMatrix = shape,
                color = color
            )
        }

        fun generatePieceTray(): List<BlockPiece> {
            return listOf(
                randomPiece(),
                randomPiece(),
                randomPiece()
            )
        }
    }
}
