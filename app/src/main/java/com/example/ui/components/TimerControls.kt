package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.ColorThemeConfig
import com.example.model.TimerStatus

@Composable
fun TimerControls(
    status: TimerStatus,
    colorTheme: ColorThemeConfig,
    onStartResume: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isRunning = status == TimerStatus.RUNNING

    // Animated glow pulse on reactor button
    val infiniteTransition = rememberInfiniteTransition(label = "reactor_pulse")
    val reactorScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isRunning) 1.06f else 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isRunning) 900 else 1800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "reactor_scale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Reset Button with Cyber Cut Corners
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CutCornerShape(10.dp))
                .background(Color(0xFF13131F))
                .border(1.dp, Color(0xFF28283E), CutCornerShape(10.dp))
                .clickable { onReset() }
                .testTag("reset_timer_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Reset Workout",
                tint = Color(0xFFA5A5BC),
                modifier = Modifier.size(24.dp)
            )
        }

        // Previous Phase Button
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CutCornerShape(10.dp))
                .background(Color(0xFF13131F))
                .border(1.dp, Color(0xFF28283E), CutCornerShape(10.dp))
                .clickable { onPrevious() }
                .testTag("previous_phase_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "Previous Phase",
                tint = Color(0xFFA5A5BC),
                modifier = Modifier.size(26.dp)
            )
        }

        // Giant Futuristic Reactor Main Action Button
        val buttonGradient = if (isRunning) {
            Brush.radialGradient(
                listOf(
                    Color(0xFFFFEA00),
                    Color(0xFFFF9100)
                )
            )
        } else {
            Brush.radialGradient(
                listOf(
                    colorTheme.workColor,
                    colorTheme.workColor.copy(alpha = 0.75f)
                )
            )
        }

        Box(
            modifier = Modifier
                .scale(reactorScale)
                .size(86.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    spotColor = if (isRunning) Color(0xFFFFEA00) else colorTheme.workColor
                )
                .clip(CircleShape)
                .background(buttonGradient)
                .border(
                    width = 2.5.dp,
                    brush = Brush.sweepGradient(
                        listOf(
                            Color.White.copy(alpha = 0.8f),
                            colorTheme.workColor,
                            Color.White.copy(alpha = 0.8f)
                        )
                    ),
                    shape = CircleShape
                )
                .clickable {
                    if (isRunning) onPause() else onStartResume()
                }
                .testTag("start_pause_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isRunning) "Pause Timer" else "Start Timer",
                tint = if (isRunning) Color.Black else Color.White,
                modifier = Modifier.size(46.dp)
            )
        }

        // Next Phase Button
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CutCornerShape(10.dp))
                .background(Color(0xFF13131F))
                .border(1.dp, Color(0xFF28283E), CutCornerShape(10.dp))
                .clickable { onNext() }
                .testTag("next_phase_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Next Phase",
                tint = Color(0xFFA5A5BC),
                modifier = Modifier.size(26.dp)
            )
        }
    }
}
