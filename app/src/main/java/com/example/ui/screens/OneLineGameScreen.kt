package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ParticleOverlay
import com.example.ui.theme.IosCircularButton
import com.example.ui.theme.IosGlassCard
import com.example.ui.theme.iosPressEffect
import com.example.viewmodel.GameViewModel

@Composable
fun OneLineGameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.oneLineGameState.collectAsState()
    val gameState by viewModel.gameState.collectAsState()
    if (state == null) return

    val level = state!!.currentLevel
    val path = state!!.path
    val isComplete = state!!.isLevelComplete

    Box(
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP BAR (iOS style)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IosCircularButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    onClick = { viewModel.navigateTo(GameViewModel.Screen.MAIN_MENU) },
                    modifier = Modifier.testTag("one_line_back_button")
                )

                // Level Capsule
                IosGlassCard(
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = Color(0xFF141F3C).copy(alpha = 0.85f),
                    borderColor = Color(0xFFF97316).copy(alpha = 0.35f)
                ) {
                    Text(
                        text = "LEVEL ${level.levelNumber}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 7.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hint Button
                    IosCircularButton(
                        icon = Icons.Default.Lightbulb,
                        contentDescription = "Hint",
                        iconTint = if (!isComplete) Color(0xFFFBBF24) else Color(0xFF64748B),
                        onClick = { if (!isComplete) viewModel.getOneLineHint() },
                        modifier = Modifier.testTag("one_line_hint_button")
                    )

                    // Restart Button
                    IosCircularButton(
                        icon = Icons.Default.Refresh,
                        contentDescription = "Restart",
                        onClick = { viewModel.restartOneLineLevel() },
                        modifier = Modifier.testTag("one_line_restart_button")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar & Counter Capsule
            IosGlassCard(
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color(0xFF131D38).copy(alpha = 0.7f),
                borderColor = Color.White.copy(alpha = 0.12f),
                modifier = Modifier.fillMaxWidth(0.92f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val progress = if (level.validCells.isEmpty()) 0f else path.size.toFloat() / level.validCells.size.toFloat()
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF34D399),
                        trackColor = Color(0xFF1E293B),
                        strokeCap = StrokeCap.Round
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "${path.size} / ${level.validCells.size}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Grid Area with uniform square cells inside iOS Glass Card
            IosGlassCard(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .aspectRatio(1f),
                shape = RoundedCornerShape(26.dp),
                backgroundColor = Color(0xFF101935).copy(alpha = 0.90f),
                borderColor = Color.White.copy(alpha = 0.15f),
                shadowElevation = 14.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    var canvasSize by remember { mutableStateOf(Size.Zero) }

                    fun handleTouch(offset: Offset) {
                        if (canvasSize.width > 0 && canvasSize.height > 0) {
                            val cellSize = kotlin.math.min(
                                canvasSize.width / level.gridWidth,
                                canvasSize.height / level.gridHeight
                            )
                            val totalW = cellSize * level.gridWidth
                            val totalH = cellSize * level.gridHeight
                            val startX = (canvasSize.width - totalW) / 2f
                            val startY = (canvasSize.height - totalH) / 2f

                            val col = ((offset.x - startX) / cellSize).toInt()
                            val row = ((offset.y - startY) / cellSize).toInt()
                            if (row in 0 until level.gridHeight && col in 0 until level.gridWidth) {
                                viewModel.onOneLineCellTouched(row, col)
                            }
                        }
                    }

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(level) {
                                detectTapGestures(
                                    onTap = { offset ->
                                        handleTouch(offset)
                                    }
                                )
                            }
                            .pointerInput(level) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        handleTouch(offset)
                                    },
                                    onDragEnd = {
                                        viewModel.onOneLineDragEnded()
                                    },
                                    onDragCancel = {
                                        viewModel.onOneLineDragEnded()
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        handleTouch(change.position)
                                    }
                                )
                            }
                    ) {
                        canvasSize = size

                        val cellSize = kotlin.math.min(
                            size.width / level.gridWidth,
                            size.height / level.gridHeight
                        )
                        val totalW = cellSize * level.gridWidth
                        val totalH = cellSize * level.gridHeight
                        val startX = (size.width - totalW) / 2f
                        val startY = (size.height - totalH) / 2f
                        val padding = kotlin.math.max(3f, cellSize * 0.08f)
                        val cellInnerSize = cellSize - padding * 2

                        // 1. Draw background cells (Exact uniform squares)
                        for (r in 0 until level.gridHeight) {
                            for (c in 0 until level.gridWidth) {
                                val pos = Pair(r, c)
                                if (level.validCells.contains(pos)) {
                                    val isStart = level.startCell == pos
                                    val isVisited = path.contains(pos)
                                    val cellColor = when {
                                        isVisited -> Color(0xFF10B981) // Green for visited
                                        isStart -> Color(0xFFFBBF24) // Yellow for starting cell
                                        else -> Color(0xFF1E2B4E) // Sleek slate for unvisited
                                    }
                                    val cellLeft = startX + c * cellSize + padding
                                    val cellTop = startY + r * cellSize + padding

                                    drawRoundRect(
                                        color = cellColor,
                                        topLeft = Offset(cellLeft, cellTop),
                                        size = Size(cellInnerSize, cellInnerSize),
                                        cornerRadius = CornerRadius(cellInnerSize * 0.22f)
                                    )
                                }
                            }
                        }

                        // 2. Draw continuous connecting line between visited cells
                        if (path.size > 1) {
                            val linePath = Path().apply {
                                val first = path.first()
                                val startPtX = startX + first.second * cellSize + cellSize / 2f
                                val startPtY = startY + first.first * cellSize + cellSize / 2f
                                moveTo(startPtX, startPtY)

                                for (i in 1 until path.size) {
                                    val pt = path[i]
                                    val ptX = startX + pt.second * cellSize + cellSize / 2f
                                    val ptY = startY + pt.first * cellSize + cellSize / 2f
                                    lineTo(ptX, ptY)
                                }
                            }

                            drawPath(
                                path = linePath,
                                color = Color(0xFF34D399),
                                style = Stroke(
                                    width = cellInnerSize * 0.38f,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }

                        // 3. Highlight current head of the path
                        if (path.isNotEmpty()) {
                            val head = path.last()
                            val headCx = startX + head.second * cellSize + cellSize / 2f
                            val headCy = startY + head.first * cellSize + cellSize / 2f
                            drawCircle(
                                color = Color.White,
                                radius = cellInnerSize * 0.20f,
                                center = Offset(headCx, headCy)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Completion Banner (iOS Style)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isComplete) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(54.dp)
                            .iosPressEffect {
                                viewModel.startOneLineLevel(level.levelNumber + 1)
                            }
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFF10B981))
                            .testTag("one_line_next_level_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (level.levelNumber < 20) "NEXT LEVEL →" else "ALL LEVELS COMPLETED! ★",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }

    // Particles on top
    ParticleOverlay(particles = gameState.particles)
}
