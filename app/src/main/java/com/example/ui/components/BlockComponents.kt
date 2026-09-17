package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BlockColor
import com.example.model.BlockPiece
import com.example.model.FloatingText
import com.example.model.Particle
import com.example.viewmodel.DragState
import kotlin.math.roundToInt

/**
 * 3D polished casual game block cell.
 */
@Composable
fun BlockCell(
    color: BlockColor,
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
    isGhost: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = if (isGhost) {
                        listOf(
                            color.highlightColor.copy(alpha = 0.45f * alpha),
                            color.mainColor.copy(alpha = 0.35f * alpha)
                        )
                    } else {
                        listOf(
                            color.highlightColor.copy(alpha = alpha),
                            color.mainColor.copy(alpha = alpha),
                            color.shadowColor.copy(alpha = alpha)
                        )
                    },
                    start = Offset(0f, 0f),
                    end = Offset(100f, 100f)
                )
            )
            .border(
                width = 1.dp,
                color = if (isGhost) color.highlightColor.copy(alpha = 0.6f) else color.highlightColor.copy(alpha = 0.8f * alpha),
                shape = RoundedCornerShape(6.dp)
            )
    ) {
        if (!isGhost) {
            // Subtle top-left glossy reflection bar
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(3.dp)
                    .align(Alignment.TopStart)
                    .padding(start = 3.dp, top = 2.dp)
                    .background(
                        Color.White.copy(alpha = 0.45f * alpha),
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

/**
 * Renders a polyomino block piece.
 */
@Composable
fun PieceView(
    piece: BlockPiece,
    cellSize: Dp = 24.dp,
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
    isGhost: Boolean = false
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (r in 0 until piece.height) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (c in 0 until piece.width) {
                    if (piece.shapeMatrix[r][c]) {
                        BlockCell(
                            color = piece.color,
                            modifier = Modifier.size(cellSize),
                            alpha = alpha,
                            isGhost = isGhost
                        )
                    } else {
                        Box(modifier = Modifier.size(cellSize))
                    }
                }
            }
        }
    }
}

/**
 * Interactive 8x8 Board.
 */
@Composable
fun BoardView(
    board: List<List<Int>>,
    dragState: DragState,
    onBoardPositioned: (Offset, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF131C35))
            .border(2.dp, Color(0xFF263765), RoundedCornerShape(16.dp))
            .padding(8.dp)
            .onGloballyPositioned { coordinates ->
                val bounds = coordinates.boundsInRoot()
                onBoardPositioned(bounds.topLeft, bounds.width)
            }
            .testTag("game_board")
    ) {
        val boardWidth = maxWidth
        val cellSize = (boardWidth - (7.dp * 2)) / 8 // spaced by 2dp

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            for (r in 0 until 8) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    for (c in 0 until 8) {
                        val cellValue = board[r][c]

                        // Check if current cell is covered by the ghost preview
                        var isGhostCell = false
                        var ghostColor: BlockColor? = null

                        if (dragState.isDragging && dragState.isValid && dragState.piece != null && dragState.hoverRow != null && dragState.hoverCol != null) {
                            val relR = r - dragState.hoverRow
                            val relC = c - dragState.hoverCol
                            if (relR in 0 until dragState.piece.height && relC in 0 until dragState.piece.width) {
                                if (dragState.piece.shapeMatrix[relR][relC]) {
                                    isGhostCell = true
                                    ghostColor = dragState.piece.color
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF1A2645))
                                .border(1.dp, Color(0xFF213054), RoundedCornerShape(6.dp))
                        ) {
                            if (cellValue != 0) {
                                BlockCell(
                                    color = BlockColor.fromId(cellValue),
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else if (isGhostCell && ghostColor != null) {
                                BlockCell(
                                    color = ghostColor,
                                    modifier = Modifier.fillMaxSize(),
                                    isGhost = true
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Bottom Tray displaying 3 pieces.
 */
@Composable
fun TrayView(
    pieces: List<BlockPiece?>,
    dragState: DragState,
    onDragStart: (Int, Offset) -> Unit,
    onDragMove: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until 3) {
            val piece = pieces.getOrNull(i)
            val isBeingDragged = dragState.isDragging && dragState.slotIndex == i
            var slotPositionInRoot by remember { mutableStateOf(Offset.Zero) }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x1AFFFFFF))
                    .onGloballyPositioned { coordinates ->
                        slotPositionInRoot = coordinates.positionInRoot()
                    }
                    .pointerInput(piece) {
                        if (piece != null) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    onDragStart(i, slotPositionInRoot + offset)
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    val screenPos = slotPositionInRoot + change.position
                                    onDragMove(screenPos)
                                },
                                onDragEnd = { onDragEnd() },
                                onDragCancel = { onDragCancel() }
                            )
                        }
                    }
                    .testTag("piece_slot_$i"),
                contentAlignment = Alignment.Center
            ) {
                if (piece != null && !isBeingDragged) {
                    PieceView(
                        piece = piece,
                        cellSize = 22.dp
                    )
                }
            }
        }
    }
}

/**
 * Particle system canvas for sparkling line clears and combo bursts.
 */
@Composable
fun ParticleOverlay(
    particles: List<Particle>,
    modifier: Modifier = Modifier
) {
    if (particles.isEmpty()) return

    Canvas(modifier = modifier.fillMaxSize()) {
        for (p in particles) {
            // Soft outer glow aura
            drawCircle(
                color = p.color.copy(alpha = 0.35f),
                radius = p.size * 2.2f,
                center = Offset(p.x, p.y)
            )
            // Vibrant sparkling core
            drawCircle(
                color = p.color.copy(alpha = 0.95f),
                radius = p.size,
                center = Offset(p.x, p.y)
            )
        }
    }
}

/**
 * Floating score text overlay with bounce and fade effects.
 */
@Composable
fun FloatingTextOverlay(
    floatingTexts: List<FloatingText>,
    modifier: Modifier = Modifier
) {
    for (item in floatingTexts) {
        val offsetY = remember { Animatable(0f) }
        val alpha = remember { Animatable(1f) }
        val scale = remember { Animatable(0.7f) }

        LaunchedEffect(item.id) {
            scale.animateTo(
                targetValue = 1.2f,
                animationSpec = tween(durationMillis = 180)
            )
            scale.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 200)
            )
        }
        LaunchedEffect(item.id) {
            offsetY.animateTo(
                targetValue = -90f,
                animationSpec = tween(durationMillis = 650, easing = LinearEasing)
            )
        }
        LaunchedEffect(item.id) {
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 650, delayMillis = 180)
            )
        }

        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            Text(
                text = item.text,
                color = item.color.copy(alpha = alpha.value),
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = item.x.roundToInt() - 60,
                            y = (item.y + offsetY.value).roundToInt()
                        )
                    }
                    .shadow(elevation = 8.dp)
            )
        }
    }
}
