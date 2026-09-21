package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.GameState
import com.example.ui.theme.IosGlassCard
import com.example.ui.theme.iosPressEffect

@Composable
fun GameOverDialog(
    gameState: GameState,
    onRestart: () -> Unit,
    onHome: () -> Unit,
    onNextLevel: (() -> Unit)? = null
) {
    Dialog(onDismissRequest = { /* Modal */ }) {
        IosGlassCard(
            shape = RoundedCornerShape(30.dp),
            backgroundColor = Color(0xFF101935).copy(alpha = 0.95f),
            borderColor = Color.White.copy(alpha = 0.18f),
            shadowElevation = 20.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("game_over_dialog")
        ) {
            Column(
                modifier = Modifier.padding(26.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Top Sheet Grabber
                Box(
                    modifier = Modifier
                        .size(36.dp, 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.22f))
                )

                Spacer(modifier = Modifier.height(18.dp))

                if (gameState.isLevelWon) {
                    // VICTORY TITLE
                    Text(
                        text = "VICTORY!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFBBF24),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Level ${gameState.currentAdventureLevel} Cleared",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stars (1 to 3)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (s in 1..3) {
                            val active = s <= gameState.adventureStars
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (active) Color(0xFFFBBF24) else Color(0xFF334155),
                                modifier = Modifier.size(if (s == 2) 42.dp else 34.dp)
                            )
                        }
                    }
                } else {
                    // GAME OVER TITLE
                    Text(
                        text = "GAME OVER",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "No more moves available",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF94A3B8)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Score Capsule (Apple Fitness style)
                IosGlassCard(
                    shape = RoundedCornerShape(18.dp),
                    backgroundColor = Color(0xFF162347).copy(alpha = 0.85f),
                    borderColor = Color.White.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "FINAL SCORE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${gameState.score}",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )

                        if (gameState.isClassic) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = Color(0xFFFBBF24),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "BEST: ${gameState.bestScore}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (gameState.score >= gameState.bestScore && gameState.score > 0) Color(0xFF10B981) else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Action Buttons (iOS Spring Tap Effect)
                if (gameState.isLevelWon && onNextLevel != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .iosPressEffect(onClick = onNextLevel)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF10B981))
                            .testTag("next_level_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Next Level",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Home Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .iosPressEffect(onClick = onHome)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.10f))
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                            .testTag("home_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Main Menu",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Restart Button
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .height(52.dp)
                            .iosPressEffect(onClick = onRestart)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF2563EB))
                            .testTag("restart_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (gameState.isLevelWon) "Replay" else "Play Again",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
