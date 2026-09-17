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
import com.example.viewmodel.GameViewModel
import kotlin.math.roundToInt

@Composable
fun AdventureGameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.gameState.collectAsState()
    val dragState by viewModel.dragState.collectAsState()
    val level = AdventureLevel.getLevel(gameState.currentAdventureLevel)

    val progressFraction = (gameState.adventureProgress.toFloat() / level.targetValue.toFloat()).coerceIn(0f, 1f)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D1B3E),
                        Color(0xFF09132C),
                        Color(0xFF050B1B)
                    )
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
            // TOP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back to Map
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF192850))
                        .clickable { viewModel.navigateTo(GameViewModel.Screen.ADVENTURE_MAP) }
                        .testTag("adventure_back_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Map",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Objective Banner
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF192850))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "LEVEL ${gameState.currentAdventureLevel}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF60A5FA),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${level.objectiveDescription} (${gameState.adventureProgress}/${level.targetValue})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .width(160.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF10B981),
                            trackColor = Color(0xFF263765),
                            strokeCap = StrokeCap.Round
                        )
                    }
                }

                // Restart Level
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF192850))
                        .clickable { viewModel.startAdventureLevel(gameState.currentAdventureLevel) }
                        .testTag("restart_adventure_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart Level",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
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

        // Dragged piece following finger directly with screen boundary clamping
        if (dragState.isDragging && dragState.piece != null) {
            val piece = dragState.piece!!
            val cellSizeDp = 34.dp
            val cellSizePx = with(density) { cellSizeDp.toPx() }
            val pieceWidthPx = piece.width * cellSizePx
            val pieceHeightPx = piece.height * cellSizePx

            // Slightly elevated above the finger so blocks are clearly visible
            val liftOffset = GameViewModel.DRAG_LIFT_OFFSET_PX
            val targetX = dragState.touchPosition.x - (pieceWidthPx / 2f)
            val targetY = dragState.touchPosition.y - liftOffset - (pieceHeightPx / 2f)

            // Clamp so the piece NEVER exits the screen boundaries
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

        // Game Over or Victory Dialog
        if (gameState.isGameOver || gameState.isLevelWon) {
            GameOverDialog(
                gameState = gameState,
                onRestart = { viewModel.startAdventureLevel(gameState.currentAdventureLevel) },
                onHome = { viewModel.navigateTo(GameViewModel.Screen.ADVENTURE_MAP) },
                onNextLevel = if (gameState.isLevelWon && gameState.currentAdventureLevel < AdventureLevel.TOTAL_LEVELS) {
                    { viewModel.startAdventureLevel(gameState.currentAdventureLevel + 1) }
                } else null
            )
        }
    }
}
