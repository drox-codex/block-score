package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TicTacToePlayer
import com.example.viewmodel.GameViewModel

@Composable
fun TicTacToeScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.ticTacToeState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo(GameViewModel.Screen.MAIN_MENU) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "TIC TAC TOE",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(onClick = { viewModel.startTicTacToeGame(state.difficulty) }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Restart",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Difficulty: ${state.difficulty.name}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Grid
        Box(
            modifier = Modifier
                .padding(32.dp)
                .aspectRatio(1f)
                .background(Color(0xFF263765), RoundedCornerShape(16.dp))
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                for (i in 0 until 3) {
                    Row(modifier = Modifier.weight(1f)) {
                        for (j in 0 until 3) {
                            val index = i * 3 + j
                            val player = state.board[index]
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF131F3F))
                                    .clickable { viewModel.onTicTacToeCellClicked(index) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (player != TicTacToePlayer.NONE) {
                                    Text(
                                        text = if (player == TicTacToePlayer.X) "X" else "O",
                                        fontSize = 64.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (player == TicTacToePlayer.X) Color(0xFFF97316) else Color(0xFF10B981)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (state.isGameOver) {
            val infiniteTransition = rememberInfiniteTransition()
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(400, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            val resultText = when {
                state.winner == TicTacToePlayer.X -> "YOU WIN!"
                state.winner == TicTacToePlayer.O -> "ROBOT WINS!"
                else -> "DRAW!"
            }
            val resultColor = when {
                state.winner == TicTacToePlayer.X -> Color(0xFFFBBF24)
                state.winner == TicTacToePlayer.O -> Color(0xFFEF4444)
                else -> Color(0xFF94A3B8)
            }

            Text(
                text = resultText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = resultColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = { viewModel.startTicTacToeGame(state.difficulty) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(64.dp)
                    .scale(scale)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "PLAY AGAIN",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
    }
}
