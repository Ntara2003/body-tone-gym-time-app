package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.TimerPreferences
import com.example.model.ColorThemeConfig
import com.example.model.IndicatorShape
import com.example.model.TimerPhase
import com.example.model.TimerStatus
import com.example.model.WorkoutPreset
import com.example.model.WorkoutSessionRecord
import com.example.sound.BoxingSoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class TimerUiState(
    val phase: TimerPhase = TimerPhase.PREPARE,
    val status: TimerStatus = TimerStatus.IDLE,
    val currentRound: Int = 1,
    val totalRounds: Int = 12,
    val roundDurationSec: Int = 180,
    val restDurationSec: Int = 60,
    val prepareDurationSec: Int = 10,
    val warningSeconds: Int = 10,
    val secondsRemaining: Int = 10,
    val phaseTotalSeconds: Int = 10,
    val totalElapsedWorkoutSec: Int = 0,
    val isWarningActive: Boolean = false,
    val colorTheme: ColorThemeConfig = ColorThemeConfig.BodyToneSignature,
    val activePreset: WorkoutPreset = WorkoutPreset.DEFAULT_PRESETS.first(),
    val customPresets: List<WorkoutPreset> = emptyList(),
    val isSoundEnabled: Boolean = true,
    val isVibrationEnabled: Boolean = true,
    val isKeepScreenOn: Boolean = true,
    val history: List<WorkoutSessionRecord> = emptyList(),
    val showFinishedDialog: Boolean = false,
    val lastCompletedRecord: WorkoutSessionRecord? = null
) {
    val progress: Float
        get() = if (phaseTotalSeconds > 0) {
            (secondsRemaining.toFloat() / phaseTotalSeconds.toFloat()).coerceIn(0f, 1f)
        } else 0f
}

enum class ColorCustomizationTarget(val title: String) {
    WORK("Round (Work) Interval"),
    REST("Rest Interval"),
    PREPARE("Prepare Interval"),
    WARNING("10-Second Warning"),
    INDICATOR_ACTIVE("Active Round Pip"),
    INDICATOR_COMPLETED("Completed Round Pip"),
    INDICATOR_REMAINING("Remaining Round Pip")
}

class BoxingTimerViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = TimerPreferences(application)
    private val soundManager = BoxingSoundManager(application)

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadInitialState()
    }

    private fun loadInitialState() {
        val theme = preferences.loadColorTheme()
        val customPresets = preferences.loadCustomPresets()
        val allPresets = WorkoutPreset.DEFAULT_PRESETS + customPresets
        val savedPresetId = preferences.selectedPresetId
        val activePreset = allPresets.find { it.id == savedPresetId } ?: WorkoutPreset.DEFAULT_PRESETS.first()
        val history = preferences.loadHistory()

        soundManager.soundEnabled = preferences.isSoundEnabled
        soundManager.vibrationEnabled = preferences.isVibrationEnabled

        _uiState.update { state ->
            state.copy(
                phase = if (activePreset.prepareDurationSec > 0) TimerPhase.PREPARE else TimerPhase.WORK,
                status = TimerStatus.IDLE,
                currentRound = 1,
                totalRounds = activePreset.rounds,
                roundDurationSec = activePreset.roundDurationSec,
                restDurationSec = activePreset.restDurationSec,
                prepareDurationSec = activePreset.prepareDurationSec,
                warningSeconds = activePreset.warningSeconds,
                secondsRemaining = if (activePreset.prepareDurationSec > 0) activePreset.prepareDurationSec else activePreset.roundDurationSec,
                phaseTotalSeconds = if (activePreset.prepareDurationSec > 0) activePreset.prepareDurationSec else activePreset.roundDurationSec,
                totalElapsedWorkoutSec = 0,
                isWarningActive = false,
                colorTheme = theme,
                activePreset = activePreset,
                customPresets = customPresets,
                isSoundEnabled = preferences.isSoundEnabled,
                isVibrationEnabled = preferences.isVibrationEnabled,
                isKeepScreenOn = preferences.isKeepScreenOn,
                history = history
            )
        }
    }

    fun startOrResume() {
        if (_uiState.value.status == TimerStatus.RUNNING) return

        if (_uiState.value.status == TimerStatus.FINISHED) {
            reset()
        }

        _uiState.update { it.copy(status = TimerStatus.RUNNING) }

        // Start bell if starting round 1 from beginning
        if (_uiState.value.phase == TimerPhase.WORK &&
            _uiState.value.secondsRemaining == _uiState.value.phaseTotalSeconds &&
            _uiState.value.currentRound == 1 &&
            _uiState.value.totalElapsedWorkoutSec == 0
        ) {
            soundManager.playBoxingBell(triple = false)
        }

        startTicker()
    }

    fun pause() {
        _uiState.update { it.copy(status = TimerStatus.PAUSED) }
        timerJob?.cancel()
    }

    fun reset() {
        timerJob?.cancel()
        val preset = _uiState.value.activePreset
        val hasPrep = preset.prepareDurationSec > 0
        val initialSec = if (hasPrep) preset.prepareDurationSec else preset.roundDurationSec

        _uiState.update { state ->
            state.copy(
                phase = if (hasPrep) TimerPhase.PREPARE else TimerPhase.WORK,
                status = TimerStatus.IDLE,
                currentRound = 1,
                secondsRemaining = initialSec,
                phaseTotalSeconds = initialSec,
                totalElapsedWorkoutSec = 0,
                isWarningActive = false,
                showFinishedDialog = false
            )
        }
    }

    fun skipToNextPhase() {
        transitionToNextPhase(manual = true)
    }

    fun skipToPreviousPhase() {
        val s = _uiState.value
        when (s.phase) {
            TimerPhase.REST -> {
                // Return to current round work
                setPhase(TimerPhase.WORK, s.currentRound, s.roundDurationSec)
            }
            TimerPhase.WORK -> {
                if (s.currentRound > 1) {
                    // Back to previous rest
                    setPhase(TimerPhase.REST, s.currentRound - 1, s.restDurationSec)
                } else {
                    reset()
                }
            }
            TimerPhase.PREPARE -> {
                reset()
            }
            TimerPhase.FINISHED -> {
                reset()
            }
        }
    }

    private fun setPhase(phase: TimerPhase, round: Int, seconds: Int) {
        _uiState.update { state ->
            state.copy(
                phase = phase,
                currentRound = round,
                secondsRemaining = seconds,
                phaseTotalSeconds = seconds,
                isWarningActive = false
            )
        }
    }

    private fun startTicker() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && _uiState.value.status == TimerStatus.RUNNING) {
                delay(1000L)
                tick()
            }
        }
    }

    private fun tick() {
        val s = _uiState.value
        val newElapsed = s.totalElapsedWorkoutSec + 1
        val newRemaining = s.secondsRemaining - 1

        if (newRemaining <= 0) {
            transitionToNextPhase(manual = false)
            _uiState.update { it.copy(totalElapsedWorkoutSec = newElapsed) }
            return
        }

        // Sound & visual alerts during countdown
        var warningNow = s.isWarningActive
        when (s.phase) {
            TimerPhase.PREPARE -> {
                if (newRemaining in 1..3) {
                    soundManager.playCountdownBeep(isHigh = false)
                }
            }
            TimerPhase.WORK -> {
                if (newRemaining == s.warningSeconds) {
                    warningNow = true
                    soundManager.playWarningClapper()
                } else if (newRemaining < s.warningSeconds) {
                    warningNow = true
                    if (newRemaining in 1..3) {
                        soundManager.playCountdownBeep(isHigh = false)
                    }
                }
            }
            TimerPhase.REST -> {
                if (newRemaining in 1..3) {
                    soundManager.playCountdownBeep(isHigh = false)
                }
            }
            TimerPhase.FINISHED -> {}
        }

        _uiState.update {
            it.copy(
                secondsRemaining = newRemaining,
                totalElapsedWorkoutSec = newElapsed,
                isWarningActive = warningNow
            )
        }
    }

    private fun transitionToNextPhase(manual: Boolean) {
        val s = _uiState.value

        when (s.phase) {
            TimerPhase.PREPARE -> {
                // Transition into Round 1
                soundManager.playBoxingBell(triple = false)
                setPhase(TimerPhase.WORK, round = 1, seconds = s.roundDurationSec)
            }
            TimerPhase.WORK -> {
                if (s.currentRound < s.totalRounds) {
                    // Go to REST
                    soundManager.playRestBuzzer()
                    setPhase(TimerPhase.REST, round = s.currentRound, seconds = s.restDurationSec)
                } else {
                    // Final round finished!
                    finishWorkout()
                }
            }
            TimerPhase.REST -> {
                // Next Round start
                val nextRound = s.currentRound + 1
                soundManager.playBoxingBell(triple = false)
                setPhase(TimerPhase.WORK, round = nextRound, seconds = s.roundDurationSec)
            }
            TimerPhase.FINISHED -> {
                reset()
            }
        }
    }

    private fun finishWorkout() {
        timerJob?.cancel()
        soundManager.playBoxingBell(triple = true)

        val s = _uiState.value
        // Estimate calories burned: ~11 METs for boxing training
        // calories = MET * weight(kg, avg 75) * hours ~= 11 * 75 * (duration/3600) ~= (duration / 60) * 12 kcal
        val durationMins = (s.totalElapsedWorkoutSec / 60).coerceAtLeast(1)
        val calories = (durationMins * 12.5f).toInt()

        val record = WorkoutSessionRecord(
            workoutName = s.activePreset.name,
            roundsCompleted = s.totalRounds,
            totalRounds = s.totalRounds,
            totalDurationSeconds = s.totalElapsedWorkoutSec,
            caloriesBurned = calories
        )

        preferences.addHistoryRecord(record)
        val updatedHistory = preferences.loadHistory()

        _uiState.update {
            it.copy(
                phase = TimerPhase.FINISHED,
                status = TimerStatus.FINISHED,
                secondsRemaining = 0,
                isWarningActive = false,
                showFinishedDialog = true,
                lastCompletedRecord = record,
                history = updatedHistory
            )
        }
    }

    fun selectPreset(preset: WorkoutPreset) {
        timerJob?.cancel()
        preferences.selectedPresetId = preset.id
        val hasPrep = preset.prepareDurationSec > 0
        val initialSec = if (hasPrep) preset.prepareDurationSec else preset.roundDurationSec

        _uiState.update { state ->
            state.copy(
                activePreset = preset,
                phase = if (hasPrep) TimerPhase.PREPARE else TimerPhase.WORK,
                status = TimerStatus.IDLE,
                currentRound = 1,
                totalRounds = preset.rounds,
                roundDurationSec = preset.roundDurationSec,
                restDurationSec = preset.restDurationSec,
                prepareDurationSec = preset.prepareDurationSec,
                warningSeconds = preset.warningSeconds,
                secondsRemaining = initialSec,
                phaseTotalSeconds = initialSec,
                totalElapsedWorkoutSec = 0,
                isWarningActive = false,
                showFinishedDialog = false
            )
        }
    }

    fun updateConfigValues(
        rounds: Int,
        roundSec: Int,
        restSec: Int,
        prepSec: Int,
        warnSec: Int
    ) {
        val currentPreset = _uiState.value.activePreset
        val updated = currentPreset.copy(
            rounds = rounds.coerceIn(1, 50),
            roundDurationSec = roundSec.coerceIn(10, 3600),
            restDurationSec = restSec.coerceIn(5, 600),
            prepareDurationSec = prepSec.coerceIn(0, 120),
            warningSeconds = warnSec.coerceIn(0, 60)
        )
        selectPreset(updated)
    }

    fun saveAsCustomPreset(name: String, desc: String) {
        val s = _uiState.value
        val newPreset = WorkoutPreset(
            id = "custom_${System.currentTimeMillis()}",
            name = name.ifBlank { "Custom ${s.totalRounds} Rounds" },
            description = desc.ifBlank { "${s.totalRounds} Rds | ${formatTimeShort(s.roundDurationSec)} Work | ${formatTimeShort(s.restDurationSec)} Rest" },
            rounds = s.totalRounds,
            roundDurationSec = s.roundDurationSec,
            restDurationSec = s.restDurationSec,
            prepareDurationSec = s.prepareDurationSec,
            warningSeconds = s.warningSeconds,
            colorTheme = s.colorTheme
        )
        val updatedCustom = _uiState.value.customPresets + newPreset
        preferences.saveCustomPresets(updatedCustom)
        _uiState.update { it.copy(customPresets = updatedCustom) }
        selectPreset(newPreset)
    }

    fun deleteCustomPreset(presetId: String) {
        val updated = _uiState.value.customPresets.filterNot { it.id == presetId }
        preferences.saveCustomPresets(updated)
        _uiState.update { it.copy(customPresets = updated) }
        if (_uiState.value.activePreset.id == presetId) {
            selectPreset(WorkoutPreset.DEFAULT_PRESETS.first())
        }
    }

    // Color Scheme Customization
    fun applyPresetTheme(presetTheme: ColorThemeConfig) {
        preferences.saveColorTheme(presetTheme)
        soundManager.soundProfile = presetTheme.soundProfile
        _uiState.update { it.copy(colorTheme = presetTheme) }
    }

    fun updateSoundProfile(profile: com.example.model.SoundProfile) {
        val updated = _uiState.value.colorTheme.copy(soundProfile = profile)
        preferences.saveColorTheme(updated)
        soundManager.soundProfile = profile
        _uiState.update { it.copy(colorTheme = updated) }
    }

    fun updateTargetColor(target: ColorCustomizationTarget, hex: Long) {
        val current = _uiState.value.colorTheme
        val updated = when (target) {
            ColorCustomizationTarget.WORK -> current.copy(name = "Custom Theme", workColorHex = hex)
            ColorCustomizationTarget.REST -> current.copy(name = "Custom Theme", restColorHex = hex)
            ColorCustomizationTarget.PREPARE -> current.copy(name = "Custom Theme", prepareColorHex = hex)
            ColorCustomizationTarget.WARNING -> current.copy(name = "Custom Theme", warningColorHex = hex)
            ColorCustomizationTarget.INDICATOR_ACTIVE -> current.copy(name = "Custom Theme", activeIndicatorHex = hex)
            ColorCustomizationTarget.INDICATOR_COMPLETED -> current.copy(name = "Custom Theme", completedIndicatorHex = hex)
            ColorCustomizationTarget.INDICATOR_REMAINING -> current.copy(name = "Custom Theme", remainingIndicatorHex = hex)
        }
        preferences.saveColorTheme(updated)
        _uiState.update { it.copy(colorTheme = updated) }
    }

    fun updateIndicatorShape(shape: IndicatorShape) {
        val updated = _uiState.value.colorTheme.copy(indicatorShape = shape)
        preferences.saveColorTheme(updated)
        _uiState.update { it.copy(colorTheme = updated) }
    }

    fun resetToDefaultColors() {
        applyPresetTheme(ColorThemeConfig.BodyToneSignature)
    }

    // Settings
    fun toggleSound() {
        val newVal = !_uiState.value.isSoundEnabled
        preferences.isSoundEnabled = newVal
        soundManager.soundEnabled = newVal
        _uiState.update { it.copy(isSoundEnabled = newVal) }
    }

    fun toggleVibration() {
        val newVal = !_uiState.value.isVibrationEnabled
        preferences.isVibrationEnabled = newVal
        soundManager.vibrationEnabled = newVal
        _uiState.update { it.copy(isVibrationEnabled = newVal) }
    }

    fun toggleKeepScreenOn() {
        val newVal = !_uiState.value.isKeepScreenOn
        preferences.isKeepScreenOn = newVal
        _uiState.update { it.copy(isKeepScreenOn = newVal) }
    }

    fun dismissFinishedDialog() {
        _uiState.update { it.copy(showFinishedDialog = false) }
    }

    fun clearHistory() {
        preferences.clearHistory()
        _uiState.update { it.copy(history = emptyList()) }
    }

    private fun formatTimeShort(sec: Int): String {
        val m = sec / 60
        val s = sec % 60
        return if (m > 0 && s == 0) "${m}m" else if (m > 0) "${m}m ${s}s" else "${s}s"
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
