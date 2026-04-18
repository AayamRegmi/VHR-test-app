package np.com.aayamregmi.viewmodel

import android.app.Application
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import np.com.aayamregmi.database.AppDatabase
import np.com.aayamregmi.database.entity.LeaderboardEntity
import np.com.aayamregmi.database.entity.TestResultEntity
import np.com.aayamregmi.database.entity.TestType
import np.com.aayamregmi.session.SessionManager
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sin

private const val SAMPLE_RATE = 44100
private const val START_FREQ = 500.0
private const val END_FREQ = 20000.0
private const val SWEEP_SECONDS = 30.0   // full sweep duration
private const val CHUNK = 2205           // ~50 ms per chunk

// Piecewise: higher threshold Hz = sharper hearing = younger age
private val AGE_ANCHORS = listOf(
    500   to 75,
    1000  to 65,
    2000  to 58,
    4000  to 50,
    8000  to 40,
    12000 to 28,
    16000 to 20,
    20000 to 18
)

class HearingTestViewModel(app: Application) : AndroidViewModel(app) {

    private val testResultDao = AppDatabase.getInstance(app).testResultDao()
    private val leaderboardDao = AppDatabase.getInstance(app).leaderboardDao()
    private val session = SessionManager.getInstance(app)

    private val _currentFrequency = MutableStateFlow(START_FREQ.toInt())
    val currentFrequency: StateFlow<Int> = _currentFrequency.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _thresholdHz = MutableStateFlow(0)
    val thresholdHz: StateFlow<Int> = _thresholdHz.asStateFlow()

    private val _estimatedAge = MutableStateFlow(0)
    val estimatedAge: StateFlow<Int> = _estimatedAge.asStateFlow()

    private var sweepJob: Job? = null
    private var audioTrack: AudioTrack? = null

    fun onStart() {
        _currentFrequency.value = START_FREQ.toInt()
        _progress.value = 0f
        _thresholdHz.value = 0
        _estimatedAge.value = 0
        _isRunning.value = true
        startSweep()
    }

    fun onStop() {
        val hz = _currentFrequency.value
        sweepJob?.cancel()
        finalize(hz)
    }

    private fun startSweep() {
        sweepJob?.cancel()
        stopAudio()

        val minBuf = AudioTrack.getMinBufferSize(
            SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT
        )
        val bufSize = maxOf(minBuf, CHUNK * 2 * 4)

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack = track
        track.play()

        val totalSamples = (SWEEP_SECONDS * SAMPLE_RATE).toLong()
        val logRatio = ln(END_FREQ / START_FREQ)

        sweepJob = viewModelScope.launch(Dispatchers.IO) {
            val samples = ShortArray(CHUNK)
            var phase = 0.0
            var pos = 0L

            while (isActive && pos < totalSamples) {
                // Logarithmic frequency at the center of this chunk
                val tCenter = (pos + CHUNK / 2.0) / totalSamples
                val freq = START_FREQ * exp(tCenter * logRatio)
                val phaseInc = 2.0 * PI * freq / SAMPLE_RATE

                for (i in samples.indices) {
                    samples[i] = (Short.MAX_VALUE * 0.5 * sin(phase)).toInt().toShort()
                    phase += phaseInc
                    if (phase > 2.0 * PI) phase -= 2.0 * PI
                }

                track.write(samples, 0, samples.size)
                pos += CHUNK

                val t = pos.toFloat() / totalSamples
                _progress.value = t.coerceIn(0f, 1f)
                _currentFrequency.value = (START_FREQ * exp(t.toDouble() * logRatio))
                    .toInt().coerceAtMost(END_FREQ.toInt())
            }

            // Completed without user stopping — they heard everything
            if (isActive) finalize(END_FREQ.toInt())
        }
    }

    private fun finalize(hz: Int) {
        _isRunning.value = false
        _thresholdHz.value = hz
        _estimatedAge.value = calcAge(hz)
        stopAudio()
        saveResult(hz)
    }

    private fun stopAudio() {
        val track = audioTrack
        audioTrack = null
        try {
            track?.stop()
            track?.release()
        } catch (_: Exception) {}
    }

    private fun calcAge(hz: Int): Int {
        if (hz <= AGE_ANCHORS.first().first) return AGE_ANCHORS.first().second
        if (hz >= AGE_ANCHORS.last().first) return AGE_ANCHORS.last().second
        for (i in 0 until AGE_ANCHORS.size - 1) {
            val (f1, a1) = AGE_ANCHORS[i]
            val (f2, a2) = AGE_ANCHORS[i + 1]
            if (hz in f1..f2) {
                val t = (hz - f1).toFloat() / (f2 - f1)
                return (a1 + (a2 - a1) * t).toInt()
            }
        }
        return 40
    }

    private fun saveResult(hz: Int) {
        val userId = session.loggedInUserId
        if (userId == SessionManager.NO_USER) return
        val score = hz.toFloat()
        viewModelScope.launch {
            testResultDao.insert(
                TestResultEntity(userId = userId, testType = TestType.HEARING, score = score)
            )
            val existing = leaderboardDao.getEntryForUser(userId, TestType.HEARING)
            if (existing == null) {
                leaderboardDao.insert(
                    LeaderboardEntity(userId = userId, testType = TestType.HEARING, bestScore = score)
                )
            } else if (score > existing.bestScore) { // higher Hz heard = better
                leaderboardDao.update(existing.copy(bestScore = score, updatedAt = System.currentTimeMillis()))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        sweepJob?.cancel()
        stopAudio()
    }
}
