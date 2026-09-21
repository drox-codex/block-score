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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun ClassicGameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.gameState.collectAsState()
    val dragState by viewModel.dragState.collectAsState()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0C1635),
                        Color(0xFF080E23),
                        Color(0xFF050814)
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
            // TOP BAR (iOS style)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home/Back button (iOS Circular Button)
                IosCircularButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Menu",
                    onClick = { viewModel.navigateTo(GameViewModel.Screen.MAIN_MENU) },
                    modifier = Modifier.testTag("back_to_menu_button")
                )

                // Scores Row in Apple Frosted Glass Capsules
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Current Score Capsule
                    IosGlassCard(
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = Color(0xFF141F3C).copy(alpha = 0.85f),
                        borderColor = Color(0xFF38BDF8).copy(alpha = 0.25f)
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

                    // Best Score Capsule
                    IosGlassCard(
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = Color(0xFF141F3C).copy(alpha = 0.85f),
                        borderColor = Color(0xFFFBBF24).copy(alpha = 0.25f)
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
                        contentDescription = "Restart Game",
                        onClick = { viewModel.startNewClassicGame() },
                        modifier = Modifier.testTag("restart_game_button")
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

        // Game Over Dialog
        if (gameState.isGameOver) {
            GameOverDialog(
                gameState = gameState,
                onRestart = { viewModel.startNewClassicGame() },
                onHome = { viewModel.navigateTo(GameViewModel.Screen.MAIN_MENU) }
            )
        }
    }
}
