package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AdventureLevel
import com.example.model.ObjectiveType
import com.example.ui.components.BoardView
import com.example.ui.components.FloatingTextOverlay
import com.example.ui.components.ParticleOverlay
import com.example.ui.components.PieceView
import com.example.ui.components.TrayView
import com.example.ui.theme.IosCircularButton
import com.example.ui.theme.IosGlassCard
import com.example.viewmodel.GameViewModel
import kotlin.math.roundToInt

@Composable
fun AdventureGameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.gameState.collectAsState()
    val dragState by viewModel.dragState.collectAsState()
    val currentTheme by viewModel.currentTheme.collectAsState()
    val level = AdventureLevel.getLevel(gameState.currentAdventureLevel)

    val progressFraction = (gameState.adventureProgress.toFloat() / level.targetValue.toFloat()).coerceIn(0f, 1f)

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
                // Back to Map
                IosCircularButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Map",
                    onClick = { viewModel.navigateTo(GameViewModel.Screen.ADVENTURE_MAP) },
                    modifier = Modifier.testTag("back_to_map_button")
                )

                // Level Title & Objective Capsule
                IosGlassCard(
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = Color(0xFF141F3C).copy(alpha = 0.85f),
                    borderColor = Color(0xFF38BDF8).copy(alpha = 0.25f),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "LEVEL ${level.levelNumber}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = when (level.objectiveType) {
                                ObjectiveType.SCORE -> "${gameState.score} / ${level.targetValue} PTS"
                                ObjectiveType.LINES -> "${gameState.adventureProgress} / ${level.targetValue} LINES"
                                ObjectiveType.COMBO -> "Combo x${level.targetValue}"
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }

                // Action buttons (Settings & Restart)
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
                        contentDescription = "Restart Level",
                        onClick = { viewModel.startAdventureLevel(gameState.currentAdventureLevel) },
                        modifier = Modifier.testTag("restart_level_button")
                    )
                }
            }

            // Objective Progress Bar (iOS Frosted Pill)
            IosGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                shape = RoundedCornerShape(14.dp),
                backgroundColor = Color(0xFF131D38).copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (gameState.isLevelWon) Color(0xFF10B981) else Color(0xFF38BDF8),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (gameState.isLevelWon) Color(0xFF10B981) else Color(0xFF2563EB),
                        trackColor = Color(0xFF1E293B),
                        strokeCap = StrokeCap.Round
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "${(progressFraction * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
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

        // Dragged piece following finger
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

        // Game Over / Victory Dialog
        if (gameState.isGameOver || gameState.isLevelWon) {
            val nextLevel = gameState.currentAdventureLevel + 1
            GameOverDialog(
                gameState = gameState,
                onRestart = { viewModel.startAdventureLevel(gameState.currentAdventureLevel) },
                onHome = { viewModel.navigateTo(GameViewModel.Screen.ADVENTURE_MAP) },
                onNextLevel = if (gameState.isLevelWon && nextLevel <= 30) {
                    { viewModel.startAdventureLevel(nextLevel) }
                } else null
            )
        }
    }
}
