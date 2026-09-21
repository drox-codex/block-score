package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.theme.IosCircularButton
import com.example.ui.theme.IosGameActionCard
import com.example.ui.theme.IosGlassCard
import com.example.ui.theme.iosPressEffect
import com.example.viewmodel.GameViewModel

@Composable
fun MainMenuScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val bestScore = viewModel.preferences.bestScore
    var showMoreGamesDialog by remember { mutableStateOf(false) }
    var showTicTacToeDifficultyDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Subtle ambient breathing glow animation behind the logo
    val infiniteTransition = rememberInfiniteTransition(label = "ambientLogoPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

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
        // Ambient iOS background orbs (top-center cyan glow, right indigo glow)
        Box(
            modifier = Modifier
                .size(340.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-80).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF2563EB).copy(alpha = pulseAlpha),
                            Color(0xFF1D4ED8).copy(alpha = pulseAlpha * 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Top Header: iOS Navigation buttons (Settings & Achievements)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Achievements Trophy button
            IosCircularButton(
                icon = Icons.Default.EmojiEvents,
                contentDescription = "Achievements",
                iconTint = Color(0xFFFBBF24),
                onClick = { viewModel.navigateTo(GameViewModel.Screen.ACHIEVEMENTS) },
                modifier = Modifier.testTag("achievements_button")
            )

            // Settings Gear button
            IosCircularButton(
                icon = Icons.Default.Settings,
                contentDescription = "Settings",
                iconTint = Color(0xFFE2E8F0),
                onClick = { viewModel.navigateTo(GameViewModel.Screen.SETTINGS) },
                modifier = Modifier.testTag("settings_button")
            )
        }

        // Center Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // App Logo Icon with continuous iOS squircle shape and subtle drop shadow
            Box(
                modifier = Modifier
                    .size(116.dp)
                    .shadow(16.dp, RoundedCornerShape(28.dp), ambientColor = Color(0xFF2563EB), spotColor = Color(0xFF2563EB))
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFF152248))
                    .border(1.5.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_block_logo),
                    contentDescription = "BLOCK SCORE Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // BLOCK SCORE Title with Apple SF Pro bold weight and refined tracking
            Text(
                text = "BLOCK SCORE",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 1.8.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Best Score Card - Apple Fitness / Game Center pill
            IosGlassCard(
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color(0xFF162347).copy(alpha = 0.85f),
                modifier = Modifier.testTag("best_score_card")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Best Score",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BEST SCORE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "$bestScore",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // iOS Arcade Cards Column
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Adventure Mode Card
                IosGameActionCard(
                    title = "Adventure",
                    subtitle = "Conquer 30 progressive puzzle stages",
                    icon = Icons.Default.Public,
                    accentColor = Color(0xFF2563EB),
                    trailingBadge = "Stage ${viewModel.preferences.unlockedLevel}/30",
                    onClick = { viewModel.navigateTo(GameViewModel.Screen.ADVENTURE_MAP) },
                    modifier = Modifier.testTag("adventure_button")
                )

                // Classic 8x8 Mode Card
                IosGameActionCard(
                    title = "Classic 8x8",
                    subtitle = "Infinite polyomino block matching",
                    icon = Icons.Default.PlayArrow,
                    accentColor = Color(0xFF10B981),
                    trailingBadge = if (bestScore > 0) "Record: $bestScore" else "Play",
                    onClick = {
                        viewModel.startNewClassicGame()
                        viewModel.navigateTo(GameViewModel.Screen.CLASSIC_GAME)
                    },
                    modifier = Modifier.testTag("classic_button")
                )

                // More Games Card
                IosGameActionCard(
                    title = "More Games",
                    subtitle = "One Line path & Tic-Tac-Toe vs Robot",
                    icon = Icons.Default.Games,
                    accentColor = Color(0xFF8B5CF6),
                    trailingBadge = "2 Games",
                    onClick = { showMoreGamesDialog = true },
                    modifier = Modifier.testTag("more_games_button")
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Bottom Brand & Telegram Link
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // iOS Frosted Glass Telegram Link Pill
                IosGlassCard(
                    shape = RoundedCornerShape(18.dp),
                    backgroundColor = Color(0xFF0284C7).copy(alpha = 0.25f),
                    borderColor = Color(0xFF38BDF8).copy(alpha = 0.4f),
                    modifier = Modifier
                        .iosPressEffect(pressedScale = 0.94f) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/drox_71"))
                            context.startActivity(intent)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Join Telegram @drox_71",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE0F2FE)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "DROX STUDIO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 2.5.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // More Games iOS Action Sheet Dialog
    if (showMoreGamesDialog) {
        Dialog(onDismissRequest = { showMoreGamesDialog = false }) {
            IosGlassCard(
                shape = RoundedCornerShape(28.dp),
                backgroundColor = Color(0xFF101935).copy(alpha = 0.95f),
                borderColor = Color.White.copy(alpha = 0.18f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Sheet Top Grabber Handle
                    Box(
                        modifier = Modifier
                            .size(36.dp, 4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "More Games",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Choose a puzzle to play",
                        fontSize = 13.sp,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // One Line Card
                    IosGameActionCard(
                        title = "One Line",
                        subtitle = "Connect all tiles without overlapping",
                        icon = Icons.Default.Timeline,
                        accentColor = Color(0xFFF97316),
                        trailingBadge = "Level ${viewModel.oneLineLevelProgress}/20",
                        onClick = {
                            showMoreGamesDialog = false
                            val nextLevel = viewModel.oneLineLevelProgress
                            viewModel.startOneLineLevel(nextLevel)
                        },
                        modifier = Modifier.testTag("one_line_button")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tic Tac Toe Card
                    IosGameActionCard(
                        title = "Tic Tac Toe",
                        subtitle = "Classic X vs O match against intelligent Robot",
                        icon = Icons.Default.Games,
                        accentColor = Color(0xFF10B981),
                        trailingBadge = "vs AI",
                        onClick = {
                            showMoreGamesDialog = false
                            showTicTacToeDifficultyDialog = true
                        },
                        modifier = Modifier.testTag("tic_tac_toe_button")
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Apple-style Done Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .iosPressEffect { showMoreGamesDialog = false }
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.10f))
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Done",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }

    // Tic Tac Toe Difficulty Dialog (iOS Segmented Style)
    if (showTicTacToeDifficultyDialog) {
        Dialog(onDismissRequest = { showTicTacToeDifficultyDialog = false }) {
            IosGlassCard(
                shape = RoundedCornerShape(28.dp),
                backgroundColor = Color(0xFF101935).copy(alpha = 0.95f),
                borderColor = Color.White.copy(alpha = 0.18f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp, 4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Select Difficulty",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Challenge the Robot at your preferred level",
                        fontSize = 13.sp,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    val difficulties = listOf(
                        Triple(com.example.model.TicTacToeDifficulty.EASY, Color(0xFF10B981), "Relaxed casual match"),
                        Triple(com.example.model.TicTacToeDifficulty.MEDIUM, Color(0xFFFBBF24), "Balanced tactical moves"),
                        Triple(com.example.model.TicTacToeDifficulty.HARD, Color(0xFFEF4444), "Unbeatable smart AI")
                    )

                    difficulties.forEach { (diff, color, subtitle) ->
                        IosGlassCard(
                            shape = RoundedCornerShape(18.dp),
                            backgroundColor = Color(0xFF172346).copy(alpha = 0.85f),
                            borderColor = color.copy(alpha = 0.35f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .iosPressEffect {
                                    showTicTacToeDifficultyDialog = false
                                    viewModel.startTicTacToeGame(diff)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = diff.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = subtitle,
                                        fontSize = 12.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.3f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .iosPressEffect { showTicTacToeDifficultyDialog = false }
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.10f))
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
