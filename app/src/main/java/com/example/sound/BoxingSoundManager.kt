package com.example.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.model.SoundProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * High-performance sound and vibration manager for Body Tone Gym boxing timer.
 * Features both Classic Boxing Bell audio and Futuristic Sci-Fi Cyber Synth effects.
 */
class BoxingSoundManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default)
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var soundEnabled: Boolean = true
    var vibrationEnabled: Boolean = true
    var soundProfile: SoundProfile = SoundProfile.CYBER_SYNTH

    /**
     * Start/End round bell
     */
    fun playBoxingBell(triple: Boolean = false) {
        if (vibrationEnabled) {
            vibratePattern(longArrayOf(0, 160, 80, 160))
        }
        if (!soundEnabled) return

        scope.launch {
            try {
                if (soundProfile == SoundProfile.CYBER_SYNTH) {
                    if (triple) {
                        playCyberEnergyBell()
                        kotlinx.coroutines.delay(260)
                        playCyberEnergyBell()
                        kotlinx.coroutines.delay(260)
                        playCyberEnergyBell()
                    } else {
                        playCyberEnergyBell()
                    }
                } else {
                    if (triple) {
                        playClassicBellAudio()
                        kotlinx.coroutines.delay(280)
                        playClassicBellAudio()
                        kotlinx.coroutines.delay(280)
                        playClassicBellAudio()
                    } else {
                        playClassicBellAudio()
                    }
                }
            } catch (_: Exception) {}
        }
    }

    /**
     * 10-second warning alert
     */
    fun playWarningClapper() {
        if (vibrationEnabled) {
            vibratePattern(longArrayOf(0, 70, 50, 70))
        }
        if (!soundEnabled) return

        scope.launch {
            try {
                if (soundProfile == SoundProfile.CYBER_SYNTH) {
                    playCyberLaserAlert()
                    kotlinx.coroutines.delay(100)
                    playCyberLaserAlert()
                } else {
                    playClassicWoodClapperAudio()
                    kotlinx.coroutines.delay(120)
                    playClassicWoodClapperAudio()
                }
            } catch (_: Exception) {}
        }
    }

    /**
     * Countdown beep for 3, 2, 1
     */
    fun playCountdownBeep(isHigh: Boolean = false) {
        if (vibrationEnabled) {
            vibratePattern(longArrayOf(0, if (isHigh) 100 else 50))
        }
        if (!soundEnabled) return

        scope.launch {
            try {
                if (soundProfile == SoundProfile.CYBER_SYNTH) {
                    val freq = if (isHigh) 1560.0 else 980.0
                    playTone(freq, 110, 0.7f)
                } else {
                    val freq = if (isHigh) 1320.0 else 880.0
                    playTone(freq, 120, 0.75f)
                }
            } catch (_: Exception) {}
        }
    }

    /**
     * Rest interval start sound
     */
    fun playRestBuzzer() {
        if (vibrationEnabled) {
            vibratePattern(longArrayOf(0, 220))
        }
        if (!soundEnabled) return

        scope.launch {
            try {
                if (soundProfile == SoundProfile.CYBER_SYNTH) {
                    playCyberShieldRecharge()
                } else {
                    playTone(550.0, 450, 0.8f)
                }
            } catch (_: Exception) {}
        }
    }

    /**
     * Synthesizes Futuristic Cyber Holographic Gong:
     * High crystal energy resonance with shimmering harmonic overtones
     */
    private fun playCyberEnergyBell() {
        val sampleRate = 44100
        val durationMs = 1100
        val numSamples = (durationMs * sampleRate / 1000)
        val audioData = ShortArray(numSamples)

        val f1 = 1396.91 // F6
        val f2 = 2093.00 // C7
        val f3 = 2793.83 // F7
        val f4 = 3729.31 // Bb7

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val decay = exp(-3.8 * t)
            val strike = if (i < 200) (i / 200.0) else 1.0

            val wave = 0.50 * sin(2.0 * PI * f1 * t) +
                    0.25 * sin(2.0 * PI * f2 * t) +
                    0.15 * sin(2.0 * PI * f3 * t) +
                    0.10 * sin(2.0 * PI * f4 * t)

            val sample = (wave * strike * decay * Short.MAX_VALUE * 0.95)
            audioData[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        playRawPcm(audioData, sampleRate)
    }

    /**
     * Synthesizes Futuristic Cyber Laser Pulse Warning
     */
    private fun playCyberLaserAlert() {
        val sampleRate = 44100
        val durationMs = 90
        val numSamples = (durationMs * sampleRate / 1000)
        val audioData = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            // Downward laser pitch chirp
            val freq = 1800.0 - (1000.0 * (i.toDouble() / numSamples))
            val decay = exp(-20.0 * t)
            val sample = (sin(2.0 * PI * freq * t) * decay * Short.MAX_VALUE * 0.80)
            audioData[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        playRawPcm(audioData, sampleRate)
    }

    /**
     * Synthesizes Futuristic Shield Recharge Hum for Rest Phase
     */
    private fun playCyberShieldRecharge() {
        val sampleRate = 44100
        val durationMs = 500
        val numSamples = (durationMs * sampleRate / 1000)
        val audioData = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            // Upward shield power hum
            val freq = 420.0 + (320.0 * (i.toDouble() / numSamples))
            val envelope = when {
                i < 300 -> (i / 300.0)
                i > numSamples - 500 -> ((numSamples - i) / 500.0)
                else -> 1.0
            }
            val sample = (sin(2.0 * PI * freq * t) * envelope * Short.MAX_VALUE * 0.75)
            audioData[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        playRawPcm(audioData, sampleRate)
    }

    private fun playClassicBellAudio() {
        val sampleRate = 44100
        val durationMs = 1200
        val numSamples = (durationMs * sampleRate / 1000)
        val audioData = ShortArray(numSamples)

        val f1 = 1175.0
        val f2 = 1760.0
        val f3 = 2349.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val decay = exp(-4.2 * t)
            val strike = if (i < 300) (i / 300.0) else 1.0

            val wave = 0.55 * sin(2.0 * PI * f1 * t) +
                    0.30 * sin(2.0 * PI * f2 * t) +
                    0.15 * sin(2.0 * PI * f3 * t)

            val sample = (wave * strike * decay * Short.MAX_VALUE * 0.95)
            audioData[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        playRawPcm(audioData, sampleRate)
    }

    private fun playClassicWoodClapperAudio() {
        val sampleRate = 44100
        val durationMs = 80
        val numSamples = (durationMs * sampleRate / 1000)
        val audioData = ShortArray(numSamples)

        val f1 = 800.0
        val f2 = 1450.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val decay = exp(-45.0 * t)
            val wave = 0.65 * sin(2.0 * PI * f1 * t) + 0.35 * sin(2.0 * PI * f2 * t)
            val sample = (wave * decay * Short.MAX_VALUE * 0.85)
            audioData[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        playRawPcm(audioData, sampleRate)
    }

    private fun playTone(freq: Double, durationMs: Int, volume: Float) {
        val sampleRate = 44100
        val numSamples = (durationMs * sampleRate / 1000)
        val generatedSnd = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = when {
                i < 150 -> (i / 150.0)
                i > numSamples - 200 -> ((numSamples - i) / 200.0)
                else -> 1.0
            }
            val sample = sin(2.0 * PI * freq * t) * envelope * Short.MAX_VALUE * volume
            generatedSnd[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        playRawPcm(generatedSnd, sampleRate)
    }

    private fun playRawPcm(samples: ShortArray, sampleRate: Int) {
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(samples.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        try {
            audioTrack.write(samples, 0, samples.size)
            audioTrack.play()
            scope.launch {
                val delayTime = (samples.size * 1000L / sampleRate) + 100
                kotlinx.coroutines.delay(delayTime)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
            try { audioTrack.release() } catch (_: Exception) {}
        }
    }

    private fun vibratePattern(pattern: LongArray) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, -1)
            }
        } catch (_: Exception) {}
    }
}
