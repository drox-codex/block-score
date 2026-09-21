package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IosCircularButton
import com.example.ui.theme.IosGlassCard
import com.example.viewmodel.GameViewModel

@Composable
fun AchievementsScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val achievements = viewModel.getAchievements()
    val unlockedCount = achievements.count { it.isUnlocked }

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
                    modifier = Modifier.testTag("achievements_back_button")
                )

                Text(
                    text = "ACHIEVEMENTS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )

                // Unlocked badge
                IosGlassCard(
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = Color(0xFF141F3C).copy(alpha = 0.85f),
                    borderColor = Color(0xFFFBBF24).copy(alpha = 0.35f)
                ) {
                    Text(
                        text = "$unlockedCount / ${achievements.size}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFBBF24),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Achievements Grid (Apple Game Center Style)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("achievements_list")
            ) {
                items(achievements) { item ->
                    val isUnlocked = item.isUnlocked

                    IosGlassCard(
                        shape = RoundedCornerShape(22.dp),
                        backgroundColor = if (isUnlocked) Color(0xFF16244C).copy(alpha = 0.85f) else Color(0xFF0F172E).copy(alpha = 0.60f),
                        borderColor = if (isUnlocked) Color(0xFF38BDF8).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.08f),
                        modifier = Modifier.height(180.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(11.dp))
                                        .background(if (isUnlocked) Color(0xFFFBBF24).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.06f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = if (isUnlocked) Color(0xFFFBBF24) else Color(0xFF64748B),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                if (isUnlocked) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Unlocked",
                                            tint = Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }

                            Column {
                                Text(
                                    text = item.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = item.description,
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8),
                                    lineHeight = 15.sp,
                                    maxLines = 2
                                )
                            }

                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isUnlocked) "Completed" else "${item.currentProgress}/${item.targetProgress}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUnlocked) Color(0xFF10B981) else Color(0xFF94A3B8)
                                    )
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                                LinearProgressIndicator(
                                    progress = { item.progressFraction },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(5.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = if (isUnlocked) Color(0xFF10B981) else Color(0xFF2563EB),
                                    trackColor = Color(0xFF1E293B),
                                    strokeCap = StrokeCap.Round
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
