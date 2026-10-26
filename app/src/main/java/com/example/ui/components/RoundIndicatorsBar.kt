package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ColorThemeConfig
import com.example.model.IndicatorShape
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RoundIndicatorsBar(
    currentRound: Int,
    totalRounds: Int,
    colorTheme: ColorThemeConfig,
    isTimerRunning: Boolean,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Futuristic Neon Pulse for active pip
    val infiniteTransition = rememberInfiniteTransition(label = "cyber_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isTimerRunning) 1.25f else 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Sci-Fi Telemetry Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "COMBAT_ROUNDS // PROGRESS",
                    color = Color(0xFF7A7A8E),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.2.sp
                )
            }
            Text(
                text = "[ ${String.format("%02d", currentRound)} / ${String.format("%02d", totalRounds)} ]",
                color = colorTheme.activeIndicatorColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Futuristic Indicators Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .testTag("round_indicators_container"),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (r in 1..totalRounds) {
                val isCompleted = r < currentRound
                val isActive = r == currentRound
                val isRemaining = r > currentRound

                val itemColor = when {
                    isActive -> colorTheme.activeIndicatorColor
                    isCompleted -> colorTheme.completedIndicatorColor
                    else -> colorTheme.remainingIndicatorColor
                }

                Box(
                    modifier = Modifier
                        .padding(horizontal = if (totalRounds > 12) 2.5.dp else 4.dp)
                        .then(if (isActive) Modifier.scale(pulseScale) else Modifier),
                    contentAlignment = Alignment.Center
                ) {
                    when (colorTheme.indicatorShape) {
                        IndicatorShape.HEXAGONS -> {
                            // Custom drawn Cyber Hexagon
                            Box(
                                modifier = Modifier.size(if (totalRounds > 12) 22.dp else 26.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.size(if (totalRounds > 12) 22.dp else 26.dp)) {
                                    val radius = size.minDimension / 2
                                    val center = Offset(size.width / 2, size.height / 2)
                                    val path = Path()
                                    for (i in 0 until 6) {
                                        val angle = Math.toRadians((60 * i - 30).toDouble())
                                        val x = center.x + (radius * cos(angle)).toFloat()
                                        val y = center.y + (radius * sin(angle)).toFloat()
                                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                    }
                                    path.close()

                                    // Fill
                                    drawPath(
                                        path = path,
                                        color = if (isActive) itemColor
                                        else if (isCompleted) itemColor.copy(alpha = 0.5f)
                                        else Color(0xFF14141E)
                                    )

                                    // Stroke
                                    drawPath(
                                        path = path,
                                        color = if (isActive) Color.White else itemColor,
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                                            width = if (isActive) 2.dp.toPx() else 1.dp.toPx()
                                        )
                                    )
                                }
                                if (isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(11.dp)
                                    )
                                } else if (isActive) {
                                    Text(
                                        text = "$r",
                                        color = Color.Black,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        IndicatorShape.ENERGY_BARS -> {
                            // Segmented Quantum Energy Bar
                            Box(
                                modifier = Modifier
                                    .width(if (totalRounds > 12) 16.dp else 22.dp)
                                    .height(if (isActive) 18.dp else 10.dp)
                                    .clip(CutCornerShape(topStart = 3.dp, bottomEnd = 3.dp))
                                    .background(itemColor)
                                    .then(
                                        if (isActive) Modifier.border(1.5.dp, Color.White, CutCornerShape(topStart = 3.dp, bottomEnd = 3.dp))
                                        else Modifier.border(0.5.dp, itemColor.copy(alpha = 0.6f), CutCornerShape(topStart = 3.dp, bottomEnd = 3.dp))
                                    )
                            )
                        }

                        IndicatorShape.HUD_TICKS -> {
                            // Vertical Sci-Fi HUD Ticks
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(if (isActive) 8.dp else 5.dp)
                                        .height(if (isActive) 22.dp else 14.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(itemColor)
                                        .then(
                                            if (isActive) Modifier.border(1.dp, Color.White, RoundedCornerShape(2.dp))
                                            else Modifier
                                        )
                                )
                                Text(
                                    text = "$r",
                                    color = if (isActive) itemColor else Color(0xFF555566),
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        IndicatorShape.GLOVES -> {
                            Box(
                                modifier = Modifier
                                    .size(if (totalRounds > 10) 26.dp else 30.dp)
                                    .clip(CutCornerShape(6.dp))
                                    .background(
                                        if (isActive) itemColor.copy(alpha = 0.35f)
                                        else if (isCompleted) itemColor.copy(alpha = 0.2f)
                                        else Color(0xFF161622)
                                    )
                                    .border(
                                        width = if (isActive) 1.5.dp else 1.dp,
                                        color = if (isActive) itemColor else itemColor.copy(alpha = 0.5f),
                                        shape = CutCornerShape(6.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Completed Round $r",
                                        tint = itemColor,
                                        modifier = Modifier.size(13.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.SportsMma,
                                        contentDescription = "Round $r",
                                        tint = itemColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        IndicatorShape.DOTS -> {
                            Box(
                                modifier = Modifier
                                    .size(if (isActive) 16.dp else 12.dp)
                                    .clip(CircleShape)
                                    .background(itemColor)
                                    .then(
                                        if (isActive) Modifier.border(2.dp, Color.White, CircleShape)
                                        else Modifier
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}
