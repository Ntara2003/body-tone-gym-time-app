package com.example

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.ColorStudioScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.PresetsScreen
import com.example.ui.screens.TimerScreen
import com.example.ui.theme.BodyToneBoxingTheme
import com.example.viewmodel.BoxingTimerViewModel

enum class AppNavTab(val label: String, val icon: ImageVector) {
    TIMER("Ring Timer", Icons.Default.SportsMma),
    COLOR_STUDIO("Colors & Styles", Icons.Default.Palette),
    PRESETS("Workouts", Icons.Default.Tune),
    HISTORY("Gym Log", Icons.Default.History)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BodyToneBoxingTheme {
                val viewModel: BoxingTimerViewModel = viewModel()
                val state by viewModel.uiState.collectAsState()

                // Keep screen awake while training
                DisposableEffect(state.isKeepScreenOn) {
                    if (state.isKeepScreenOn) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    } else {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }
                    onDispose {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }
                }

                var currentTab by remember { mutableStateOf(AppNavTab.TIMER) }

                // BackHandler so back button on secondary screens returns to the main Timer screen
                if (currentTab != AppNavTab.TIMER) {
                    BackHandler {
                        currentTab = AppNavTab.TIMER
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color(0xFF09090D),
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .testTag("main_bottom_nav_bar"),
                            containerColor = Color(0xFF0F0F14),
                            tonalElevation = 8.dp
                        ) {
                            AppNavTab.values().forEach { tab ->
                                val isSelected = currentTab == tab
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { currentTab = tab },
                                    icon = {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = tab.label,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.label,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = Color.White,
                                        indicatorColor = Color(0xFFFF1838),
                                        unselectedIconColor = Color(0xFF757582),
                                        unselectedTextColor = Color(0xFF757582)
                                    ),
                                    modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentTab) {
                            AppNavTab.TIMER -> {
                                TimerScreen(
                                    state = state,
                                    onStartResume = viewModel::startOrResume,
                                    onPause = viewModel::pause,
                                    onReset = viewModel::reset,
                                    onPrevious = viewModel::skipToPreviousPhase,
                                    onNext = viewModel::skipToNextPhase,
                                    onSelectPreset = viewModel::selectPreset,
                                    onToggleSound = viewModel::toggleSound,
                                    onToggleVibration = viewModel::toggleVibration,
                                    onToggleKeepScreenOn = viewModel::toggleKeepScreenOn,
                                    onDismissFinishedDialog = viewModel::dismissFinishedDialog
                                )
                            }

                            AppNavTab.COLOR_STUDIO -> {
                                ColorStudioScreen(
                                    currentTheme = state.colorTheme,
                                    onApplyPreset = viewModel::applyPresetTheme,
                                    onUpdateTargetColor = viewModel::updateTargetColor,
                                    onUpdateShape = viewModel::updateIndicatorShape,
                                    onUpdateSoundProfile = viewModel::updateSoundProfile,
                                    onResetDefaults = viewModel::resetToDefaultColors
                                )
                            }

                            AppNavTab.PRESETS -> {
                                PresetsScreen(
                                    state = state,
                                    onSelectPreset = { preset ->
                                        viewModel.selectPreset(preset)
                                        currentTab = AppNavTab.TIMER
                                    },
                                    onUpdateConfig = viewModel::updateConfigValues,
                                    onSaveCustomPreset = viewModel::saveAsCustomPreset,
                                    onDeleteCustomPreset = viewModel::deleteCustomPreset
                                )
                            }

                            AppNavTab.HISTORY -> {
                                HistoryScreen(
                                    history = state.history,
                                    onClearHistory = viewModel::clearHistory
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
