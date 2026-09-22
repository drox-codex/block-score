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
import com.example.model.AppTheme
import com.example.model.DailyChallenge
import com.example.model.SpinReward
import com.example.model.SpinWheelConfig
import kotlinx.coroutines.Job
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
        // Gentle elevation above the finger (~60dp / 120px) so blocks are clearly visible
        const val DRAG_LIFT_OFFSET_PX = 120f
    }

    val preferences = GamePreferences(application)
    val soundManager = SoundManager(application, preferences)

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _dragState = MutableStateFlow(DragState())
    val dragState: StateFlow<DragState> = _dragState.asStateFlow()

    private val _currentTheme = MutableStateFlow(AppTheme.fromId(preferences.currentTheme))
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    // Screen navigation state
    enum class Screen {
        SPLASH,
        MAIN_MENU,
        CLASSIC_GAME,
        ADVENTURE_MAP,
        ADVENTURE_GAME,
        ACHIEVEMENTS,
        SETTINGS,
        ONE_LINE_GAME,
        TIC_TAC_TOE_GAME
    }

    private val _currentScreen = MutableStateFlow(Screen.SPLASH)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Board layout coordinates measured by Compose
    var boardBoundsInRoot: Pair<Offset, Float> = Pair(Offset.Zero, 0f) // (topLeft, sizePx)

    // Zen Mode undo history
    private var previousGameState: GameState? = null

    // Blitz countdown job
    private var blitzJob: Job? = null
    
    // Tic-Tac-Toe State
    private val _ticTacToeState = MutableStateFlow(com.example.model.TicTacToeState())
    val ticTacToeState: StateFlow<com.example.model.TicTacToeState> = _ticTacToeState.asStateFlow()

    // One Line Mode State
    private val _oneLineGameState = MutableStateFlow<com.example.model.OneLineGameState?>(null)
    val oneLineGameState: StateFlow<com.example.model.OneLineGameState?> = _oneLineGameState.asStateFlow()
    var oneLineLevelProgress: Int
        get() = preferences.oneLineLevelProgress
        set(value) { preferences.oneLineLevelProgress = value }

    init {
        startNewClassicGame()
    }

    fun setTheme(theme: AppTheme) {
        preferences.currentTheme = theme.id
        _currentTheme.value = theme
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun startNewClassicGame() {
        blitzJob?.cancel()
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
        previousGameState = null
        resetDrag()
    }

    fun startBlitzGame() {
        blitzJob?.cancel()
        val initialPieces = BlockPiece.generatePieceTray()
        _gameState.value = GameState(
            board = List(8) { List(8) { 0 } },
            trayPieces = initialPieces,
            score = 0,
            bestScore = preferences.blitzBestScore,
            currentCombo = 0,
            gameMode = GameMode.BLITZ,
            blitzTimeRemainingSec = 90,
            isGameOver = false,
            isLevelWon = false
        )
        previousGameState = null
        resetDrag()
        navigateTo(Screen.CLASSIC_GAME)

        // Launch 90s countdown
        blitzJob = viewModelScope.launch {
            while (_gameState.value.blitzTimeRemainingSec > 0 && !_gameState.value.isGameOver) {
                delay(1000)
                val remaining = _gameState.value.blitzTimeRemainingSec - 1
                if (remaining <= 0) {
                    soundManager.playGameOver()
                    _gameState.update { it.copy(blitzTimeRemainingSec = 0, isGameOver = true) }
                    break
                } else {
                    _gameState.update { it.copy(blitzTimeRemainingSec = remaining) }
                }
            }
        }
    }

    fun startZenGame() {
        blitzJob?.cancel()
        val initialPieces = BlockPiece.generatePieceTray()
        _gameState.value = GameState(
            board = List(8) { List(8) { 0 } },
            trayPieces = initialPieces,
            score = 0,
            bestScore = preferences.bestScore,
            currentCombo = 0,
            gameMode = GameMode.ZEN,
            canUndo = false,
            isGameOver = false,
            isLevelWon = false
        )
        previousGameState = null
        resetDrag()
        navigateTo(Screen.CLASSIC_GAME)
    }

    fun startDailyChallenge() {
        blitzJob?.cancel()
        val today = DailyChallenge.getTodayDate()
        val challenge = DailyChallenge.generateForDate(today)
        val initialPieces = BlockPiece.generatePieceTray()

        _gameState.value = GameState(
            board = List(8) { List(8) { 0 } },
            trayPieces = initialPieces,
            score = 0,
            bestScore = challenge.targetScore,
            currentCombo = 0,
            gameMode = GameMode.DAILY,
            dailyTargetLines = challenge.targetLines,
            dailyCurrentLines = 0,
            dailyMovesRemaining = challenge.maxMoves,
            isGameOver = false,
            isLevelWon = false
        )
        previousGameState = null
        resetDrag()
        navigateTo(Screen.CLASSIC_GAME)
    }

    fun undoZenMove() {
        if (_gameState.value.isZen && previousGameState != null) {
            _gameState.value = previousGameState!!.copy(canUndo = false)
            previousGameState = null
            soundManager.playBlockPlace()
        }
    }

    fun spinLuckyWheel(): SpinReward {
        val reward = SpinWheelConfig.REWARDS.random()
        preferences.lastSpinDate = DailyChallenge.getTodayDate()
        when (reward.id) {
            0, 3 -> preferences.hintsCount += reward.rewardValue
            1, 4 -> preferences.updateBestScore(preferences.bestScore + reward.rewardValue)
            2 -> setTheme(AppTheme.AQUA_GLASS)
            5 -> preferences.hintsCount += 2
        }
        return reward
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

    fun startOneLineLevel(levelNum: Int) {
        val level = com.example.model.OneLineLevels.getLevel(levelNum)
        val initialPath = level.startCell?.let { listOf(it) } ?: emptyList()
        _oneLineGameState.value = com.example.model.OneLineGameState(
            currentLevel = level,
            path = initialPath,
            isLevelComplete = false
        )
        navigateTo(Screen.ONE_LINE_GAME)
    }

    fun onOneLineCellTouched(row: Int, col: Int) {
        val state = _oneLineGameState.value ?: return
        if (state.isLevelComplete) return

        val pos = Pair(row, col)
        if (!state.currentLevel.validCells.contains(pos)) return

        var newPath = state.path.toMutableList()

        if (newPath.isEmpty()) {
            if (state.currentLevel.startCell == null || pos == state.currentLevel.startCell) {
                newPath.add(pos)
                soundManager.playBlockPlace()
            }
        } else {
            val current = newPath.last()
            if (pos == current) return

            val index = newPath.indexOf(pos)
            if (index != -1 && index < newPath.size - 1) {
                // Allow player to backtrack by dragging back along the path
                newPath = newPath.subList(0, index + 1).toMutableList()
                soundManager.playBlockPlace()
            } else if (index == -1) {
                // Check if directly adjacent
                val isAdjacent = (kotlin.math.abs(pos.first - current.first) == 1 && pos.second == current.second) ||
                                 (kotlin.math.abs(pos.second - current.second) == 1 && pos.first == current.first)

                if (isAdjacent) {
                    newPath.add(pos)
                    soundManager.playBlockPlace()
                } else {
                    // Fast swipe jumped over 1 cell: check intermediate orthogonal steps
                    val rDiff = pos.first - current.first
                    val cDiff = pos.second - current.second
                    if (kotlin.math.abs(rDiff) + kotlin.math.abs(cDiff) == 2) {
                        val mid1 = Pair(current.first + rDiff.compareTo(0), current.second)
                        val mid2 = Pair(current.first, current.second + cDiff.compareTo(0))
                        val mid = when {
                            state.currentLevel.validCells.contains(mid1) && !newPath.contains(mid1) -> mid1
                            state.currentLevel.validCells.contains(mid2) && !newPath.contains(mid2) -> mid2
                            else -> null
                        }
                        if (mid != null) {
                            newPath.add(mid)
                            newPath.add(pos)
                            soundManager.playBlockPlace()
                        }
                    }
                }
            }
        }

        val isComplete = newPath.size == state.currentLevel.validCells.size
        if (isComplete) {
            soundManager.playLevelComplete()
            if (state.currentLevel.levelNumber >= oneLineLevelProgress) {
                oneLineLevelProgress = state.currentLevel.levelNumber + 1
            }
            
            // Spawn celebration particles
            val newParticles = mutableListOf<com.example.model.Particle>()
            val (boardTopLeft, boardSize) = boardBoundsInRoot
            val cx = if (boardSize > 0) boardTopLeft.x + (boardSize / 2f) else 500f
            val cy = if (boardSize > 0) boardTopLeft.y + (boardSize / 2f) else 1000f
            for (i in 0 until 50) {
                spawnCellParticles(
                    cx = cx + (kotlin.random.Random.nextFloat() - 0.5f) * 600f, 
                    cy = cy + (kotlin.random.Random.nextFloat() - 0.5f) * 600f, 
                    color = androidx.compose.ui.graphics.Color(0xFFFBBF24), 
                    list = newParticles
                )
                spawnCellParticles(
                    cx = cx + (kotlin.random.Random.nextFloat() - 0.5f) * 600f, 
                    cy = cy + (kotlin.random.Random.nextFloat() - 0.5f) * 600f, 
                    color = androidx.compose.ui.graphics.Color(0xFF10B981), 
                    list = newParticles
                )
            }
            _gameState.value = _gameState.value.copy(particles = newParticles)
            viewModelScope.launch { 
                kotlinx.coroutines.delay(1000)
                _gameState.value = _gameState.value.copy(particles = emptyList()) 
            }
        } else {
            // Check for dead end: if head has no valid unvisited neighbors, user is stuck
            if (newPath.isNotEmpty() && isOneLineDeadEnd(newPath.last(), newPath, state.currentLevel.validCells)) {
                soundManager.playGameOver()
                newPath = state.currentLevel.startCell?.let { mutableListOf(it) } ?: mutableListOf()
            }
        }

        _oneLineGameState.value = state.copy(
            path = newPath,
            isLevelComplete = isComplete
        )
    }

    private fun isOneLineDeadEnd(
        head: Pair<Int, Int>,
        currentPath: List<Pair<Int, Int>>,
        validCells: Set<Pair<Int, Int>>
    ): Boolean {
        if (currentPath.size == validCells.size) return false
        val neighbors = listOf(
            Pair(head.first - 1, head.second),
            Pair(head.first + 1, head.second),
            Pair(head.first, head.second - 1),
            Pair(head.first, head.second + 1)
        )
        return neighbors.none { validCells.contains(it) && !currentPath.contains(it) }
    }

    fun onOneLineDragEnded() {
        // Do not reset valid user progress when lifting finger.
        // Mistakes (dead ends) are already detected and reset immediately in onOneLineCellTouched.
    }

    fun getOneLineHint() {
        val state = _oneLineGameState.value ?: return
        if (state.isLevelComplete) return
        val solution = state.currentLevel.solutionPath
        if (solution.isEmpty()) return

        val currentPath = state.path
        var matchCount = 0
        while (matchCount < currentPath.size && matchCount < solution.size && currentPath[matchCount] == solution[matchCount]) {
            matchCount++
        }

        val newPath = if (matchCount < currentPath.size) {
            // Player deviated from valid solution, trim back to the matching prefix + 1 step forward
            solution.take(matchCount + 1).toMutableList()
        } else {
            // Player is following solution, advance 1 step
            solution.take(kotlin.math.min(matchCount + 1, solution.size)).toMutableList()
        }

        soundManager.playBlockPlace()
        val isComplete = newPath.size == state.currentLevel.validCells.size
        if (isComplete) {
            soundManager.playLevelComplete()
            if (state.currentLevel.levelNumber >= oneLineLevelProgress) {
                oneLineLevelProgress = state.currentLevel.levelNumber + 1
            }
        }

        _oneLineGameState.value = state.copy(
            path = newPath,
            isLevelComplete = isComplete
        )
    }

    fun restartOneLineLevel() {
        _oneLineGameState.value?.let { state ->
            startOneLineLevel(state.currentLevel.levelNumber)
        }
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
        if (_gameState.value.isZen) {
            previousGameState = _gameState.value.copy(canUndo = true)
        }

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
        val rawMovePoints = (placementPoints + linePoints + comboBonus) * (if (_gameState.value.isBlitz) 2 else 1)
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
        var updatedTray = _gameState.value.trayPieces.toMutableList()
        updatedTray[slotIndex] = null
        if (updatedTray.all { it == null }) {
            updatedTray.clear()
            updatedTray.addAll(BlockPiece.generatePieceTray())
        }

        // In Zen mode, if no pieces fit, peacefully provide fresh pieces
        if (_gameState.value.isZen && updatedTray.filterNotNull().none { canPieceFitAnywhere(it, nextBoard) }) {
            updatedTray = BlockPiece.generatePieceTray().toMutableList()
        }

        val newScore = ScoreValidator.sanitizeScore(_gameState.value.score + totalMovePoints)
        val newBestScore = if (newScore > _gameState.value.bestScore) newScore else _gameState.value.bestScore

        // Particles & Floating Scores
        val newParticles = mutableListOf<Particle>()
        val newFloatingTexts = mutableListOf<FloatingText>()

        if (linesCleared > 0) {
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
        } else if (_gameState.value.isBlitz) {
            if (newScore > preferences.blitzBestScore) {
                preferences.blitzBestScore = newScore
            }
        }

        // Check Adventure & Daily mode objectives
        var isLevelWon = false
        var earnedStars = 0
        var adventureProgress = _gameState.value.adventureProgress
        var dailyCurrent = _gameState.value.dailyCurrentLines
        var dailyRemainingMoves = _gameState.value.dailyMovesRemaining

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
        } else if (_gameState.value.isDaily) {
            dailyCurrent += linesCleared
            dailyRemainingMoves = (dailyRemainingMoves - 1).coerceAtLeast(0)
            if (dailyCurrent >= _gameState.value.dailyTargetLines) {
                isLevelWon = true
                val today = DailyChallenge.getTodayDate()
                if (preferences.lastDailyCompletedDate != today) {
                    preferences.lastDailyCompletedDate = today
                    preferences.dailyStreak += 1
                }
                soundManager.playLevelComplete()
            }
        }

        // Check Game Over
        var isGameOver = false
        if (!_gameState.value.isZen && !isLevelWon) {
            val noPiecesFit = updatedTray.filterNotNull().none { canPieceFitAnywhere(it, nextBoard) }
            val dailyOutOfMoves = _gameState.value.isDaily && dailyRemainingMoves <= 0 && !isLevelWon
            isGameOver = noPiecesFit || dailyOutOfMoves
        }

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
            dailyCurrentLines = dailyCurrent,
            dailyMovesRemaining = dailyRemainingMoves,
            canUndo = _gameState.value.isZen && previousGameState != null,
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
        startOneLineLevel(1)
    }

    fun startTicTacToeGame(difficulty: com.example.model.TicTacToeDifficulty) {
        _ticTacToeState.value = com.example.model.TicTacToeState(
            difficulty = difficulty
        )
        navigateTo(Screen.TIC_TAC_TOE_GAME)
    }

    fun onTicTacToeCellClicked(index: Int) {
        val state = _ticTacToeState.value
        if (state.isGameOver || state.board[index] != com.example.model.TicTacToePlayer.NONE || state.currentPlayer != com.example.model.TicTacToePlayer.X) return

        val newBoard = state.board.toMutableList()
        newBoard[index] = com.example.model.TicTacToePlayer.X
        soundManager.playBlockPlace()

        updateTicTacToeState(newBoard)

        if (!_ticTacToeState.value.isGameOver) {
            viewModelScope.launch {
                kotlinx.coroutines.delay(500)
                makeTicTacToeAIMove()
            }
        }
    }

    private fun makeTicTacToeAIMove() {
        val state = _ticTacToeState.value
        if (state.isGameOver) return

        val emptyIndices = state.board.indices.filter { state.board[it] == com.example.model.TicTacToePlayer.NONE }
        if (emptyIndices.isEmpty()) return

        val move = when (state.difficulty) {
            com.example.model.TicTacToeDifficulty.EASY -> emptyIndices.random()
            com.example.model.TicTacToeDifficulty.MEDIUM -> {
                if (kotlin.random.Random.nextFloat() > 0.5f) {
                    findBestTicTacToeMove(state.board, com.example.model.TicTacToePlayer.O)
                } else {
                    emptyIndices.random()
                }
            }
            com.example.model.TicTacToeDifficulty.HARD -> findBestTicTacToeMove(state.board, com.example.model.TicTacToePlayer.O)
        }

        val newBoard = state.board.toMutableList()
        newBoard[move] = com.example.model.TicTacToePlayer.O
        soundManager.playBlockPlace()

        updateTicTacToeState(newBoard)
    }

    private fun updateTicTacToeState(board: List<com.example.model.TicTacToePlayer>) {
        val winner = checkTicTacToeWinner(board)
        val isDraw = winner == null && !board.contains(com.example.model.TicTacToePlayer.NONE)
        val isGameOver = winner != null || isDraw

        if (isGameOver) {
            if (winner != null) soundManager.playLevelComplete() else soundManager.playGameOver()
        }

        _ticTacToeState.value = _ticTacToeState.value.copy(
            board = board,
            winner = winner,
            isDraw = isDraw,
            isGameOver = isGameOver,
            currentPlayer = if (isGameOver) com.example.model.TicTacToePlayer.NONE else if (_ticTacToeState.value.currentPlayer == com.example.model.TicTacToePlayer.X) com.example.model.TicTacToePlayer.O else com.example.model.TicTacToePlayer.X
        )
    }

    private fun checkTicTacToeWinner(board: List<com.example.model.TicTacToePlayer>): com.example.model.TicTacToePlayer? {
        val winPatterns = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Rows
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Cols
            listOf(0, 4, 8), listOf(2, 4, 6)                   // Diagonals
        )
        for (pattern in winPatterns) {
            if (board[pattern[0]] != com.example.model.TicTacToePlayer.NONE &&
                board[pattern[0]] == board[pattern[1]] &&
                board[pattern[1]] == board[pattern[2]]) {
                return board[pattern[0]]
            }
        }
        return null
    }

    private fun findBestTicTacToeMove(board: List<com.example.model.TicTacToePlayer>, player: com.example.model.TicTacToePlayer): Int {
        var bestVal = if (player == com.example.model.TicTacToePlayer.O) Int.MIN_VALUE else Int.MAX_VALUE
        var bestMove = -1

        for (i in board.indices) {
            if (board[i] == com.example.model.TicTacToePlayer.NONE) {
                val newBoard = board.toMutableList()
                newBoard[i] = player
                val moveVal = minimax(newBoard, 0, false)
                if (player == com.example.model.TicTacToePlayer.O) {
                    if (moveVal > bestVal) {
                        bestMove = i
                        bestVal = moveVal
                    }
                } else {
                    if (moveVal < bestVal) {
                        bestMove = i
                        bestVal = moveVal
                    }
                }
            }
        }
        return bestMove
    }

    private fun minimax(board: MutableList<com.example.model.TicTacToePlayer>, depth: Int, isMax: Boolean): Int {
        val winner = checkTicTacToeWinner(board)
        if (winner == com.example.model.TicTacToePlayer.O) return 10 - depth
        if (winner == com.example.model.TicTacToePlayer.X) return -10 + depth
        if (!board.contains(com.example.model.TicTacToePlayer.NONE)) return 0

        if (isMax) {
            var best = Int.MIN_VALUE
            for (i in board.indices) {
                if (board[i] == com.example.model.TicTacToePlayer.NONE) {
                    board[i] = com.example.model.TicTacToePlayer.O
                    best = kotlin.math.max(best, minimax(board, depth + 1, !isMax))
                    board[i] = com.example.model.TicTacToePlayer.NONE
                }
            }
            return best
        } else {
            var best = Int.MAX_VALUE
            for (i in board.indices) {
                if (board[i] == com.example.model.TicTacToePlayer.NONE) {
                    board[i] = com.example.model.TicTacToePlayer.X
                    best = kotlin.math.min(best, minimax(board, depth + 1, !isMax))
                    board[i] = com.example.model.TicTacToePlayer.NONE
                }
            }
            return best
        }
    }
}
