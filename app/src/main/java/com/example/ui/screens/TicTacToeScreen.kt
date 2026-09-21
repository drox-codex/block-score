package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TicTacToeDifficulty
import com.example.model.TicTacToePlayer
import com.example.ui.theme.IosCircularButton
import com.example.ui.theme.IosGlassCard
import com.example.ui.theme.iosPressEffect
import com.example.viewmodel.GameViewModel

@Composable
fun TicTacToeScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.ticTacToeState.collectAsState()

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
            // TOP BAR
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
                    modifier = Modifier.testTag("tic_tac_toe_back_button")
                )

                Text(
                    text = "TIC TAC TOE",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.2.sp
                )

                IosCircularButton(
                    icon = Icons.Default.Refresh,
                    contentDescription = "Restart",
                    onClick = { viewModel.startTicTacToeGame(state.difficulty) },
                    modifier = Modifier.testTag("tic_tac_toe_restart_button")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // iOS Native Segmented Picker for Difficulty
            IosGlassCard(
                shape = RoundedCornerShape(14.dp),
                backgroundColor = Color(0xFF0E172F).copy(alpha = 0.9f),
                borderColor = Color.White.copy(alpha = 0.12f),
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(44.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val difficulties = listOf(
                        TicTacToeDifficulty.EASY to "Easy",
                        TicTacToeDifficulty.MEDIUM to "Medium",
                        TicTacToeDifficulty.HARD to "Hard"
                    )

                    difficulties.forEach { (diff, label) ->
                        val isSelected = state.difficulty == diff
                        val activeColor = when (diff) {
                            TicTacToeDifficulty.EASY -> Color(0xFF10B981)
                            TicTacToeDifficulty.MEDIUM -> Color(0xFFFBBF24)
                            TicTacToeDifficulty.HARD -> Color(0xFFEF4444)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(11.dp))
                                .background(
                                    if (isSelected) activeColor.copy(alpha = 0.25f) else Color.Transparent
                                )
                                .border(
                                    width = if (isSelected) 1.dp else 0.dp,
                                    color = if (isSelected) activeColor.copy(alpha = 0.7f) else Color.Transparent,
                                    shape = RoundedCornerShape(11.dp)
                                )
                                .clickable {
                                    if (state.difficulty != diff) {
                                        viewModel.startTicTacToeGame(diff)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Turn Indicator Capsule (Apple Dynamic Island style)
            IosGlassCard(
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color(0xFF142042).copy(alpha = 0.85f),
                borderColor = if (state.currentPlayer == TicTacToePlayer.X) Color(0xFF38BDF8).copy(alpha = 0.4f) else Color(0xFFF97316).copy(alpha = 0.4f),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    val turnColor = if (state.currentPlayer == TicTacToePlayer.X) Color(0xFF38BDF8) else Color(0xFFF97316)
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(turnColor)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (state.isGameOver) {
                            "Game Completed"
                        } else if (state.currentPlayer == TicTacToePlayer.X) {
                            "Your Turn (X)"
                        } else {
                            "Robot Thinking... (O)"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3x3 GRID (iOS Glass Board)
            IosGlassCard(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .aspectRatio(1f),
                shape = RoundedCornerShape(26.dp),
                backgroundColor = Color(0xFF101935).copy(alpha = 0.90f),
                borderColor = Color.White.copy(alpha = 0.15f),
                shadowElevation = 14.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (i in 0 until 3) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (j in 0 until 3) {
                                val index = i * 3 + j
                                val player = state.board[index]

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(Color(0xFF18254A))
                                        .border(
                                            width = 1.dp,
                                            color = when (player) {
                                                TicTacToePlayer.X -> Color(0xFF38BDF8).copy(alpha = 0.5f)
                                                TicTacToePlayer.O -> Color(0xFFF97316).copy(alpha = 0.5f)
                                                TicTacToePlayer.NONE -> Color.White.copy(alpha = 0.06f)
                                            },
                                            shape = RoundedCornerShape(18.dp)
                                        )
                                        .iosPressEffect(pressedScale = 0.92f) {
                                            viewModel.onTicTacToeCellClicked(index)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (player != TicTacToePlayer.NONE) {
                                        Text(
                                            text = if (player == TicTacToePlayer.X) "X" else "O",
                                            fontSize = 48.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (player == TicTacToePlayer.X) Color(0xFF38BDF8) else Color(0xFFF97316)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Game Result Banner / Replay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                contentAlignment = Alignment.Center
            ) {
                if (state.isGameOver) {
                    val resultText = when {
                        state.winner == TicTacToePlayer.X -> "VICTORY! YOU WON"
                        state.winner == TicTacToePlayer.O -> "ROBOT WON!"
                        else -> "DRAW MATCH"
                    }
                    val resultColor = when {
                        state.winner == TicTacToePlayer.X -> Color(0xFFFBBF24)
                        state.winner == TicTacToePlayer.O -> Color(0xFFEF4444)
                        else -> Color(0xFF94A3B8)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = resultText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = resultColor,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .iosPressEffect { viewModel.startTicTacToeGame(state.difficulty) }
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF2563EB))
                                .padding(horizontal = 24.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "Play Again",
                                fontSize = 15.sp,
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
