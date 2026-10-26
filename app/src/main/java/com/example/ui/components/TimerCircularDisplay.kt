package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ColorThemeConfig
import com.example.model.TimerPhase
import com.example.model.TimerStatus
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TimerCircularDisplay(
    phase: TimerPhase,
    status: TimerStatus,
    currentRound: Int,
    totalRounds: Int,
    secondsRemaining: Int,
    phaseTotalSeconds: Int,
    progress: Float,
    isWarningActive: Boolean,
    colorTheme: ColorThemeConfig,
    modifier: Modifier = Modifier
) {
    val targetPrimaryColor = when {
        isWarningActive -> colorTheme.warningColor
        phase == TimerPhase.WORK -> colorTheme.workColor
        phase == TimerPhase.REST -> colorTheme.restColor
        phase == TimerPhase.PREPARE -> colorTheme.prepareColor
        else -> colorTheme.activeIndicatorColor
    }

    val animatedColor by animateColorAsState(
        targetValue = targetPrimaryColor,
        animationSpec = tween(350),
        label = "phase_color"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "progress_arc"
    )

    // Futuristic continuous HUD rotating ticks
    val infiniteTransition = rememberInfiniteTransition(label = "hud_rotation")
    val hudRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "hud_rot"
    )

    // Pulse when running
    val corePulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isWarningActive) 200 else 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "core_pulse"
    )

    // Warning alert strobe
    val warningStrobe by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(220),
            repeatMode = RepeatMode.Reverse
        ),
        label = "strobe"
    )

    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        val diameter = maxWidth.coerceAtMost(330.dp)

        Box(
            modifier = Modifier
                .size(diameter)
                .aspectRatio(1f)
                .testTag("circular_timer_container"),
            contentAlignment = Alignment.Center
        ) {
            // Futuristic Sci-Fi Canvas: Telemetry Ticks, Hex Brackets, Glow Arc
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val baseRadius = (size.minDimension / 2) - 12.dp.toPx()

                // 1. Outer Telemetry Dial Ticks
                val tickCount = 60
                val tickLength = 6.dp.toPx()
                for (i in 0 until tickCount) {
                    val angleDeg = (i * (360f / tickCount) + hudRotation) % 360f
                    val angleRad = Math.toRadians(angleDeg.toDouble())
                    val isMajor = i % 5 == 0

                    val rStart = baseRadius + (if (isMajor) 4.dp.toPx() else 0f)
                    val rEnd = rStart + (if (isMajor) tickLength * 1.5f else tickLength)

                    val startX = center.x + (rStart * cos(angleRad)).toFloat()
                    val startY = center.y + (rStart * sin(angleRad)).toFloat()
                    val endX = center.x + (rEnd * cos(angleRad)).toFloat()
                    val endY = center.y + (rEnd * sin(angleRad)).toFloat()

                    drawLine(
                        color = if (isMajor) animatedColor.copy(alpha = 0.55f) else Color(0xFF262638),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx()
                    )
                }

                // 2. Track Background
                drawCircle(
                    color = Color(0xFF101018),
                    radius = baseRadius - 8.dp.toPx(),
                    center = center,
                    style = Stroke(width = 12.dp.toPx())
                )

                // 3. Cyber Ambient Reactor Glow
                val glowAlpha = if (isWarningActive) warningStrobe * 0.22f else 0.08f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            animatedColor.copy(alpha = glowAlpha),
                            animatedColor.copy(alpha = glowAlpha * 0.4f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = baseRadius
                    ),
                    center = center
                )

                // 4. Glowing Cyber Arc
                val strokeWidth = 14.dp.toPx()
                val arcRadius = baseRadius - 8.dp.toPx()
                val sweepAngle = animatedProgress * 360f

                if (sweepAngle > 0.5f) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                animatedColor.copy(alpha = 0.4f),
                                animatedColor,
                                if (isWarningActive) Color.White else animatedColor
                            ),
                            center = center
                        ),
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
                        size = Size(arcRadius * 2, arcRadius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                // 5. Four Cyber HUD Crosshair Corner Brackets
                val bracketRadius = baseRadius - 20.dp.toPx()
                val bracketOffset = 28.dp.toPx()
                // Top-left
                drawLine(
                    color = animatedColor.copy(alpha = 0.4f),
                    start = Offset(center.x - bracketRadius, center.y - bracketRadius + bracketOffset),
                    end = Offset(center.x - bracketRadius, center.y - bracketRadius),
                    strokeWidth = 2.dp.toPx()
                )
                drawLine(
                    color = animatedColor.copy(alpha = 0.4f),
                    start = Offset(center.x - bracketRadius, center.y - bracketRadius),
                    end = Offset(center.x - bracketRadius + bracketOffset, center.y - bracketRadius),
                    strokeWidth = 2.dp.toPx()
                )
                // Top-right
                drawLine(
                    color = animatedColor.copy(alpha = 0.4f),
                    start = Offset(center.x + bracketRadius - bracketOffset, center.y - bracketRadius),
                    end = Offset(center.x + bracketRadius, center.y - bracketRadius),
                    strokeWidth = 2.dp.toPx()
                )
                drawLine(
                    color = animatedColor.copy(alpha = 0.4f),
                    start = Offset(center.x + bracketRadius, center.y - bracketRadius),
                    end = Offset(center.x + bracketRadius, center.y - bracketRadius + bracketOffset),
                    strokeWidth = 2.dp.toPx()
                )
                // Bottom-left
                drawLine(
                    color = animatedColor.copy(alpha = 0.4f),
                    start = Offset(center.x - bracketRadius, center.y + bracketRadius - bracketOffset),
                    end = Offset(center.x - bracketRadius, center.y + bracketRadius),
                    strokeWidth = 2.dp.toPx()
                )
                drawLine(
                    color = animatedColor.copy(alpha = 0.4f),
                    start = Offset(center.x - bracketRadius, center.y + bracketRadius),
                    end = Offset(center.x - bracketRadius + bracketOffset, center.y + bracketRadius),
                    strokeWidth = 2.dp.toPx()
                )
                // Bottom-right
                drawLine(
                    color = animatedColor.copy(alpha = 0.4f),
                    start = Offset(center.x + bracketRadius - bracketOffset, center.y + bracketRadius),
                    end = Offset(center.x + bracketRadius, center.y + bracketRadius),
                    strokeWidth = 2.dp.toPx()
                )
                drawLine(
                    color = animatedColor.copy(alpha = 0.4f),
                    start = Offset(center.x + bracketRadius, center.y + bracketRadius),
                    end = Offset(center.x + bracketRadius, center.y + bracketRadius - bracketOffset),
                    strokeWidth = 2.dp.toPx()
                )
            }

            // Central Telemetry HUD Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp)
            ) {
                // Futuristic Cyber Badge with angled cut corners
                val badgeText = when (phase) {
                    TimerPhase.PREPARE -> "SYS // PREPARE"
                    TimerPhase.WORK -> if (isWarningActive) "ALERT // FINAL 10S" else "ROUND 0$currentRound // COMBAT"
                    TimerPhase.REST -> "SHIELD // RECHARGE"
                    TimerPhase.FINISHED -> "MISSION // COMPLETE"
                }

                val phaseIcon = when (phase) {
                    TimerPhase.PREPARE -> Icons.Default.HourglassBottom
                    TimerPhase.WORK -> if (isWarningActive) Icons.Default.Warning else Icons.Default.Bolt
                    TimerPhase.REST -> Icons.Default.Security
                    TimerPhase.FINISHED -> Icons.Default.SportsMma
                }

                Box(
                    modifier = Modifier
                        .clip(CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp))
                        .background(animatedColor.copy(alpha = if (isWarningActive) warningStrobe else 0.18f))
                        .border(1.5.dp, animatedColor, CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp))
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = phaseIcon,
                            contentDescription = null,
                            tint = if (isWarningActive) Color.Black else animatedColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = badgeText,
                            color = if (isWarningActive) Color.Black else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.2.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // High-Tech Cyber Digital Clock
                Text(
                    text = timeFormatted,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = if (isWarningActive) animatedColor else Color.White,
                    letterSpacing = (-1.5).sp,
                    modifier = Modifier.testTag("timer_countdown_display")
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Sub-Telemetry info: Round indicator & Cyber BPM
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(animatedColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ROUND $currentRound OF $totalRounds",
                        color = Color(0xFFA5A5B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.2.sp
                    )
                }

                // If paused, show Sci-Fi PAUSED Banner
                if (status == TimerStatus.PAUSED) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(CutCornerShape(4.dp))
                            .background(Color(0xFFFFB300))
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "[ SYSTEM PAUSED ]",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}
