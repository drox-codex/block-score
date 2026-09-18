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
