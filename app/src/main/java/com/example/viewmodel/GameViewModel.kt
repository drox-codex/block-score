package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.GamePreferences
import com.example.model.Achievement
import com.example.model.AdventureLevel
import com.example.model.BlockPiece
import com.example.model.FloatingText
import com.example.model.GameMode
import com.example.model.GameState
import com.example.model.ObjectiveType
import com.example.model.Particle
import com.example.security.ScoreValidator
import com.example.security.SecurityUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class DragState(
    val isDragging: Boolean = false,
    val piece: BlockPiece? = null,
    val slotIndex: Int? = null,
    val touchPosition: Offset = Offset.Zero,
    val hoverRow: Int? = null,
    val hoverCol: Int? = null,
    val isValid: Boolean = false
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        // Gentle elevation above the finger (~36dp / 72px) so blocks are clearly visible
        const val DRAG_LIFT_OFFSET_PX = 72f
    }

    val preferences = GamePreferences(application)
    val soundManager = SoundManager(application, preferences)

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _dragState = MutableStateFlow(DragState())
    val dragState: StateFlow<DragState> = _dragState.asStateFlow()

    // Screen navigation state
    enum class Screen {
        SPLASH,
        MAIN_MENU,
        CLASSIC_GAME,
        ADVENTURE_MAP,
        ADVENTURE_GAME,
        ACHIEVEMENTS,
        SETTINGS
    }

    private val _currentScreen = MutableStateFlow(Screen.SPLASH)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Board layout coordinates measured by Compose
    var boardBoundsInRoot: Pair<Offset, Float> = Pair(Offset.Zero, 0f) // (topLeft, sizePx)

    init {
        startNewClassicGame()
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun startNewClassicGame() {
        val initialPieces = BlockPiece.generatePieceTray()
        _gameState.value = GameState(
            board = List(8) { List(8) { 0 } },
            trayPieces = initialPieces,
            score = 0,
            bestScore = preferences.bestScore,
            currentCombo = 0,
            gameMode = GameMode.CLASSIC,
            isGameOver = false,
            isLevelWon = false
        )
        resetDrag()
    }

    fun startAdventureLevel(levelNum: Int) {
        val safeLevel = ScoreValidator.sanitizeLevel(levelNum)
        val initialPieces = BlockPiece.generatePieceTray()
        _gameState.value = GameState(
            board = List(8) { List(8) { 0 } },
            trayPieces = initialPieces,
            score = 0,
            bestScore = preferences.bestScore,
            currentCombo = 0,
            gameMode = GameMode.ADVENTURE,
            currentAdventureLevel = safeLevel,
            adventureProgress = 0,
            adventureStars = 0,
            isGameOver = false,
            isLevelWon = false
        )
        resetDrag()
        navigateTo(Screen.ADVENTURE_GAME)
    }

    fun restartCurrentGame() {
        if (_gameState.value.isAdventure) {
            startAdventureLevel(_gameState.value.currentAdventureLevel)
        } else {
            startNewClassicGame()
        }
    }

    fun onDragStart(slotIndex: Int, initialTouch: Offset) {
        val piece = _gameState.value.trayPieces.getOrNull(slotIndex) ?: return
        _dragState.value = DragState(
            isDragging = true,
            piece = piece,
            slotIndex = slotIndex,
            touchPosition = initialTouch,
            hoverRow = null,
            hoverCol = null,
            isValid = false
        )
    }

    fun onDragMove(touchPos: Offset) {
        val piece = _dragState.value.piece ?: return
        val (boardTopLeft, boardSize) = boardBoundsInRoot

        // Target position is gently lifted above the finger so blocks are clearly visible
        val targetPos = Offset(touchPos.x, touchPos.y - DRAG_LIFT_OFFSET_PX)

        if (boardSize > 0f) {
            val cellSize = boardSize / 8f
            val relX = targetPos.x - boardTopLeft.x
            val relY = targetPos.y - boardTopLeft.y

            // Center piece directly on finger position
            val pieceWidthPx = piece.width * cellSize
            val pieceHeightPx = piece.height * cellSize
            val originX = relX - (pieceWidthPx / 2f) + (cellSize / 2f)
            val originY = relY - (pieceHeightPx / 2f) + (cellSize / 2f)

            val col = kotlin.math.round(originX / cellSize).toInt()
            val row = kotlin.math.round(originY / cellSize).toInt()

            val valid = canPlaceAt(piece, row, col, _gameState.value.board)
            _dragState.value = _dragState.value.copy(
                touchPosition = touchPos,
                hoverRow = if (valid) row else null,
                hoverCol = if (valid) col else null,
                isValid = valid
            )
        } else {
            _dragState.value = _dragState.value.copy(touchPosition = touchPos)
        }
    }

    fun onDragEnd() {
        val state = _dragState.value
        if (state.isDragging && state.isValid && state.piece != null && state.hoverRow != null && state.hoverCol != null && state.slotIndex != null) {
            placePiece(state.piece, state.hoverRow, state.hoverCol, state.slotIndex)
        }
        resetDrag()
    }

    fun onDragCancel() {
        resetDrag()
    }

    private fun resetDrag() {
        _dragState.value = DragState()
    }

    private fun canPlaceAt(piece: BlockPiece, startRow: Int, startCol: Int, board: List<List<Int>>): Boolean {
        if (startRow < 0 || startCol < 0) return false
        if (startRow + piece.height > 8 || startCol + piece.width > 8) return false

        for (r in 0 until piece.height) {
            for (c in 0 until piece.width) {
                if (piece.shapeMatrix[r][c]) {
                    if (board[startRow + r][startCol + c] != 0) {
                        return false
                    }
                }
            }
        }
        return true
    }

    private fun canPieceFitAnywhere(piece: BlockPiece, board: List<List<Int>>): Boolean {
        for (r in 0..(8 - piece.height)) {
            for (c in 0..(8 - piece.width)) {
                if (canPlaceAt(piece, r, c, board)) {
                    return true
                }
            }
        }
        return false
    }

    private fun placePiece(piece: BlockPiece, startRow: Int, startCol: Int, slotIndex: Int) {
        val currentBoard = _gameState.value.board.map { it.toMutableList() }
        for (r in 0 until piece.height) {
            for (c in 0 until piece.width) {
                if (piece.shapeMatrix[r][c]) {
                    currentBoard[startRow + r][startCol + c] = piece.color.id
                }
            }
        }

        // Calculate placement points
        val placedTiles = piece.totalBlocks
        val placementPoints = placedTiles * 10

        // Find full rows and columns
        val fullRows = mutableSetOf<Int>()
        val fullCols = mutableSetOf<Int>()

        for (r in 0 until 8) {
            if (currentBoard[r].all { it != 0 }) {
                fullRows.add(r)
            }
        }
        for (c in 0 until 8) {
            if ((0 until 8).all { r -> currentBoard[r][c] != 0 }) {
                fullCols.add(c)
            }
        }

        val linesCleared = fullRows.size + fullCols.size
        var newCombo = if (linesCleared > 0) _gameState.value.currentCombo + 1 else 0

        // Points for lines
        val linePoints = when (linesCleared) {
            0 -> 0
            1 -> 100
            2 -> 300
            3 -> 600
            4 -> 1000
            5 -> 1500
            else -> linesCleared * 350
        }
        val comboBonus = if (newCombo > 1) (newCombo - 1) * 100 else 0
        val rawMovePoints = placementPoints + linePoints + comboBonus
        val validMove = ScoreValidator.validateMoveScore(placedTiles, linesCleared, newCombo, rawMovePoints)
        val totalMovePoints = if (validMove) rawMovePoints else 0
        newCombo = ScoreValidator.sanitizeCombo(newCombo)

        // Clear cells on board
        val nextBoard = currentBoard.mapIndexed { r, row ->
            row.mapIndexed { c, cell ->
                if (r in fullRows || c in fullCols) 0 else cell
            }
        }

        // Update piece tray
        val updatedTray = _gameState.value.trayPieces.toMutableList()
        updatedTray[slotIndex] = null
        if (updatedTray.all { it == null }) {
            updatedTray.clear()
            updatedTray.addAll(BlockPiece.generatePieceTray())
        }

        val newScore = ScoreValidator.sanitizeScore(_gameState.value.score + totalMovePoints)
        val newBestScore = if (newScore > _gameState.value.bestScore) newScore else _gameState.value.bestScore

        // Particles & Floating Scores
        val newParticles = mutableListOf<Particle>()
        val newFloatingTexts = mutableListOf<FloatingText>()

        if (linesCleared > 0) {
            // Generate sparkle particles at line clear centers
            val (boardTopLeft, boardSize) = boardBoundsInRoot
            val cellSize = if (boardSize > 0) boardSize / 8f else 50f

            for (r in fullRows) {
                for (c in 0 until 8) {
                    val px = boardTopLeft.x + (c + 0.5f) * cellSize
                    val py = boardTopLeft.y + (r + 0.5f) * cellSize
                    spawnCellParticles(px, py, piece.color.mainColor, newParticles)
                }
            }
            for (c in fullCols) {
                for (r in 0 until 8) {
                    val px = boardTopLeft.x + (c + 0.5f) * cellSize
                    val py = boardTopLeft.y + (r + 0.5f) * cellSize
                    spawnCellParticles(px, py, piece.color.mainColor, newParticles)
                }
            }

            // Spawn floating score
            val text = if (newCombo > 1) "+$totalMovePoints  COMBO x$newCombo!" else "+$totalMovePoints"
            val textX = boardTopLeft.x + boardSize / 2f
            val textY = boardTopLeft.y + boardSize / 2f
            newFloatingTexts.add(
                FloatingText(
                    id = System.currentTimeMillis(),
                    text = text,
                    x = textX,
                    y = textY,
                    color = if (newCombo > 1) Color(0xFFFBBF24) else Color.White
                )
            )

            // Audio & haptics
            soundManager.playLineClear(linesCleared)
            if (newCombo > 1) {
                soundManager.playCombo(newCombo)
            }
        } else {
            soundManager.playBlockPlace()
        }

        // Stats tracking
        preferences.addStats(linesCleared, placedTiles, newCombo)
        if (_gameState.value.isClassic) {
            preferences.updateBestScore(newScore)
        }

        // Check Adventure mode objective
        var isLevelWon = false
        var earnedStars = 0
        var adventureProgress = _gameState.value.adventureProgress

        if (_gameState.value.isAdventure) {
            val level = AdventureLevel.getLevel(_gameState.value.currentAdventureLevel)
            when (level.objectiveType) {
                ObjectiveType.SCORE -> {
                    adventureProgress = newScore
                    if (adventureProgress >= level.targetValue) isLevelWon = true
                }
                ObjectiveType.LINES -> {
                    adventureProgress += linesCleared
                    if (adventureProgress >= level.targetValue) isLevelWon = true
                }
                ObjectiveType.COMBO -> {
                    if (newCombo > adventureProgress) adventureProgress = newCombo
                    if (adventureProgress >= level.targetValue) isLevelWon = true
                }
            }

            if (isLevelWon) {
                earnedStars = when {
                    adventureProgress >= level.threeStarTarget -> 3
                    adventureProgress >= level.twoStarTarget -> 2
                    else -> 1
                }
                preferences.saveLevelStars(level.levelNumber, earnedStars)
                preferences.unlockNextLevel(level.levelNumber)
                soundManager.playLevelComplete()
            }
        }

        // Check Game Over: can ANY remaining tray piece fit?
        val isGameOver = !isLevelWon && updatedTray.filterNotNull().none { canPieceFitAnywhere(it, nextBoard) }

        if (isGameOver) {
            soundManager.playGameOver()
        }

        _gameState.value = _gameState.value.copy(
            board = nextBoard,
            trayPieces = updatedTray,
            score = newScore,
            bestScore = newBestScore,
            currentCombo = newCombo,
            highestCombo = maxOf(_gameState.value.highestCombo, newCombo),
            totalLinesCleared = _gameState.value.totalLinesCleared + linesCleared,
            totalBlocksPlaced = _gameState.value.totalBlocksPlaced + placedTiles,
            isGameOver = isGameOver,
            isLevelWon = isLevelWon,
            adventureProgress = adventureProgress,
            adventureStars = earnedStars,
            particles = newParticles,
            floatingTexts = newFloatingTexts,
            lastMoveClearedLines = linesCleared
        )

        // Clear particles after animation
        if (newParticles.isNotEmpty() || newFloatingTexts.isNotEmpty()) {
            viewModelScope.launch {
                delay(700)
                _gameState.update { it.copy(particles = emptyList(), floatingTexts = emptyList()) }
            }
        }
    }

    private fun spawnCellParticles(cx: Float, cy: Float, color: Color, list: MutableList<Particle>) {
        for (i in 0 until 4) {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 8f + 3f
            val vx = kotlin.math.cos(angle) * speed
            val vy = kotlin.math.sin(angle) * speed
            list.add(
                Particle(
                    id = System.nanoTime() + i,
                    x = cx,
                    y = cy,
                    vx = vx,
                    vy = vy,
                    color = color,
                    size = Random.nextFloat() * 8f + 6f
                )
            )
        }
    }

    fun getAchievements(): List<Achievement> {
        return listOf(
            Achievement(
                id = "score_master",
                title = "Score Master",
                description = "Reach a score of 2,500 in Classic",
                currentProgress = preferences.bestScore,
                targetProgress = 2500,
                iconName = "trophy"
            ),
            Achievement(
                id = "combo_master",
                title = "Combo Master",
                description = "Achieve a 4x or higher combo",
                currentProgress = preferences.maxCombo,
                targetProgress = 4,
                iconName = "bolt"
            ),
            Achievement(
                id = "line_breaker",
                title = "Line Breaker",
                description = "Clear 100 total rows & columns",
                currentProgress = preferences.totalLines,
                targetProgress = 100,
                iconName = "clear"
            ),
            Achievement(
                id = "block_expert",
                title = "Block Expert",
                description = "Place 500 puzzle blocks on the board",
                currentProgress = preferences.totalBlocks,
                targetProgress = 500,
                iconName = "cube"
            ),
            Achievement(
                id = "perfect_start",
                title = "Multi-Clear",
                description = "Clear 2 or more lines simultaneously",
                currentProgress = if (preferences.totalLines >= 2) 1 else 0,
                targetProgress = 1,
                iconName = "star"
            ),
            Achievement(
                id = "adventure_hero",
                title = "Adventure Hero",
                description = "Complete 10 Adventure levels",
                currentProgress = (preferences.unlockedLevel - 1).coerceAtLeast(0),
                targetProgress = 10,
                iconName = "map"
            )
        )
    }

    fun toggleSound() {
        preferences.soundEnabled = !preferences.soundEnabled
    }

    fun toggleMusic() {
        preferences.musicEnabled = !preferences.musicEnabled
        soundManager.updateAmbientMusic()
    }

    fun toggleVibration() {
        preferences.vibrationEnabled = !preferences.vibrationEnabled
    }

    fun resetProgress() {
        preferences.resetAll()
        _gameState.value = GameState(bestScore = 0)
        soundManager.updateAmbientMusic()
    }
}
