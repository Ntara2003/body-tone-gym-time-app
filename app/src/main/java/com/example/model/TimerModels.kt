package com.example.model

import androidx.compose.ui.graphics.Color

enum class TimerPhase(val displayName: String, val sciFiCode: String) {
    PREPARE("PREPARE PROTOCOL", "SYS.PREP_01"),
    WORK("ENGAGE TARGET", "COMBAT_ACTIVE"),
    REST("REST & RECHARGE", "ENERGY_RECOVERY"),
    FINISHED("MISSION ACCOMPLISHED", "SIMULATION_COMPLETE")
}

enum class TimerStatus {
    IDLE,
    RUNNING,
    PAUSED,
    FINISHED
}

enum class IndicatorShape(val displayName: String) {
    HEXAGONS("Cyber Hexagons"),
    ENERGY_BARS("Quantum Energy Bars"),
    HUD_TICKS("Sci-Fi HUD Ticks"),
    GLOVES("Cyber Boxing Gloves"),
    DOTS("Neon Pulsar Dots")
}

enum class SoundProfile(val displayName: String) {
    CYBER_SYNTH("Futuristic Cyber Sounds"),
    CLASSIC_BELL("Classic Gym Bell")
}

data class ColorThemeConfig(
    val name: String = "Body Tone Cyber Red",
    val workColorHex: Long = 0xFFFF003CL, // Futuristic Neon Laser Red
    val restColorHex: Long = 0xFF00F0FFL, // Cyber Hologram Cyan
    val prepareColorHex: Long = 0xFFFF9900L, // Plasma Amber
    val warningColorHex: Long = 0xFFFFEA00L, // High-Vis Laser Yellow
    val activeIndicatorHex: Long = 0xFFFF003CL,
    val completedIndicatorHex: Long = 0xFF00FF66L, // Neon Toxic Green
    val remainingIndicatorHex: Long = 0xFF1B1B26L, // Deep Cyber Carbon
    val indicatorShape: IndicatorShape = IndicatorShape.HEXAGONS,
    val soundProfile: SoundProfile = SoundProfile.CYBER_SYNTH
) {
    val workColor: Color get() = Color(workColorHex)
    val restColor: Color get() = Color(restColorHex)
    val prepareColor: Color get() = Color(prepareColorHex)
    val warningColor: Color get() = Color(warningColorHex)
    val activeIndicatorColor: Color get() = Color(activeIndicatorHex)
    val completedIndicatorColor: Color get() = Color(completedIndicatorHex)
    val remainingIndicatorColor: Color get() = Color(remainingIndicatorHex)

    companion object {
        val BodyToneCyberRed = ColorThemeConfig(
            name = "Body Tone Cyber Red",
            workColorHex = 0xFFFF003CL, // Neon Red
            restColorHex = 0xFF00F0FFL, // Cyber Cyan
            prepareColorHex = 0xFFFF9100L, // Plasma Orange
            warningColorHex = 0xFFFFEA00L, // Laser Yellow
            activeIndicatorHex = 0xFFFF003CL,
            completedIndicatorHex = 0xFF00FF66L,
            remainingIndicatorHex = 0xFF1A1A24L,
            indicatorShape = IndicatorShape.HEXAGONS,
            soundProfile = SoundProfile.CYBER_SYNTH
        )

        val BodyToneSignature = BodyToneCyberRed

        val CyberNeon2077 = ColorThemeConfig(
            name = "Cyberpunk 2077",
            workColorHex = 0xFFFFE600L, // Cyber Yellow
            restColorHex = 0xFFD000FFL, // Electric Purple
            prepareColorHex = 0xFF00F0FFL, // Hologram Blue
            warningColorHex = 0xFFFF0055L, // Neon Coral
            activeIndicatorHex = 0xFFFFE600L,
            completedIndicatorHex = 0xFF00F0FFL,
            remainingIndicatorHex = 0xFF211E2BL,
            indicatorShape = IndicatorShape.ENERGY_BARS,
            soundProfile = SoundProfile.CYBER_SYNTH
        )

        val QuantumVoid = ColorThemeConfig(
            name = "Quantum Void",
            workColorHex = 0xFF00FF66L, // Toxic Neon
            restColorHex = 0xFFFF007FL, // Laser Magenta
            prepareColorHex = 0xFF00F0FFL, // Cyan
            warningColorHex = 0xFFFF9900L, // Amber
            activeIndicatorHex = 0xFF00FF66L,
            completedIndicatorHex = 0xFF00F0FFL,
            remainingIndicatorHex = 0xFF14241CL,
            indicatorShape = IndicatorShape.HUD_TICKS,
            soundProfile = SoundProfile.CYBER_SYNTH
        )

        val SolarFlareHUD = ColorThemeConfig(
            name = "Solar Flare HUD",
            workColorHex = 0xFFFF6B00L, // Solar Orange
            restColorHex = 0xFF00D4FFL, // Ice Cyan
            prepareColorHex = 0xFFFFD700L, // Plasma Gold
            warningColorHex = 0xFFFF003CL, // Red Alert
            activeIndicatorHex = 0xFFFF6B00L,
            completedIndicatorHex = 0xFF00D4FFL,
            remainingIndicatorHex = 0xFF261D16L,
            indicatorShape = IndicatorShape.HEXAGONS,
            soundProfile = SoundProfile.CYBER_SYNTH
        )

        val MatrixProtocol = ColorThemeConfig(
            name = "Matrix Terminal",
            workColorHex = 0xFF00FF41L, // Matrix Green
            restColorHex = 0xFF00B4D8L, // Terminal Blue
            prepareColorHex = 0xFF70E000L, // Lime
            warningColorHex = 0xFFFFD000L, // Warning
            activeIndicatorHex = 0xFF00FF41L,
            completedIndicatorHex = 0xFF00B4D8L,
            remainingIndicatorHex = 0xFF102016L,
            indicatorShape = IndicatorShape.ENERGY_BARS,
            soundProfile = SoundProfile.CYBER_SYNTH
        )

        val PRESET_THEMES = listOf(
            BodyToneCyberRed,
            CyberNeon2077,
            QuantumVoid,
            SolarFlareHUD,
            MatrixProtocol
        )
    }
}

