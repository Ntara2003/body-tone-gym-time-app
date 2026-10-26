package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WorkoutPreset
import com.example.viewmodel.TimerUiState

@Composable
fun PresetsScreen(
    state: TimerUiState,
    onSelectPreset: (WorkoutPreset) -> Unit,
    onUpdateConfig: (Int, Int, Int, Int, Int) -> Unit,
    onSaveCustomPreset: (String, String) -> Unit,
    onDeleteCustomPreset: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSaveDialog by remember { mutableStateOf(false) }
    var presetNameInput by remember { mutableStateOf("") }
    var presetDescInput by remember { mutableStateOf("") }

    val allPresets = WorkoutPreset.DEFAULT_PRESETS + state.customPresets

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF09090D))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = Color(0xFFFF1838),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "WORKOUT BUILDER & PRESETS",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Fine-tune round times or pick professional boxing formats",
                        color = Color(0xFF888894),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Active Workout Customizer Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF13131A)),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF232330)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ACTIVE CONFIGURATION",
                        color = Color(0xFFFF1838),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Rounds Stepper
                    SettingRow(
                        title = "TOTAL ROUNDS",
                        valueText = "${state.totalRounds} Rds",
                        valueColor = state.colorTheme.workColor,
                        onDecrement = {
                            if (state.totalRounds > 1) {
                                onUpdateConfig(state.totalRounds - 1, state.roundDurationSec, state.restDurationSec, state.prepareDurationSec, state.warningSeconds)
                            }
                        },
                        onIncrement = {
                            if (state.totalRounds < 50) {
                                onUpdateConfig(state.totalRounds + 1, state.roundDurationSec, state.restDurationSec, state.prepareDurationSec, state.warningSeconds)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Round Duration Stepper
                    SettingRow(
                        title = "ROUND (WORK) TIME",
                        valueText = formatTime(state.roundDurationSec),
                        valueColor = state.colorTheme.workColor,
                        onDecrement = {
                            if (state.roundDurationSec > 15) {
                                onUpdateConfig(state.totalRounds, state.roundDurationSec - 15, state.restDurationSec, state.prepareDurationSec, state.warningSeconds)
                            }
                        },
                        onIncrement = {
                            if (state.roundDurationSec < 1200) {
                                onUpdateConfig(state.totalRounds, state.roundDurationSec + 15, state.restDurationSec, state.prepareDurationSec, state.warningSeconds)
                            }
                        }
                    )

                    // Quick duration buttons for rounds
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(60 to "1m", 120 to "2m", 180 to "3m", 300 to "5m").forEach { (sec, label) ->
                            val isSel = state.roundDurationSec == sec
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) state.colorTheme.workColor else Color(0xFF1C1C24))
                                    .clickable {
                                        onUpdateConfig(state.totalRounds, sec, state.restDurationSec, state.prepareDurationSec, state.warningSeconds)
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSel) Color.White else Color(0xFFA0A0AC),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Rest Duration Stepper
                    SettingRow(
                        title = "REST INTERVAL",
                        valueText = formatTime(state.restDurationSec),
                        valueColor = state.colorTheme.restColor,
                        onDecrement = {
                            if (state.restDurationSec > 10) {
                                onUpdateConfig(state.totalRounds, state.roundDurationSec, state.restDurationSec - 5, state.prepareDurationSec, state.warningSeconds)
                            }
                        },
                        onIncrement = {
                            if (state.restDurationSec < 300) {
                                onUpdateConfig(state.totalRounds, state.roundDurationSec, state.restDurationSec + 5, state.prepareDurationSec, state.warningSeconds)
                            }
                        }
                    )

                    // Quick rest buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(30 to "30s", 45 to "45s", 60 to "60s", 90 to "90s").forEach { (sec, label) ->
                            val isSel = state.restDurationSec == sec
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) state.colorTheme.restColor else Color(0xFF1C1C24))
                                    .clickable {
                                        onUpdateConfig(state.totalRounds, state.roundDurationSec, sec, state.prepareDurationSec, state.warningSeconds)
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSel) Color.Black else Color(0xFFA0A0AC),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Prepare & Warning Bells
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Prepare Time
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "PREPARE", color = Color(0xFF888894), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1A1A22))
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${state.prepareDurationSec}s",
                                    color = state.colorTheme.prepareColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF262632))
                                            .clickable {
                                                if (state.prepareDurationSec >= 5) {
                                                    onUpdateConfig(state.totalRounds, state.roundDurationSec, state.restDurationSec, state.prepareDurationSec - 5, state.warningSeconds)
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) { Text("-", color = Color.White, fontWeight = FontWeight.Bold) }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF262632))
                                            .clickable {
                                                if (state.prepareDurationSec <= 60) {
                                                    onUpdateConfig(state.totalRounds, state.roundDurationSec, state.restDurationSec, state.prepareDurationSec + 5, state.warningSeconds)
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) { Text("+", color = Color.White, fontWeight = FontWeight.Bold) }
                                }
                            }
                        }

                        // Warning Bell Time
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "WARNING CLAPPER", color = Color(0xFF888894), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1A1A22))
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${state.warningSeconds}s",
                                    color = state.colorTheme.warningColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF262632))
                                            .clickable {
                                                if (state.warningSeconds >= 5) {
                                                    onUpdateConfig(state.totalRounds, state.roundDurationSec, state.restDurationSec, state.prepareDurationSec, state.warningSeconds - 5)
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) { Text("-", color = Color.White, fontWeight = FontWeight.Bold) }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF262632))
                                            .clickable {
                                                if (state.warningSeconds <= 30) {
                                                    onUpdateConfig(state.totalRounds, state.roundDurationSec, state.restDurationSec, state.prepareDurationSec, state.warningSeconds + 5)
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) { Text("+", color = Color.White, fontWeight = FontWeight.Bold) }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Save as Custom Preset Button
                    Button(
                        onClick = {
                            presetNameInput = "My Workout (${state.totalRounds} Rds)"
                            presetDescInput = "${state.totalRounds} Rds | ${formatTime(state.roundDurationSec)} Work | ${formatTime(state.restDurationSec)} Rest"
                            showSaveDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_custom_preset_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22222E)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = null, tint = Color(0xFFFF1838), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Save as Custom Workout Preset", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }

        // Preset List Section
        item {
            Text(
                text = "GYM WORKOUT PRESETS",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(allPresets) { preset ->
            val isSelected = state.activePreset.id == preset.id
            val isCustom = preset.id.startsWith("custom_")

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectPreset(preset) }
                    .testTag("preset_card_${preset.id}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF1E1E28) else Color(0xFF13131A)
                ),
                shape = RoundedCornerShape(14.dp),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFF1838)) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22222C))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0xFFFF1838) else Color(0xFF20202A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isCustom) Icons.Default.FitnessCenter else Icons.Default.SportsMma,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else Color(0xFFA5A5B2),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = preset.name,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFFF1838).copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "ACTIVE", color = Color(0xFFFF5252), fontSize = 9.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "${preset.rounds} Rds • ${formatTime(preset.roundDurationSec)} Work • ${formatTime(preset.restDurationSec)} Rest",
                            color = Color(0xFFFFEA00),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )

                        Text(
                            text = preset.description,
                            color = Color(0xFF888894),
                            fontSize = 11.sp,
                            maxLines = 2
                        )
                    }

                    if (isCustom) {
                        IconButton(
                            onClick = { onDeleteCustomPreset(preset.id) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Custom Preset",
                                tint = Color(0xFFE57373),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color(0xFFFF1838),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Save Preset Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            containerColor = Color(0xFF181822),
            title = {
                Text(text = "Save Custom Preset", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = presetNameInput,
                        onValueChange = { presetNameInput = it },
                        label = { Text("Preset Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF1838),
                            unfocusedBorderColor = Color(0xFF33333E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = presetDescInput,
                        onValueChange = { presetDescInput = it },
                        label = { Text("Short Description") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF1838),
                            unfocusedBorderColor = Color(0xFF33333E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveCustomPreset(presetNameInput, presetDescInput)
                        showSaveDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1838))
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel", color = Color(0xFFA5A5B2))
                }
            }
        )
    }
}

@Composable
private fun SettingRow(
    title: String,
    valueText: String,
    valueColor: Color,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, color = Color(0xFFA0A0AC), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(
                text = valueText,
                color = valueColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF22222E))
                    .clickable { onDecrement() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "-", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF22222E))
                    .clickable { onIncrement() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "+", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun formatTime(sec: Int): String {
    val m = sec / 60
    val s = sec % 60
    return String.format("%02d:%02d", m, s)
}
