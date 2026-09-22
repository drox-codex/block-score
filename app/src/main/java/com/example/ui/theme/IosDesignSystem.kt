package com.example.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * iOS-style spring press scale effect.
 * Smoothly scales the component down on press and bounces back on release.
 */
fun Modifier.iosPressEffect(
    pressedScale: Float = 0.95f,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    if (!enabled) return@composed this

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "iosPressScale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(enabled) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                isPressed = true
                val up = waitForUpOrCancellation()
                isPressed = false
                if (up != null && onClick != null) {
                    onClick()
                }
            }
        }
}

/**
 * Apple-style Frosted Glass Surface Card with subtle specular top highlight
 * and fine translucent border.
 */
@Composable
fun IosGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp),
    backgroundColor: Color = Color(0xFF131D38).copy(alpha = 0.75f),
    borderColor: Color = Color.White.copy(alpha = 0.12f),
    shadowElevation: Dp = 8.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = shadowElevation,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.5f),
                spotColor = Color.Black.copy(alpha = 0.5f)
            )
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        borderColor.copy(alpha = 0.22f),
                        borderColor.copy(alpha = 0.06f)
                    )
                ),
                shape = shape
            )
    ) {
        // Specular ambient shine along the top edge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.20f),
                            Color.Transparent
                        )
                    )
                )
        )
        content()
    }
}

/**
 * Apple-style circular frosted glass button with spring tap feedback.
 */
@Composable
fun IosCircularButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = Color.White,
    size: Dp = 44.dp,
    iconSize: Dp = 22.dp,
    backgroundColor: Color = Color(0xFF1C2746).copy(alpha = 0.85f),
    badgeCount: Int? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .iosPressEffect(pressedScale = 0.90f, onClick = onClick)
            .shadow(6.dp, CircleShape)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(iconSize)
        )
        if (badgeCount != null && badgeCount > 0) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$badgeCount",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * iOS-styled Arcade Game Banner Card with icon, title, subtitle, badges, and chevron.
 */
@Composable
fun IosGameActionCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailingBadge: String? = null,
    testTag: String = ""
) {
    IosGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .iosPressEffect(pressedScale = 0.96f, onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        backgroundColor = Color(0xFF141F3C).copy(alpha = 0.82f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = if (subtitle.isNullOrBlank()) 14.dp else 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Pill Container with vibrant glowing gradient
            Box(
                modifier = Modifier
                    .size(if (subtitle.isNullOrBlank()) 46.dp else 52.dp)
                    .shadow(8.dp, RoundedCornerShape(14.dp), ambientColor = accentColor, spotColor = accentColor)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                accentColor,
                                accentColor.copy(alpha = 0.75f)
                            )
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(if (subtitle.isNullOrBlank()) 24.dp else 28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.2.sp
                    )
                    if (trailingBadge != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(accentColor.copy(alpha = 0.2f))
                                .border(0.8.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = trailingBadge,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = accentColor
                            )
                        }
                    }
                }
                if (!subtitle.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Apple SF-Style Chevron
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.35f),
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

/**
 * Aqua Water Glass Container with fluid refraction glow and specular border.
 */
@Composable
fun IosWaterGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    accentColor: Color = Color(0xFF00F0FF),
    backgroundColor: Color = Color(0xFF071E3D).copy(alpha = 0.72f),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(12.dp, shape, ambientColor = accentColor.copy(alpha = 0.2f), spotColor = accentColor.copy(alpha = 0.25f))
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = 0.88f)
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.55f),
                        Color.White.copy(alpha = 0.15f),
                        accentColor.copy(alpha = 0.10f)
                    )
                ),
                shape = shape
            )
    ) {
        // Specular water sheen line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            accentColor.copy(alpha = 0.6f),
                            Color.White.copy(alpha = 0.8f),
                            accentColor.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    )
                )
        )
        content()
    }
}

/**
 * Floating Animated Combo Badge (e.g., x2 COMBO, UNSTOPPABLE!) with fluid pop-in.
 */
@Composable
fun IosFloatingComboBadge(
    combo: Int,
    modifier: Modifier = Modifier
) {
    if (combo < 2) return

    val (comboText, comboColor) = when {
        combo >= 5 -> "UNSTOPPABLE! x$combo" to Color(0xFFEF4444)
        combo >= 4 -> "INCREDIBLE! x$combo" to Color(0xFFEC4899)
        combo >= 3 -> "SUPER COMBO! x$combo" to Color(0xFFF59E0B)
        else -> "COMBO x$combo" to Color(0xFF00F0FF)
    }

    Box(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(16.dp), ambientColor = comboColor, spotColor = comboColor)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0A192F).copy(alpha = 0.90f))
            .border(1.2.dp, comboColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = comboText,
            color = comboColor,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            letterSpacing = 0.6.sp
        )
    }
}

