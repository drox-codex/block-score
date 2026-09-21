package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IosCircularButton
import com.example.ui.theme.IosGlassCard
import com.example.ui.theme.iosPressEffect
import com.example.viewmodel.GameViewModel

@Composable
fun SettingsScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    var soundEnabled by remember { mutableStateOf(viewModel.preferences.soundEnabled) }
    var musicEnabled by remember { mutableStateOf(viewModel.preferences.musicEnabled) }
    var vibrationEnabled by remember { mutableStateOf(viewModel.preferences.vibrationEnabled) }
    var showResetDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

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
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IosCircularButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    onClick = { viewModel.navigateTo(GameViewModel.Screen.MAIN_MENU) },
                    modifier = Modifier.testTag("settings_back_button")
                )

                Text(
                    text = "SETTINGS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.size(44.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Section 1: Audio & Haptics (iOS Grouped Style)
            Text(
                text = "AUDIO & HAPTICS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )

            IosGlassCard(
                shape = RoundedCornerShape(22.dp),
                backgroundColor = Color(0xFF101935).copy(alpha = 0.90f),
                borderColor = Color.White.copy(alpha = 0.12f),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("audio_settings_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    IosSettingsToggleRow(
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        iconBg = Color(0xFF0284C7),
                        title = "Sound Effects",
                        checked = soundEnabled,
                        onCheckedChange = {
                            soundEnabled = it
                            viewModel.preferences.soundEnabled = it
                        },
                        tag = "sound_toggle"
                    )

                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.08f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
                    )

                    IosSettingsToggleRow(
                        icon = Icons.Default.MusicNote,
                        iconBg = Color(0xFFEA580C),
                        title = "Ambient Music",
                        checked = musicEnabled,
                        onCheckedChange = {
                            musicEnabled = it
                            viewModel.preferences.musicEnabled = it
                            viewModel.soundManager.updateAmbientMusic()
                        },
                        tag = "music_toggle"
                    )

                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.08f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
                    )

                    IosSettingsToggleRow(
                        icon = Icons.Default.Vibration,
                        iconBg = Color(0xFF10B981),
                        title = "Haptic Feedback",
                        checked = vibrationEnabled,
                        onCheckedChange = {
                            vibrationEnabled = it
                            viewModel.preferences.vibrationEnabled = it
                        },
                        tag = "vibration_toggle"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 2: Statistics (Apple Health / Activity style)
            Text(
                text = "STATISTICS & RECORDS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )

            IosGlassCard(
                shape = RoundedCornerShape(22.dp),
                backgroundColor = Color(0xFF101935).copy(alpha = 0.90f),
                borderColor = Color.White.copy(alpha = 0.12f),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("stats_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Best Score",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "${viewModel.preferences.bestScore}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Highest Combo",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "x${viewModel.preferences.maxCombo}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFBBF24)
                            )
                        }
                    }

                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.08f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Total Lines Cleared",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "${viewModel.preferences.totalLines}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Total Blocks Placed",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "${viewModel.preferences.totalBlocks}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 3: Data Management
            Text(
                text = "DATA MANAGEMENT",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )

            IosGlassCard(
                shape = RoundedCornerShape(22.dp),
                backgroundColor = Color(0xFF101935).copy(alpha = 0.90f),
                borderColor = Color.White.copy(alpha = 0.12f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Reset all scores, unlocked adventure stages, and One Line puzzle levels back to default.",
                        fontSize = 13.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .iosPressEffect { showResetDialog = true }
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFEF4444).copy(alpha = 0.18f))
                            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                            .testTag("reset_progress_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = null,
                                tint = Color(0xFFF87171),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Reset Game Progress",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF87171)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 4: About
            IosGlassCard(
                shape = RoundedCornerShape(22.dp),
                backgroundColor = Color(0xFF101935).copy(alpha = 0.90f),
                borderColor = Color.White.copy(alpha = 0.12f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF6366F1)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "BLOCK SCORE",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Version 1.2.0 • Offline Ready",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }

        // Reset Confirmation Dialog (iOS Alert Style)
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = {
                    Text(
                        text = "Reset All Progress?",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                },
                text = {
                    Text(
                        text = "This will permanently erase your high score, adventure map progress, and One Line puzzle records. This action cannot be undone.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.resetProgress()
                            showResetDialog = false
                        }
                    ) {
                        Text(
                            text = "Reset Everything",
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showResetDialog = false }
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                containerColor = Color(0xFF152042),
                shape = RoundedCornerShape(26.dp)
            )
        }
    }
}

@Composable
private fun IosSettingsToggleRow(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(9.dp))
                .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(19.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(tag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF34D399),
                uncheckedThumbColor = Color.White.copy(alpha = 0.8f),
                uncheckedTrackColor = Color.White.copy(alpha = 0.15f)
            )
        )
    }
}
