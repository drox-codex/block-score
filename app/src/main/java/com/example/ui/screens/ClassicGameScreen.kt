package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.GameState
import com.example.ui.components.BoardView
import com.example.ui.components.FloatingTextOverlay
import com.example.ui.components.ParticleOverlay
import com.example.ui.components.PieceView
import com.example.ui.components.TrayView
import com.example.ui.theme.IosCircularButton
import com.example.ui.theme.IosFloatingComboBadge
import com.example.ui.theme.IosGlassCard
import com.example.ui.theme.IosWaterGlassCard
import com.example.ui.theme.iosPressEffect
import com.example.viewmodel.GameViewModel
import kotlin.math.roundToInt

@Composable
fun ClassicGameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.gameState.collectAsState()
    val dragState by viewModel.dragState.collectAsState()
    val currentTheme by viewModel.currentTheme.collectAsState()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = currentTheme.backgroundGradient
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        val screenWidthPx = constraints.maxWidth.toFloat()
        val screenHeightPx = constraints.maxHeight.toFloat()
        val density = androidx.compose.ui.platform.LocalDensity.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP BAR (iOS style)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home/Back button
                IosCircularButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Menu",
                    onClick = { viewModel.navigateTo(GameViewModel.Screen.MAIN_MENU) },
                    modifier = Modifier.testTag("back_to_menu_button")
                )

                // MODE SPECIFIC HEADER
                when {
                    gameState.isBlitz -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val mins = gameState.blitzTimeRemainingSec / 60
                            val secs = gameState.blitzTimeRemainingSec % 60
                            val timeStr = String.format("%02d:%02d", mins, secs)

                            // Blitz Timer Pill
                            IosGlassCard(
                                shape = RoundedCornerShape(16.dp),
                                backgroundColor = Color(0xFF831843).copy(alpha = 0.85f),
                                borderColor = Color(0xFFF43F5E).copy(alpha = 0.45f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = "Time",
                                        tint = Color(0xFFF43F5E),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = timeStr,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }

                            // Blitz Score
                            IosGlassCard(
                                shape = RoundedCornerShape(16.dp),
                                backgroundColor = currentTheme.glassBackground,
                                borderColor = currentTheme.primaryAccent.copy(alpha = 0.35f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "SCORE (2x)",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFEC4899)
                                    )
                                    Text(
                                        text = "${gameState.score}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                    gameState.isZen -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IosWaterGlassCard(
                                shape = RoundedCornerShape(16.dp),
                                accentColor = Color(0xFF06B6D4),
                                backgroundColor = currentTheme.glassBackground
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = Color(0xFF06B6D4),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ZEN • ${gameState.score}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                }
                            }

                            // Zen Undo Button
                            IosCircularButton(
                                icon = Icons.AutoMirrored.Filled.Undo,
                                contentDescription = "Undo Move",
                                iconTint = if (gameState.canUndo) Color(0xFF38BDF8) else Color(0xFF64748B),
                                onClick = { viewModel.undoZenMove() },
                                modifier = Modifier.testTag("zen_undo_button")
                            )
                        }
                    }
                    gameState.isDaily -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IosGlassCard(
                                shape = RoundedCornerShape(16.dp),
                                backgroundColor = currentTheme.glassBackground,
                                borderColor = Color(0xFFF59E0B).copy(alpha = 0.40f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "LINES GOAL",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFBBF24)
                                    )
                                    Text(
                                        text = "${gameState.dailyCurrentLines} / ${gameState.dailyTargetLines}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }

                            IosGlassCard(
                                shape = RoundedCornerShape(16.dp),
                                backgroundColor = currentTheme.glassBackground,
                                borderColor = Color(0xFF38BDF8).copy(alpha = 0.40f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "MOVES LEFT",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Text(
                                        text = "${gameState.dailyMovesRemaining}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (gameState.dailyMovesRemaining <= 5) Color(0xFFEF4444) else Color.White
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        // Standard Classic Mode Scores
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IosGlassCard(
                                shape = RoundedCornerShape(16.dp),
                                backgroundColor = currentTheme.glassBackground,
                                borderColor = currentTheme.primaryAccent.copy(alpha = 0.35f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "SCORE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF94A3B8),
                                        letterSpacing = 0.8.sp
                                    )
                                    Text(
                                        text = "${gameState.score}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }

                            IosGlassCard(
                                shape = RoundedCornerShape(16.dp),
                                backgroundColor = currentTheme.glassBackground,
                                borderColor = Color(0xFFFBBF24).copy(alpha = 0.35f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.EmojiEvents,
                                            contentDescription = null,
                                            tint = Color(0xFFFBBF24),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "BEST",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF94A3B8),
                                            letterSpacing = 0.8.sp
                                        )
                                    }
                                    Text(
                                        text = "${gameState.bestScore}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFFBBF24)
                                    )
                                }
                            }
                        }
                    }
                }

                // Actions (Settings & Restart)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IosCircularButton(
                        icon = Icons.Default.Settings,
                        contentDescription = "Settings",
                        onClick = { viewModel.navigateTo(GameViewModel.Screen.SETTINGS) },
                        modifier = Modifier.testTag("settings_button")
                    )

                    IosCircularButton(
                        icon = Icons.Default.Refresh,
                        contentDescription = "Restart Game",
                        onClick = {
                            when {
                                gameState.isBlitz -> viewModel.startBlitzGame()
                                gameState.isZen -> viewModel.startZenGame()
                                gameState.isDaily -> viewModel.startDailyChallenge()
                                else -> viewModel.startNewClassicGame()
                            }
                        },
                        modifier = Modifier.testTag("restart_game_button")
                    )
                }
            }

            // COMBO POPUP BADGE (Animated pop-in when combo >= 2)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp),
                contentAlignment = Alignment.Center
            ) {
                IosFloatingComboBadge(combo = gameState.currentCombo)
            }

            // BOARD VIEW
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                contentAlignment = Alignment.Center
            ) {
                BoardView(
                    board = gameState.board,
                    dragState = dragState,
                    onBoardPositioned = { topLeft, size ->
                        viewModel.boardBoundsInRoot = Pair(topLeft, size)
                    }
                )
            }

            // TRAY VIEW
            TrayView(
                pieces = gameState.trayPieces,
                dragState = dragState,
                onDragStart = { idx, pos -> viewModel.onDragStart(idx, pos) },
                onDragMove = { pos -> viewModel.onDragMove(pos) },
                onDragEnd = { viewModel.onDragEnd() },
                onDragCancel = { viewModel.onDragCancel() }
            )
        }

        // Particle Overlay
        ParticleOverlay(particles = gameState.particles)

        // Floating Text Overlay
        FloatingTextOverlay(floatingTexts = gameState.floatingTexts)

        // Dragged piece following finger directly with boundary clamping
        if (dragState.isDragging && dragState.piece != null) {
            val piece = dragState.piece!!
            val cellSizeDp = 34.dp
            val cellSizePx = with(density) { cellSizeDp.toPx() }
            val pieceWidthPx = piece.width * cellSizePx
            val pieceHeightPx = piece.height * cellSizePx

            val liftOffset = GameViewModel.DRAG_LIFT_OFFSET_PX
            val targetX = dragState.touchPosition.x - (pieceWidthPx / 2f)
            val targetY = dragState.touchPosition.y - liftOffset - (pieceHeightPx / 2f)

            val clampedX = targetX.coerceIn(0f, (screenWidthPx - pieceWidthPx).coerceAtLeast(0f))
            val clampedY = targetY.coerceIn(0f, (screenHeightPx - pieceHeightPx).coerceAtLeast(0f))

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(clampedX.roundToInt(), clampedY.roundToInt())
                    }
            ) {
                PieceView(
                    piece = piece,
                    cellSize = cellSizeDp,
                    alpha = 0.95f
                )
            }
        }

        // Daily Challenge Victory Dialog
        if (gameState.isDaily && gameState.isLevelWon) {
            DailyWonDialog(
                viewModel = viewModel,
                onHome = { viewModel.navigateTo(GameViewModel.Screen.MAIN_MENU) }
            )
        }

        // Game Over Dialog
        if (gameState.isGameOver) {
            GameOverDialog(
                gameState = gameState,
                onRestart = {
                    when {
                        gameState.isBlitz -> viewModel.startBlitzGame()
                        gameState.isDaily -> viewModel.startDailyChallenge()
                        else -> viewModel.startNewClassicGame()
                    }
                },
                onHome = { viewModel.navigateTo(GameViewModel.Screen.MAIN_MENU) }
            )
        }
    }
}

@Composable
fun DailyWonDialog(
    viewModel: GameViewModel,
    onHome: () -> Unit
) {
    Dialog(onDismissRequest = onHome) {
        IosWaterGlassCard(
            shape = RoundedCornerShape(28.dp),
            accentColor = Color(0xFFF59E0B),
            backgroundColor = Color(0xFF0F1B38).copy(alpha = 0.98f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "CHALLENGE WON! 🔥",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFBBF24)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Streak: ${viewModel.preferences.dailyStreak} Days in a row!",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .iosPressEffect { onHome() }
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFF59E0B), Color(0xFFEC4899))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Claim Reward & Menu",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
