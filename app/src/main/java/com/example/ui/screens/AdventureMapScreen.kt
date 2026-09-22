package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AdventureLevel
import com.example.ui.theme.IosCircularButton
import com.example.ui.theme.IosGlassCard
import com.example.ui.theme.iosPressEffect
import com.example.viewmodel.GameViewModel

@Composable
fun AdventureMapScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val unlockedLevel = viewModel.preferences.unlockedLevel
    val gridState = rememberLazyGridState()

    // Auto-scroll to current unlocked level
    LaunchedEffect(unlockedLevel) {
        val targetIndex = (unlockedLevel - 1).coerceIn(0, AdventureLevel.TOTAL_LEVELS - 1)
        gridState.animateScrollToItem(targetIndex)
    }

    // Calculate total stars earned
    var totalStars = 0
    for (i in 1..AdventureLevel.TOTAL_LEVELS) {
        totalStars += viewModel.preferences.getLevelStars(i)
    }

    Box(
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // Top Bar (iOS Header)
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
                    modifier = Modifier.testTag("adventure_map_back_button")
                )

                Text(
                    text = "ADVENTURE",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )

                // Total Stars Capsule
                IosGlassCard(
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = Color(0xFF141F3C).copy(alpha = 0.85f),
                    borderColor = Color(0xFFFBBF24).copy(alpha = 0.35f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Stars",
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$totalStars / 90",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Progress Banner
            IosGlassCard(
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color(0xFF101935).copy(alpha = 0.85f),
                borderColor = Color.White.copy(alpha = 0.12f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CURRENT STAGE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "Stage $unlockedLevel of 30",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2563EB).copy(alpha = 0.25f))
                            .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${((unlockedLevel.toFloat() / 30f) * 100).toInt()}% Done",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Level Grid (3 columns of iOS squircle nodes)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                state = gridState,
                contentPadding = PaddingValues(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(AdventureLevel.TOTAL_LEVELS) { index ->
                    val levelNum = index + 1
                    val isUnlocked = levelNum <= unlockedLevel
                    val isCurrent = levelNum == unlockedLevel
                    val stars = viewModel.preferences.getLevelStars(levelNum)

                    IosGlassCard(
                        shape = RoundedCornerShape(22.dp),
                        backgroundColor = when {
                            isCurrent -> Color(0xFF1D4ED8).copy(alpha = 0.75f)
                            isUnlocked -> Color(0xFF142042).copy(alpha = 0.85f)
                            else -> Color(0xFF0D1426).copy(alpha = 0.55f)
                        },
                        borderColor = when {
                            isCurrent -> Color(0xFF60A5FA).copy(alpha = 0.8f)
                            isUnlocked -> Color.White.copy(alpha = 0.15f)
                            else -> Color.White.copy(alpha = 0.05f)
                        },
                        modifier = Modifier
                            .aspectRatio(0.95f)
                            .iosPressEffect(enabled = isUnlocked) {
                                if (isUnlocked) {
                                    viewModel.startAdventureLevel(levelNum)
                                }
                            }
                            .testTag("level_node_$levelNum")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (isUnlocked) {
                                Text(
                                    text = "$levelNum",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    for (s in 1..3) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = if (s <= stars) Color(0xFFFBBF24) else Color(0xFF334155),
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = Color(0xFF475569),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$levelNum",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF475569)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
