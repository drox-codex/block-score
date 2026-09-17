package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.data.GamePreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class SoundManager(
    private val context: Context,
    private val preferences: GamePreferences
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var ambientMusicJob: Job? = null

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    init {
        updateAmbientMusic()
    }

    fun updateAmbientMusic() {
        if (preferences.musicEnabled) {
            startAmbientMusic()
        } else {
            stopAmbientMusic()
        }
    }

    private fun startAmbientMusic() {
        if (ambientMusicJob?.isActive == true) return
        ambientMusicJob = coroutineScope.launch {
            // Calm, soothing lo-fi ambient pad chords (Cmaj9 -> Am9 -> Fmaj9 -> G6)
            val chords = listOf(
                listOf(130.81, 196.00, 246.94, 329.63, 587.33), // Cmaj9
                listOf(110.00, 164.81, 196.00, 261.63, 493.88), // Am9
                listOf(87.31, 130.81, 164.81, 220.00, 392.00),  // Fmaj9
                listOf(98.00, 146.83, 196.00, 246.94, 329.63)   // G6
            )
            var chordIdx = 0
            while (isActive && preferences.musicEnabled) {
                val chord = chords[chordIdx % chords.size]
                chordIdx++
                playSynthPad(chord, durationMs = 4200)
                delay(4000)
            }
        }
    }

    private fun stopAmbientMusic() {
        ambientMusicJob?.cancel()
        ambientMusicJob = null
    }

    fun playBlockPlace() {
        if (preferences.vibrationEnabled) {
            vibrate(15, 60)
        }
        if (!preferences.soundEnabled) return

        coroutineScope.launch {
            // Satisfying acoustic marimba / wooden pop
            val sampleRate = 44100
            val durationMs = 85
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val progress = i.toDouble() / numSamples
                val freq = 480.0 - (progress * 260.0)
                val envelope = exp(-progress * 8.5)
                val sample = (sin(2.0 * PI * freq * t) + 0.35 * sin(4.0 * PI * freq * t)) * envelope
                buffer[i] = (sample * 16500.0).toInt().coerceIn(-32767, 32767).toShort()
            }
            playPcm(buffer, sampleRate)
        }
    }

    fun playLineClear(lineCount: Int = 1) {
        if (preferences.vibrationEnabled) {
            vibrate(35, 180)
        }
        if (!preferences.soundEnabled) return

        coroutineScope.launch {
            // Bright sparkling crystal windchime / harp sweep
            val baseFreqs = when (lineCount) {
                1 -> listOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
                2 -> listOf(523.25, 659.25, 783.99, 1046.50, 1318.51) // C5 to E6
                else -> listOf(523.25, 659.25, 783.99, 1046.50, 1318.51, 1567.98) // Full radiant sparkle
            }

            val sampleRate = 44100
            val durationMs = 300
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)

            val noteSamples = numSamples / baseFreqs.size
            for (noteIdx in baseFreqs.indices) {
                val freq = baseFreqs[noteIdx]
                val start = noteIdx * (noteSamples * 2 / 3)
                for (i in 0 until (numSamples - start)) {
                    val bufIdx = start + i
                    if (bufIdx >= numSamples) break
                    val t = i.toDouble() / sampleRate
                    val progress = i.toDouble() / (numSamples - start)
                    val envelope = exp(-progress * 4.2)
                    val sample = (sin(2.0 * PI * freq * t) + 0.25 * sin(4.0 * PI * freq * t) + 0.1 * sin(6.0 * PI * freq * t)) * envelope
                    val currentVal = buffer[bufIdx].toDouble()
                    buffer[bufIdx] = (currentVal + sample * 11500.0).toInt().coerceIn(-32767, 32767).toShort()
                }
            }
            playPcm(buffer, sampleRate)
        }
    }

    fun playCombo(comboCount: Int) {
        if (preferences.vibrationEnabled) {
            vibrate(45, 220)
        }
        if (!preferences.soundEnabled) return

        coroutineScope.launch {
            val pitchMultiplier = 1.0 + (comboCount.coerceAtMost(8) * 0.1)
            val baseFreqs = listOf(
                523.25 * pitchMultiplier,
                659.25 * pitchMultiplier,
                783.99 * pitchMultiplier,
                1046.50 * pitchMultiplier
            )

            val sampleRate = 44100
            val durationMs = 360
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)

            val step = numSamples / 4
            for (idx in baseFreqs.indices) {
                val freq = baseFreqs[idx]
                val start = idx * (step / 2)
                for (i in 0 until (numSamples - start)) {
                    val bufIdx = start + i
                    if (bufIdx >= numSamples) break
                    val t = i.toDouble() / sampleRate
                    val progress = i.toDouble() / (numSamples - start)
                    val envelope = exp(-progress * 3.8)
                    val sample = (sin(2.0 * PI * freq * t) + 0.3 * sin(3.0 * PI * freq * t)) * envelope
                    val currentVal = buffer[bufIdx].toDouble()
                    buffer[bufIdx] = (currentVal + sample * 12500.0).toInt().coerceIn(-32767, 32767).toShort()
                }
            }
            playPcm(buffer, sampleRate)
        }
    }

    fun playLevelComplete() {
        if (preferences.vibrationEnabled) {
            vibrate(60, 240)
        }
        if (!preferences.soundEnabled) return

        coroutineScope.launch {
            val melody = listOf(523.25, 659.25, 783.99, 1046.50, 1318.51)
            val sampleRate = 44100
            val durationMs = 500
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)

            val step = numSamples / melody.size
            for (idx in melody.indices) {
                val freq = melody[idx]
                val start = idx * step
                for (i in 0 until (numSamples - start)) {
                    val bufIdx = start + i
                    if (bufIdx >= numSamples) break
                    val t = i.toDouble() / sampleRate
                    val progress = i.toDouble() / (numSamples - start)
                    val envelope = exp(-progress * 3.5)
                    val sample = (sin(2.0 * PI * freq * t) + 0.2 * sin(4.0 * PI * freq * t)) * envelope
                    val currentVal = buffer[bufIdx].toDouble()
                    buffer[bufIdx] = (currentVal + sample * 13000.0).toInt().coerceIn(-32767, 32767).toShort()
                }
            }
            playPcm(buffer, sampleRate)
        }
    }

    fun playGameOver() {
        if (preferences.vibrationEnabled) {
            vibrate(80, 140)
        }
        if (!preferences.soundEnabled) return

        coroutineScope.launch {
            val melody = listOf(440.00, 392.00, 349.23, 293.66)
            val sampleRate = 44100
            val durationMs = 450
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)

            val step = numSamples / melody.size
            for (idx in melody.indices) {
                val freq = melody[idx]
                val start = idx * step
                for (i in 0 until (numSamples - start)) {
                    val bufIdx = start + i
                    if (bufIdx >= numSamples) break
                    val t = i.toDouble() / sampleRate
                    val progress = i.toDouble() / (numSamples - start)
                    val envelope = exp(-progress * 3.0)
                    val sample = sin(2.0 * PI * freq * t) * envelope
                    val currentVal = buffer[bufIdx].toDouble()
                    buffer[bufIdx] = (currentVal + sample * 13000.0).toInt().coerceIn(-32767, 32767).toShort()
                }
            }
            playPcm(buffer, sampleRate)
        }
    }

    private fun playSynthPad(frequencies: List<Double>, durationMs: Int) {
        val sampleRate = 22050
        val numSamples = (sampleRate * durationMs) / 1000
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / numSamples
            // Gentle ambient pad envelope: soft fade in, sustained warmth, soft fade out
            val envelope = when {
                progress < 0.25 -> progress / 0.25
                progress > 0.70 -> (1.0 - progress) / 0.30
                else -> 1.0
            }
            var mixedSample = 0.0
            for (freq in frequencies) {
                // Pure sine + gentle warm harmonic for cozy analog depth
                mixedSample += sin(2.0 * PI * freq * t) + 0.15 * sin(PI * freq * t)
            }
            mixedSample = (mixedSample / frequencies.size) * envelope * 2800.0
            buffer[i] = mixedSample.toInt().coerceIn(-32767, 32767).toShort()
        }
        playPcm(buffer, sampleRate)
    }

    private fun playPcm(buffer: ShortArray, sampleRate: Int) {
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
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
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            // Schedule track release
            coroutineScope.launch {
                val playDurationMs = (buffer.size * 1000L) / sampleRate + 100
                delay(playDurationMs)
                try {
                    track.stop()
                    track.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }

    private fun vibrate(durationMs: Long, amplitude: Int = VibrationEffect.DEFAULT_AMPLITUDE) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255)))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }
}
