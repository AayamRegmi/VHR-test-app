package np.com.aayamregmi.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import np.com.aayamregmi.database.AppDatabase
import np.com.aayamregmi.database.entity.LeaderboardEntity
import np.com.aayamregmi.database.entity.TestResultEntity
import np.com.aayamregmi.database.entity.TestType
import np.com.aayamregmi.session.SessionManager
import kotlin.random.Random

enum class ReflexState {
    WAITING_TO_START,
    WAITING_FOR_GREEN,
    GREEN,
    FINISHED
}

class ReflexTestViewModel(app: Application) : AndroidViewModel(app) {

    private val testResultDao = AppDatabase.getInstance(app).testResultDao()
    private val leaderboardDao = AppDatabase.getInstance(app).leaderboardDao()
    private val session = SessionManager.getInstance(app)

    private val _state = MutableStateFlow(ReflexState.WAITING_TO_START)
    val state: StateFlow<ReflexState> = _state.asStateFlow()

    private val _reactionTimeMs = MutableStateFlow(0L)
    val reactionTimeMs: StateFlow<Long> = _reactionTimeMs.asStateFlow()

    private var startTime = 0L
    private var greenJob: Job? = null

    fun onTap() {
        when (_state.value) {
            ReflexState.WAITING_TO_START -> {
                _state.value = ReflexState.WAITING_FOR_GREEN
                greenJob = viewModelScope.launch {
                    delay(Random.nextLong(1500, 5000))
                    startTime = System.currentTimeMillis()
                    _state.value = ReflexState.GREEN
                }
            }
            ReflexState.GREEN -> {
                greenJob?.cancel()
                val elapsed = System.currentTimeMillis() - startTime
                _reactionTimeMs.value = elapsed
                _state.value = ReflexState.FINISHED
                saveResult(elapsed)
            }
            ReflexState.FINISHED -> {
                _state.value = ReflexState.WAITING_TO_START
                _reactionTimeMs.value = 0L
            }
            ReflexState.WAITING_FOR_GREEN -> { /* ignore taps while waiting */ }
        }
    }

    private fun saveResult(reactionTimeMs: Long) {
        val userId = session.loggedInUserId
        if (userId == SessionManager.NO_USER) return

        val score = reactionTimeMs.toFloat()
        viewModelScope.launch {
            testResultDao.insert(TestResultEntity(userId = userId, testType = TestType.REFLEX, score = score))

            val existing = leaderboardDao.getEntryForUser(userId, TestType.REFLEX)
            if (existing == null) {
                leaderboardDao.insert(LeaderboardEntity(userId = userId, testType = TestType.REFLEX, bestScore = score))
            } else if (score < existing.bestScore) {
                leaderboardDao.update(existing.copy(bestScore = score, updatedAt = System.currentTimeMillis()))
            }
        }
    }
}
