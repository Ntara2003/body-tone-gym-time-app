package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@Composable
fun BodyToneHeader(
    isSoundEnabled: Boolean,
    onToggleSound: () -> Unit,
    isVibrationEnabled: Boolean,
    onToggleVibration: () -> Unit,
    isKeepScreenOn: Boolean,
    onToggleKeepScreenOn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFF0C0C14),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                listOf(
                    Color(0xFFFF003C).copy(alpha = 0.6f),
                    Color(0xFF00F0FF).copy(alpha = 0.6f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Gym Logo & Futuristic Branding
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CutCornerShape(8.dp))
                            .background(Color.Black)
                            .border(1.5.dp, Color(0xFFFF003C), CutCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.body_tone_logo),
                            contentDescription = "Body Tone Gym Logo",
                            modifier = Modifier.size(50.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "BODY TONE",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.8.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "GYM",
                                color = Color(0xFFFF003C),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.8.sp
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(CutCornerShape(3.dp))
                                    .background(Color(0xFFFF003C).copy(alpha = 0.25f))
                                    .border(0.5.dp, Color(0xFFFF003C), CutCornerShape(3.dp))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "EST 2024",
                                    color = Color(0xFFFF5252),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CYBER TIMER // v2.0",
                                color = Color(0xFF00F0FF),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                // Futuristic Cyber Toggles
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Sound Toggle
                    IconButton(
                        onClick = onToggleSound,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CutCornerShape(6.dp))
                            .background(if (isSoundEnabled) Color(0xFF1C1A2E) else Color(0xFF13131D))
                            .border(1.dp, if (isSoundEnabled) Color(0xFFFFEA00) else Color(0xFF262638), CutCornerShape(6.dp))
                            .testTag("sound_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isSoundEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                            contentDescription = if (isSoundEnabled) "Sound On" else "Sound Muted",
                            tint = if (isSoundEnabled) Color(0xFFFFEA00) else Color(0xFF6B6B7C),
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Vibration Toggle
                    IconButton(
                        onClick = onToggleVibration,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CutCornerShape(6.dp))
                            .background(if (isVibrationEnabled) Color(0xFF12222E) else Color(0xFF13131D))
                            .border(1.dp, if (isVibrationEnabled) Color(0xFF00F0FF) else Color(0xFF262638), CutCornerShape(6.dp))
                            .testTag("vibration_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = if (isVibrationEnabled) "Vibration On" else "Vibration Off",
                            tint = if (isVibrationEnabled) Color(0xFF00F0FF) else Color(0xFF6B6B7C),
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Keep Screen On Toggle
                    IconButton(
                        onClick = onToggleKeepScreenOn,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CutCornerShape(6.dp))
                            .background(if (isKeepScreenOn) Color(0xFF12281E) else Color(0xFF13131D))
                            .border(1.dp, if (isKeepScreenOn) Color(0xFF00FF66) else Color(0xFF262638), CutCornerShape(6.dp))
                            .testTag("screen_awake_button")
                    ) {
                        Icon(
                            imageVector = if (isKeepScreenOn) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (isKeepScreenOn) "Screen Awake On" else "Screen Awake Off",
                            tint = if (isKeepScreenOn) Color(0xFF00FF66) else Color(0xFF6B6B7C),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}
