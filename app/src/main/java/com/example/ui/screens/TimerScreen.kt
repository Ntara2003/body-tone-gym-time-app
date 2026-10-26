package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TimerPhase
import com.example.model.TimerStatus
import com.example.model.WorkoutPreset
import com.example.ui.components.BodyToneHeader
import com.example.ui.components.RoundIndicatorsBar
import com.example.ui.components.TimerCircularDisplay
import com.example.ui.components.TimerControls
import com.example.viewmodel.TimerUiState

@Composable
fun TimerScreen(
    state: TimerUiState,
    onStartResume: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSelectPreset: (WorkoutPreset) -> Unit,
    onToggleSound: () -> Unit,
    onToggleVibration: () -> Unit,
    onToggleKeepScreenOn: () -> Unit,
    onDismissFinishedDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allPresets = WorkoutPreset.DEFAULT_PRESETS + state.customPresets

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090910))
    ) {
        // Body Tone Gym Futuristic Header
        BodyToneHeader(
            isSoundEnabled = state.isSoundEnabled,
            onToggleSound = onToggleSound,
            isVibrationEnabled = state.isVibrationEnabled,
            onToggleVibration = onToggleVibration,
            isKeepScreenOn = state.isKeepScreenOn,
            onToggleKeepScreenOn = onToggleKeepScreenOn
        )

        // Futuristic Cyber Preset Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            allPresets.forEach { preset ->
                val isSelected = state.activePreset.id == preset.id
                Surface(
                    onClick = { onSelectPreset(preset) },
                    shape = CutCornerShape(6.dp),
                    color = if (isSelected) state.colorTheme.workColor.copy(alpha = 0.25f) else Color(0xFF12121A),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) state.colorTheme.workColor else Color(0xFF242436)
                    ),
                    modifier = Modifier.testTag("quick_preset_${preset.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CutCornerShape(2.dp))
                                .background(if (isSelected) state.colorTheme.workColor else Color(0xFF6B6B7C))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = preset.name,
                            color = if (isSelected) Color.White else Color(0xFFA5A5B8),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Round Indicators Bar with user's custom futuristic colors & shapes
        RoundIndicatorsBar(
            currentRound = state.currentRound,
            totalRounds = state.totalRounds,
            colorTheme = state.colorTheme,
            isTimerRunning = state.status == TimerStatus.RUNNING
        )

        Spacer(modifier = Modifier.weight(0.5f))

        // Giant Circular Futuristic Boxing Timer HUD
        TimerCircularDisplay(
            phase = state.phase,
            status = state.status,
            currentRound = state.currentRound,
            totalRounds = state.totalRounds,
            secondsRemaining = state.secondsRemaining,
            phaseTotalSeconds = state.phaseTotalSeconds,
            progress = state.progress,
            isWarningActive = state.isWarningActive,
            colorTheme = state.colorTheme
        )

        // Telemetry Readouts (Total Elapsed & Next Phase)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Total elapsed
            val elapsedMins = state.totalElapsedWorkoutSec / 60
            val elapsedSecs = state.totalElapsedWorkoutSec % 60
            val elapsedFormatted = String.format("%02d:%02d", elapsedMins, elapsedSecs)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color(0xFF00F0FF),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "TOTAL // $elapsedFormatted",
                    color = Color(0xFFA0A0B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Next Phase Preview
            val nextInfo = when (state.phase) {
                TimerPhase.PREPARE -> "NEXT // ROUND 01"
                TimerPhase.WORK -> if (state.currentRound < state.totalRounds) "NEXT // REST (${state.restDurationSec}s)" else "NEXT // VICTORY"
                TimerPhase.REST -> "NEXT // ROUND 0${state.currentRound + 1}"
                TimerPhase.FINISHED -> "STATUS // COMPLETED"
            }

            Text(
                text = nextInfo,
                color = if (state.phase == TimerPhase.WORK) state.colorTheme.restColor else state.colorTheme.workColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.weight(0.5f))

        // Glove-friendly Large Control Buttons
        TimerControls(
            status = state.status,
            colorTheme = state.colorTheme,
            onStartResume = onStartResume,
            onPause = onPause,
            onReset = onReset,
            onPrevious = onPrevious,
            onNext = onNext
        )

        Spacer(modifier = Modifier.height(8.dp))
    }

    // Workout Complete Dialog with Cyber Visuals
    if (state.showFinishedDialog && state.lastCompletedRecord != null) {
        val record = state.lastCompletedRecord
        AlertDialog(
            onDismissRequest = onDismissFinishedDialog,
            containerColor = Color(0xFF12121E),
            shape = CutCornerShape(16.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MISSION ACCOMPLISHED",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "BODY TONE GYM // CHAMPION",
                        color = Color(0xFFFF003C),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF181826)),
                        shape = CutCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2C40)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Workout:", color = Color(0xFFA0A0B8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                Text(record.workoutName, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Rounds Boxed:", color = Color(0xFFA0A0B8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                Text("${record.roundsCompleted} Rounds", color = Color(0xFF00F0FF), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Ring Time:", color = Color(0xFFA0A0B8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                val m = record.totalDurationSeconds / 60
                                val s = record.totalDurationSeconds % 60
                                Text("${m}m ${s}s", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Energy Expended:", color = Color(0xFFA0A0B8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                Text("~${record.caloriesBurned} kcal", color = Color(0xFFFF9900), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismissFinishedDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF003C)),
                    shape = CutCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "READY FOR NEXT BOUT",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
            }
        )
    }
}
