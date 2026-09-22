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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import com.example.model.AppTheme
import com.example.model.DailyChallenge
import com.example.model.SpinReward
import com.example.model.SpinWheelConfig
import com.example.ui.components.LuckyWheelDialog
import com.example.ui.theme.IosCircularButton
import com.example.ui.theme.IosGameActionCard
import com.example.ui.theme.IosGlassCard
import com.example.ui.theme.IosWaterGlassCard
import com.example.ui.theme.iosPressEffect
import com.example.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainMenuScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val bestScore = viewModel.preferences.bestScore
    val blitzBest = viewModel.preferences.blitzBestScore
    val dailyStreak = viewModel.preferences.dailyStreak
    val today = DailyChallenge.getTodayDate()
    val isDailyCompleted = viewModel.preferences.lastDailyCompletedDate == today

    var showMoreGamesDialog by remember { mutableStateOf(false) }
    var showTicTacToeDifficultyDialog by remember { mutableStateOf(false) }
    var showLuckyWheelDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val infiniteTransition = rememberInfiniteTransition(label = "ambientPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.40f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
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
        // TOP HEADER BAR (Directly inside root column so clicks are never obscured)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Group: Trophy Achievements & Lucky Spin
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                IosCircularButton(
                    icon = Icons.Default.EmojiEvents,
                    contentDescription = "Achievements",
                    iconTint = Color(0xFFFBBF24),
                    onClick = { viewModel.navigateTo(GameViewModel.Screen.ACHIEVEMENTS) },
                    modifier = Modifier.testTag("achievements_button")
                )

                IosCircularButton(
                    icon = Icons.Default.Casino,
                    contentDescription = "Lucky Spin",
                    iconTint = Color(0xFFEC4899),
                    onClick = { showLuckyWheelDialog = true },
                    modifier = Modifier.testTag("lucky_wheel_button")
                )
            }

            // Right Group: Theme Switcher & Settings
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                IosCircularButton(
                    icon = Icons.Default.Palette,
                    contentDescription = "Themes",
                    iconTint = currentTheme.primaryAccent,
                    onClick = { showThemeDialog = true },
                    modifier = Modifier.testTag("theme_selector_button")
                )

                IosCircularButton(
                    icon = Icons.Default.Settings,
                    contentDescription = "Settings",
                    iconTint = Color(0xFFE2E8F0),
                    onClick = { viewModel.navigateTo(GameViewModel.Screen.SETTINGS) },
                    modifier = Modifier.testTag("settings_button")
                )
            }
        }

        // SCROLLABLE CENTER CONTENT
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // App Logo Icon with continuous iOS squircle shape and specular refraction
            Box(
                modifier = Modifier
                    .size(108.dp)
                    .shadow(16.dp, RoundedCornerShape(26.dp), ambientColor = currentTheme.primaryAccent, spotColor = currentTheme.primaryAccent)
                    .clip(RoundedCornerShape(26.dp))
                    .background(currentTheme.glassBackground)
                    .border(1.5.dp, currentTheme.glassBorder, RoundedCornerShape(26.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_block_logo),
                    contentDescription = "BLOCK SCORE Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "BLOCK SCORE",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 1.6.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Best Score Glass Pill
            IosWaterGlassCard(
                shape = RoundedCornerShape(20.dp),
                accentColor = currentTheme.primaryAccent,
                backgroundColor = currentTheme.glassBackground,
                modifier = Modifier.testTag("best_score_card")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Best Score",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RECORD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$bestScore",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // GAME MODES COLUMN
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Adventure Mode
                IosGameActionCard(
                    title = "Adventure",
                    icon = Icons.Default.Public,
                    accentColor = Color(0xFF2563EB),
                    trailingBadge = "Stage ${viewModel.preferences.unlockedLevel}/30",
                    onClick = { viewModel.navigateTo(GameViewModel.Screen.ADVENTURE_MAP) },
                    modifier = Modifier.testTag("adventure_button")
                )

                // 2. Classic 8x8 Mode
                IosGameActionCard(
                    title = "Classic 8x8",
                    icon = Icons.Default.PlayArrow,
                    accentColor = Color(0xFF10B981),
                    trailingBadge = if (bestScore > 0) "$bestScore" else "Play",
                    onClick = {
                        viewModel.startNewClassicGame()
                        viewModel.navigateTo(GameViewModel.Screen.CLASSIC_GAME)
                    },
                    modifier = Modifier.testTag("classic_button")
                )

                // 3. Daily Challenge
                IosGameActionCard(
                    title = "Daily Challenge",
                    icon = Icons.Default.DateRange,
                    accentColor = Color(0xFFF59E0B),
                    trailingBadge = if (isDailyCompleted) "Done 🔥$dailyStreak" else "Day 🔥$dailyStreak",
                    onClick = {
                        viewModel.startDailyChallenge()
                    },
                    modifier = Modifier.testTag("daily_challenge_button")
                )

                // 4. Blitz 90s Speed Mode
                IosGameActionCard(
                    title = "Blitz 90s",
                    icon = Icons.Default.Bolt,
                    accentColor = Color(0xFFEC4899),
                    trailingBadge = if (blitzBest > 0) "Top: $blitzBest" else "2x Multiplier",
                    onClick = {
                        viewModel.startBlitzGame()
                    },
                    modifier = Modifier.testTag("blitz_button")
                )

                // 5. Zen Relaxed Mode
                IosGameActionCard(
                    title = "Zen Mode",
                    icon = Icons.Default.Favorite,
                    accentColor = Color(0xFF06B6D4),
                    trailingBadge = "Endless",
                    onClick = {
                        viewModel.startZenGame()
                    },
                    modifier = Modifier.testTag("zen_button")
                )

                // 6. More Games
                IosGameActionCard(
                    title = "More Games",
                    icon = Icons.Default.Games,
                    accentColor = Color(0xFF8B5CF6),
                    trailingBadge = "One Line & Tic Tac Toe",
                    onClick = { showMoreGamesDialog = true },
                    modifier = Modifier.testTag("more_games_button")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Telegram community channel pill
            IosGlassCard(
                shape = RoundedCornerShape(18.dp),
                backgroundColor = Color(0xFF0284C7).copy(alpha = 0.20f),
                borderColor = Color(0xFF38BDF8).copy(alpha = 0.35f),
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

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "DROX STUDIO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 2.5.sp
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Lucky Wheel Dialog
    if (showLuckyWheelDialog) {
        LuckyWheelDialog(
            viewModel = viewModel,
            onDismiss = { showLuckyWheelDialog = false }
        )
    }

    // Theme Picker Dialog
    if (showThemeDialog) {
        ThemePickerDialog(
            currentTheme = currentTheme,
            onThemeSelect = { theme ->
                viewModel.setTheme(theme)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    // More Games Dialog
    if (showMoreGamesDialog) {
        Dialog(onDismissRequest = { showMoreGamesDialog = false }) {
            IosGlassCard(
                shape = RoundedCornerShape(28.dp),
                backgroundColor = Color(0xFF101935).copy(alpha = 0.96f),
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
                        text = "More Games",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    IosGameActionCard(
                        title = "One Line",
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

                    Spacer(modifier = Modifier.height(10.dp))

                    IosGameActionCard(
                        title = "Tic Tac Toe",
                        icon = Icons.Default.Games,
                        accentColor = Color(0xFF10B981),
                        trailingBadge = "vs AI",
                        onClick = {
                            showMoreGamesDialog = false
                            showTicTacToeDifficultyDialog = true
                        },
                        modifier = Modifier.testTag("tic_tac_toe_button")
                    )

                    Spacer(modifier = Modifier.height(18.dp))

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

    // Tic Tac Toe Difficulty Dialog
    if (showTicTacToeDifficultyDialog) {
        Dialog(onDismissRequest = { showTicTacToeDifficultyDialog = false }) {
            IosGlassCard(
                shape = RoundedCornerShape(28.dp),
                backgroundColor = Color(0xFF101935).copy(alpha = 0.96f),
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
                        text = "Difficulty",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val difficulties = listOf(
                        Pair(com.example.model.TicTacToeDifficulty.EASY, Color(0xFF10B981)),
                        Pair(com.example.model.TicTacToeDifficulty.MEDIUM, Color(0xFFFBBF24)),
                        Pair(com.example.model.TicTacToeDifficulty.HARD, Color(0xFFEF4444))
                    )

                    difficulties.forEach { (diff, color) ->
                        IosGlassCard(
                            shape = RoundedCornerShape(16.dp),
                            backgroundColor = Color(0xFF172346).copy(alpha = 0.85f),
                            borderColor = color.copy(alpha = 0.35f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
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
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Text(
                                    text = diff.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
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

@Composable
fun ThemePickerDialog(
    currentTheme: AppTheme,
    onThemeSelect: (AppTheme) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        IosGlassCard(
            shape = RoundedCornerShape(28.dp),
            backgroundColor = Color(0xFF0F1B38).copy(alpha = 0.98f),
            borderColor = Color.White.copy(alpha = 0.20f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Themes & Styles",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                AppTheme.entries.forEach { theme ->
                    val isSelected = theme == currentTheme
                    IosGlassCard(
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = theme.glassBackground,
                        borderColor = if (isSelected) theme.primaryAccent else Color.White.copy(alpha = 0.12f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .iosPressEffect { onThemeSelect(theme) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(theme.primaryAccent)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = theme.displayName,
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Text(
                                    text = "Active",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.primaryAccent
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .iosPressEffect { onDismiss() }
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Done",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