data class WorkoutPreset(
    val id: String,
    val name: String,
    val description: String,
    val rounds: Int,
    val roundDurationSec: Int,
    val restDurationSec: Int,
    val prepareDurationSec: Int = 10,
    val warningSeconds: Int = 10,
    val colorTheme: ColorThemeConfig = ColorThemeConfig.BodyToneCyberRed
) {
    companion object {
        val DEFAULT_PRESETS = listOf(
            WorkoutPreset(
                id = "pro_boxing",
                name = "Cyber Championship Bout",
                description = "12 rounds of 3:00 min high-velocity combat with 1:00 min recovery shields.",
                rounds = 12,
                roundDurationSec = 180,
                restDurationSec = 60,
                prepareDurationSec = 10,
                warningSeconds = 10,
                colorTheme = ColorThemeConfig.BodyToneCyberRed
            ),
            WorkoutPreset(
                id = "amateur_boxing",
                name = "Apex Speed Bout",
                description = "3 explosive 3:00 min rounds designed for maximum punch output and reaction velocity.",
                rounds = 3,
                roundDurationSec = 180,
                restDurationSec = 60,
                prepareDurationSec = 10,
                warningSeconds = 10,
                colorTheme = ColorThemeConfig.CyberNeon2077
            ),
            WorkoutPreset(
                id = "sparring",
                name = "Holographic Sparring",
                description = "6 rounds of 3:00 min tactical pacing with 45s rest to sharpen defensive reflexes.",
                rounds = 6,
                roundDurationSec = 180,
                restDurationSec = 45,
                prepareDurationSec = 10,
                warningSeconds = 10,
                colorTheme = ColorThemeConfig.SolarFlareHUD
            ),
            WorkoutPreset(
                id = "heavy_bag_hiit",
                name = "Plasma Heavy Bag Blast",
                description = "8 rounds of 2:00 min maximum power strikes with 30s quick-charge recovery.",
                rounds = 8,
                roundDurationSec = 120,
                restDurationSec = 30,
                prepareDurationSec = 10,
                warningSeconds = 10,
                colorTheme = ColorThemeConfig.QuantumVoid
            ),
            WorkoutPreset(
                id = "mma_title",
                name = "Titan MMA Championship",
                description = "5 rounds of 5:00 min endurance combat with 1:00 min rest and 15s warning pulse.",
                rounds = 5,
                roundDurationSec = 300,
                restDurationSec = 60,
                prepareDurationSec = 15,
                warningSeconds = 15,
                colorTheme = ColorThemeConfig.MatrixProtocol
            ),
            WorkoutPreset(
                id = "speed_bag_footwork",
                name = "Quantum Reflex & Agility",
                description = "10 rounds of 1:30 min rapid kinetic drills with 30s rest cycles.",
                rounds = 10,
                roundDurationSec = 90,
                restDurationSec = 30,
                prepareDurationSec = 5,
                warningSeconds = 10,
                colorTheme = ColorThemeConfig.BodyToneCyberRed
            )
        )
    }
}

data class WorkoutSessionRecord(
    val id: String = System.currentTimeMillis().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val workoutName: String,
    val roundsCompleted: Int,
    val totalRounds: Int,
    val totalDurationSeconds: Int,
    val caloriesBurned: Int
)
