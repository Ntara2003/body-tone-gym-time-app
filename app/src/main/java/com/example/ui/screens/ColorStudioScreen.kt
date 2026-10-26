package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.model.ColorThemeConfig
import com.example.model.IndicatorShape
import com.example.model.SoundProfile
import com.example.ui.components.RoundIndicatorsBar
import com.example.viewmodel.ColorCustomizationTarget

@Composable
fun ColorStudioScreen(
    currentTheme: ColorThemeConfig,
    onApplyPreset: (ColorThemeConfig) -> Unit,
    onUpdateTargetColor: (ColorCustomizationTarget, Long) -> Unit,
    onUpdateShape: (IndicatorShape) -> Unit,
    onUpdateSoundProfile: (SoundProfile) -> Unit,
    onResetDefaults: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTarget by remember { mutableStateOf(ColorCustomizationTarget.WORK) }
    val scrollState = rememberScrollState()

    // Futuristic Neon Palette
    val curatedPalette = listOf(
        0xFFFF003CL, // Cyber Laser Red
        0xFFFF3366L, // Neon Pink
        0xFFFF5400L, // Plasma Orange
        0xFFFF9900L, // Warm Amber
        0xFFFFEA00L, // Laser Yellow
        0xFFFFE600L, // Cyberpunk Yellow
        0xFF00FF66L, // Toxic Neon Green
        0xFF00FF41L, // Matrix Green
        0xFF00F0FFL, // Cyber Hologram Cyan
        0xFF00D4FFL, // Electric Blue
        0xFF2979FFL, // Deep Ring Blue
        0xFFD000FFL, // Laser Violet
        0xFFFF007FL, // Neon Magenta
        0xFFFFFFFFL, // Quantum White
        0xFF7A7A8EL, // Steel Gray
        0xFF1A1A26L  // Carbon Black
    )

    val currentHex = when (selectedTarget) {
        ColorCustomizationTarget.WORK -> currentTheme.workColorHex
        ColorCustomizationTarget.REST -> currentTheme.restColorHex
        ColorCustomizationTarget.PREPARE -> currentTheme.prepareColorHex
        ColorCustomizationTarget.WARNING -> currentTheme.warningColorHex
        ColorCustomizationTarget.INDICATOR_ACTIVE -> currentTheme.activeIndicatorHex
        ColorCustomizationTarget.INDICATOR_COMPLETED -> currentTheme.completedIndicatorHex
        ColorCustomizationTarget.INDICATOR_REMAINING -> currentTheme.remainingIndicatorHex
    }

    val currentColor = Color(currentHex)
    val r = ((currentHex shr 16) and 0xFF).toFloat() / 255f
    val g = ((currentHex shr 8) and 0xFF).toFloat() / 255f
    val b = (currentHex and 0xFF).toFloat() / 255f

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090910))
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Section Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = null,
                tint = Color(0xFFFF003C),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "CYBER COLOR STUDIO",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "CUSTOMIZE // ROUND INDICATORS & REST INTERVALS",
                    color = Color(0xFF00F0FF),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }
        }

        // Live Futuristic HUD Preview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF101018)),
            shape = CutCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                Brush.horizontalGradient(listOf(currentTheme.workColor, currentTheme.restColor))
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HUD // LIVE SIMULATION",
                        color = Color(0xFF8888A0),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "[ ${currentTheme.name} ]",
                        color = currentTheme.workColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mini Dual Interval preview
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Work
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CutCornerShape(8.dp))
                                .background(currentTheme.workColor.copy(alpha = 0.2f))
                                .border(2.dp, currentTheme.workColor, CutCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "03:00",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ROUND (WORK)",
                            color = currentTheme.workColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Rest
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CutCornerShape(8.dp))
                                .background(currentTheme.restColor.copy(alpha = 0.2f))
                                .border(2.dp, currentTheme.restColor, CutCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "01:00",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "REST INTERVAL",
                            color = currentTheme.restColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Warning
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CutCornerShape(8.dp))
                                .background(currentTheme.warningColor.copy(alpha = 0.2f))
                                .border(2.dp, currentTheme.warningColor, CutCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "00:10",
                                color = currentTheme.warningColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "10s WARNING",
                            color = currentTheme.warningColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Round Indicators Sample
                RoundIndicatorsBar(
                    currentRound = 3,
                    totalRounds = 6,
                    colorTheme = currentTheme,
                    isTimerRunning = true
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Futuristic Audio Engine Selector
        Text(
            text = "AUDIO ENGINE SYNTHESIZER",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SoundProfile.values().forEach { profile ->
                val isSelected = currentTheme.soundProfile == profile
                Surface(
                    onClick = { onUpdateSoundProfile(profile) },
                    modifier = Modifier.weight(1f),
                    shape = CutCornerShape(6.dp),
                    color = if (isSelected) Color(0xFF1B2232) else Color(0xFF12121A),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) Color(0xFF00F0FF) else Color(0xFF262638)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = if (isSelected) Color(0xFF00F0FF) else Color(0xFF6B6B78),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = profile.displayName,
                            color = if (isSelected) Color.White else Color(0xFF888898),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Futuristic Preset Themes Carousel
        Text(
            text = "FUTURISTIC THEME MATRICES",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ColorThemeConfig.PRESET_THEMES.forEach { preset ->
                val isSelected = currentTheme.name == preset.name
                Surface(
                    onClick = { onApplyPreset(preset) },
                    shape = CutCornerShape(8.dp),
                    color = if (isSelected) Color(0xFF1E1E2C) else Color(0xFF111118),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isSelected) preset.workColor else Color(0xFF242434)
                    ),
                    modifier = Modifier.testTag("preset_theme_${preset.name}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CutCornerShape(3.dp))
                                .background(preset.workColor)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CutCornerShape(3.dp))
                                .background(preset.restColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = preset.name,
                            color = if (isSelected) Color.White else Color(0xFFA5A5B8),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Indicator Shape Selector
        Text(
            text = "ROUND INDICATOR SHAPE & HUD STYLE",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IndicatorShape.values().forEach { shape ->
                val isSelected = currentTheme.indicatorShape == shape
                Surface(
                    onClick = { onUpdateShape(shape) },
                    shape = CutCornerShape(6.dp),
                    color = if (isSelected) currentTheme.workColor.copy(alpha = 0.25f) else Color(0xFF12121A),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) currentTheme.workColor else Color(0xFF262638)
                    ),
                    modifier = Modifier.testTag("shape_option_${shape.name}")
                ) {
                    Text(
                        text = shape.displayName,
                        color = if (isSelected) Color.White else Color(0xFF888898),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Target Element Selector
        Text(
            text = "TARGET ELEMENT CUSTOMIZATION",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ColorCustomizationTarget.values().forEach { target ->
                val isSelected = selectedTarget == target
                val targetColor = when (target) {
                    ColorCustomizationTarget.WORK -> currentTheme.workColor
                    ColorCustomizationTarget.REST -> currentTheme.restColor
                    ColorCustomizationTarget.PREPARE -> currentTheme.prepareColor
                    ColorCustomizationTarget.WARNING -> currentTheme.warningColor
                    ColorCustomizationTarget.INDICATOR_ACTIVE -> currentTheme.activeIndicatorColor
                    ColorCustomizationTarget.INDICATOR_COMPLETED -> currentTheme.completedIndicatorColor
                    ColorCustomizationTarget.INDICATOR_REMAINING -> currentTheme.remainingIndicatorColor
                }

                Surface(
                    onClick = { selectedTarget = target },
                    shape = CutCornerShape(4.dp),
                    color = if (isSelected) targetColor.copy(alpha = 0.3f) else Color(0xFF13131C),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) targetColor else Color(0xFF242434)
                    ),
                    modifier = Modifier.testTag("color_target_${target.name}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CutCornerShape(2.dp))
                                .background(targetColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = target.title,
                            color = if (isSelected) Color.White else Color(0xFFA5A5B8),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Futuristic Palette Swatches
        Text(
            text = "COLOR MATRIX: ${selectedTarget.title.uppercase()}",
            color = Color(0xFFA5A5B8),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (row in curatedPalette.chunked(8)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    row.forEach { hex ->
                        val isChosen = currentHex == hex
                        val swatchColor = Color(hex)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CutCornerShape(6.dp))
                                .background(swatchColor)
                                .clickable {
                                    onUpdateTargetColor(selectedTarget, hex)
                                }
                                .then(
                                    if (isChosen) Modifier.border(2.5.dp, Color.White, CutCornerShape(6.dp))
                                    else Modifier.border(1.dp, Color(0xFF282838), CutCornerShape(6.dp))
                                )
                                .testTag("swatch_${String.format("%08X", hex)}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isChosen) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = if (hex == 0xFFFFFFFFL || hex == 0xFFFFEA00L || hex == 0xFFFFE600L) Color.Black else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // RGB Synthesizer Sliders
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF101018)),
            shape = CutCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222232))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RGB COLOR MIXER",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = String.format("#%06X", (currentHex and 0xFFFFFFL)),
                        color = currentColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "R", color = Color(0xFFFF003C), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, modifier = Modifier.width(18.dp))
                    Slider(
                        value = r,
                        onValueChange = { newR ->
                            val newHex = (0xFFL shl 24) or
                                    ((newR * 255).toInt().toLong() shl 16) or
                                    ((g * 255).toInt().toLong() shl 8) or
                                    (b * 255).toInt().toLong()
                            onUpdateTargetColor(selectedTarget, newHex)
                        },
                        colors = SliderDefaults.colors(thumbColor = Color(0xFFFF003C), activeTrackColor = Color(0xFFFF003C)),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "G", color = Color(0xFF00FF66), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, modifier = Modifier.width(18.dp))
                    Slider(
                        value = g,
                        onValueChange = { newG ->
                            val newHex = (0xFFL shl 24) or
                                    ((r * 255).toInt().toLong() shl 16) or
                                    ((newG * 255).toInt().toLong() shl 8) or
                                    (b * 255).toInt().toLong()
                            onUpdateTargetColor(selectedTarget, newHex)
                        },
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF00FF66), activeTrackColor = Color(0xFF00FF66)),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "B", color = Color(0xFF00F0FF), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, modifier = Modifier.width(18.dp))
                    Slider(
                        value = b,
                        onValueChange = { newB ->
                            val newHex = (0xFFL shl 24) or
                                    ((r * 255).toInt().toLong() shl 16) or
                                    ((g * 255).toInt().toLong() shl 8) or
                                    ((newB * 255).toInt().toLong())
                            onUpdateTargetColor(selectedTarget, newHex)
                        },
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF00F0FF), activeTrackColor = Color(0xFF00F0FF)),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reset to Body Tone Gym Default button
        OutlinedButton(
            onClick = onResetDefaults,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reset_color_defaults_button"),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF003C)),
            shape = CutCornerShape(8.dp)
        ) {
            Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                "Reset to Body Tone Cyber Red Default",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
