package com.example.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TargetBasedAnimation
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.SpinReward
import com.example.model.SpinWheelConfig
import com.example.ui.theme.IosGlassCard
import com.example.ui.theme.IosWaterGlassCard
import com.example.ui.theme.iosPressEffect
import com.example.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val SLICE_COLORS = listOf(
    Color(0xFFEC4899), // 0: +3 Hints
    Color(0xFF0284C7), // 1: +100 Score
    Color(0xFF8B5CF6), // 2: Aqua Glow
    Color(0xFFF59E0B), // 3: +5 Hints
    Color(0xFF10B981), // 4: +250 Score
    Color(0xFFF43F5E)  // 5: Lucky Spin
)

@Composable
fun LuckyWheelDialog(
    viewModel: GameViewModel,
    onDismiss: () -> Unit
) {
    var isSpinning by remember { mutableStateOf(false) }
    var currentAngle by remember { mutableFloatStateOf(0f) }
    var wonReward by remember { mutableStateOf<SpinReward?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val rewards = SpinWheelConfig.REWARDS

    Dialog(onDismissRequest = { if (!isSpinning) onDismiss() }) {
        IosWaterGlassCard(
            shape = RoundedCornerShape(32.dp),
            accentColor = Color(0xFFEC4899),
            backgroundColor = Color(0xFF0C152F).copy(alpha = 0.98f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = null,
                        tint = Color(0xFFEC4899),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LUCKY SPIN",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.2.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // The Wheel with Top Indicator
                Box(
                    modifier = Modifier
                        .size(280.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Wheel Canvas
                    Canvas(
                        modifier = Modifier
                            .size(260.dp)
                            .shadow(20.dp, CircleShape, ambientColor = Color(0xFFEC4899), spotColor = Color(0xFF8B5CF6))
                            .clip(CircleShape)
                    ) {
                        val canvasRadius = size.minDimension / 2f
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val sliceSweep = 360f / rewards.size // 60 deg

                        // Draw wheel rotated by currentAngle
                        rotate(degrees = currentAngle, pivot = center) {
                            for (i in rewards.indices) {
                                // Start angle: top slice (i=0) starts at -90 - 30 = -120 deg
                                val startAngle = -90f - (sliceSweep / 2f) + (i * sliceSweep)
                                
                                // Draw slice arc
                                drawArc(
                                    color = SLICE_COLORS[i % SLICE_COLORS.size],
                                    startAngle = startAngle,
                                    sweepAngle = sliceSweep,
                                    useCenter = true,
                                    size = Size(size.width, size.height),
                                    topLeft = Offset.Zero
                                )

                                // Draw slice border line
                                val rad = (startAngle * PI / 180f).toFloat()
                                val lineEnd = Offset(
                                    center.x + canvasRadius * cos(rad),
                                    center.y + canvasRadius * sin(rad)
                                )
                                drawLine(
                                    color = Color.White.copy(alpha = 0.35f),
                                    start = center,
                                    end = lineEnd,
                                    strokeWidth = 2.5f
                                )

                                // Draw slice text/label
                                val midAngle = startAngle + (sliceSweep / 2f)
                                val midRad = (midAngle * PI / 180f).toFloat()
                                val textDist = canvasRadius * 0.62f
                                val tx = center.x + textDist * cos(midRad)
                                val ty = center.y + textDist * sin(midRad)

                                drawIntoCanvas { nativeCanvas ->
                                    val paint = Paint().apply {
                                        color = android.graphics.Color.WHITE
                                        textSize = 34f
                                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                                        textAlign = Paint.Align.CENTER
                                        isAntiAlias = true
                                        setShadowLayer(4f, 0f, 2f, android.graphics.Color.BLACK)
                                    }

                                    nativeCanvas.nativeCanvas.save()
                                    nativeCanvas.nativeCanvas.translate(tx, ty)
                                    nativeCanvas.nativeCanvas.rotate(midAngle + 90f)
                                    nativeCanvas.nativeCanvas.drawText(rewards[i].label, 0f, 12f, paint)
                                    nativeCanvas.nativeCanvas.restore()
                                }
                            }

                            // Outer gold ring
                            drawCircle(
                                color = Color(0xFFFBBF24),
                                radius = canvasRadius,
                                center = center,
                                style = Stroke(width = 6f)
                            )
                        }

                        // Center shiny hub cap
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                            ),
                            radius = canvasRadius * 0.22f,
                            center = center
                        )
                        drawCircle(
                            color = Color(0xFFFBBF24),
                            radius = canvasRadius * 0.22f,
                            center = center,
                            style = Stroke(width = 3.5f)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = canvasRadius * 0.06f,
                            center = center
                        )
                    }

                    // Top Pointer Arrow (Points down at the top center slice)
                    Canvas(
                        modifier = Modifier
                            .size(34.dp, 36.dp)
                            .align(Alignment.TopCenter)
                            .offset(y = (-6).dp)
                    ) {
                        val path = Path().apply {
                            moveTo(size.width / 2f, size.height) // tip pointing down
                            lineTo(0f, 0f) // top left
                            lineTo(size.width, 0f) // top right
                            close()
                        }
                        // Drop shadow
                        drawPath(
                            path = path,
                            color = Color.Black.copy(alpha = 0.5f)
                        )
                        // Main gold needle
                        drawPath(
                            path = path,
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFFFDE047), Color(0xFFEAB308))
                            )
                        )
                        // Needle highlight border
                        drawPath(
                            path = path,
                            color = Color.White,
                            style = Stroke(width = 2.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Won Reward banner
                if (wonReward != null) {
                    IosGlassCard(
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = Color(0xFF1E293B).copy(alpha = 0.90f),
                        borderColor = Color(0xFFFBBF24).copy(alpha = 0.60f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🎉 You Won: ",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = wonReward!!.label,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFBBF24)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Action Button (SPIN)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .iosPressEffect(enabled = !isSpinning) {
                            if (!isSpinning) {
                                isSpinning = true
                                wonReward = null
                                coroutineScope.launch {
                                    // Pick random winning slice (0..5)
                                    val winningIndex = (0 until rewards.size).random()
                                    val sliceSweep = 360f / rewards.size // 60 deg
                                    
                                    // To place slice `winningIndex` under top needle (at -90°):
                                    // Target final angle modulo 360 must equal -(winningIndex * 60)
                                    val targetModulo = (360f - (winningIndex * sliceSweep)) % 360f
                                    val fullSpins = 5 * 360f // 5 full revolutions
                                    
                                    val startAngle = currentAngle % 360f
                                    var delta = targetModulo - startAngle
                                    if (delta < 0) delta += 360f
                                    val targetAngle = currentAngle + fullSpins + delta

                                    val anim = TargetBasedAnimation(
                                        animationSpec = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
                                        typeConverter = Float.VectorConverter,
                                        initialValue = currentAngle,
                                        targetValue = targetAngle
                                    )

                                    val startTime = System.nanoTime()
                                    while (true) {
                                        val playTime = System.nanoTime() - startTime
                                        currentAngle = anim.getValueFromNanos(playTime)
                                        if (anim.isFinishedFromNanos(playTime)) {
                                            currentAngle = targetAngle
                                            break
                                        }
                                        delay(16)
                                    }

                                    // Award the prize
                                    val reward = rewards[winningIndex]
                                    when (reward.id) {
                                        0, 3 -> viewModel.preferences.hintsCount += reward.rewardValue
                                        1, 4 -> viewModel.preferences.updateBestScore(viewModel.preferences.bestScore + reward.rewardValue)
                                        2 -> viewModel.setTheme(com.example.model.AppTheme.AQUA_GLASS)
                                        5 -> viewModel.preferences.hintsCount += 2
                                    }
                                    wonReward = reward
                                    viewModel.soundManager.playLevelComplete()
                                    isSpinning = false
                                }
                            }
                        }
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFFEC4899), Color(0xFF8B5CF6))
                            )
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isSpinning) "Spinning..." else "SPIN FOR PRIZES",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        letterSpacing = 0.8.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Close Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .iosPressEffect(enabled = !isSpinning) { onDismiss() }
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Close",
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
