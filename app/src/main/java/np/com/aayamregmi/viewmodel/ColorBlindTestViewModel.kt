package np.com.aayamregmi.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import np.com.aayamregmi.database.AppDatabase
import np.com.aayamregmi.database.entity.LeaderboardEntity
import np.com.aayamregmi.database.entity.TestResultEntity
import np.com.aayamregmi.database.entity.TestType
import np.com.aayamregmi.session.SessionManager

private data class IshiharaPlate(val url: String, val answer: String)

private val PLATES = listOf(
    IshiharaPlate("https://commons.wikimedia.org/wiki/Special:FilePath/Ishihara_1.svg", "12"),
    IshiharaPlate("https://commons.wikimedia.org/wiki/Special:FilePath/Ishihara_2.svg", "8"),
    IshiharaPlate("https://commons.wikimedia.org/wiki/Special:FilePath/Ishihara_3.svg", "29"),
    IshiharaPlate("https://commons.wikimedia.org/wiki/Special:FilePath/Ishihara_4.svg", "5"),
    IshiharaPlate("https://commons.wikimedia.org/wiki/Special:FilePath/Ishihara_5.svg", "3"),
    IshiharaPlate("https://commons.wikimedia.org/wiki/Special:FilePath/Ishihara_6.svg", "15"),
    IshiharaPlate("https://commons.wikimedia.org/wiki/Special:FilePath/Ishihara_7.svg", "74"),
    IshiharaPlate("https://commons.wikimedia.org/wiki/Special:FilePath/Ishihara_8.svg", "6"),
    IshiharaPlate("https://commons.wikimedia.org/wiki/Special:FilePath/Ishihara_9.svg", "45"),
    IshiharaPlate("https://commons.wikimedia.org/wiki/Special:FilePath/Ishihara_10.svg", "5"),
)

enum class ColorBlindState { IDLE, TESTING, FINISHED }

class ColorBlindTestViewModel(app: Application) : AndroidViewModel(app) {

    private val testResultDao = AppDatabase.getInstance(app).testResultDao()
    private val leaderboardDao = AppDatabase.getInstance(app).leaderboardDao()
    private val session = SessionManager.getInstance(app)

    val totalPlates: Int = PLATES.size

    private val _state = MutableStateFlow(ColorBlindState.IDLE)
    val state: StateFlow<ColorBlindState> = _state.asStateFlow()

    private val _plateIndex = MutableStateFlow(0)
    val plateIndex: StateFlow<Int> = _plateIndex.asStateFlow()

    private val _currentUrl = MutableStateFlow(PLATES[0].url)
    val currentUrl: StateFlow<String> = _currentUrl.asStateFlow()

    private val _score = MutableStateFlow(0f)
    val score: StateFlow<Float> = _score.asStateFlow()

    private var correctCount = 0

    fun onStart() {
        correctCount = 0
        _plateIndex.value = 0
        _currentUrl.value = PLATES[0].url
        _score.value = 0f
        _state.value = ColorBlindState.TESTING
    }

    fun onSubmit(userInput: String) {
        if (userInput.trim() == PLATES[_plateIndex.value].answer) correctCount++

        val next = _plateIndex.value + 1
        if (next < PLATES.size) {
            _plateIndex.value = next
            _currentUrl.value = PLATES[next].url
        } else {
            val pct = correctCount.toFloat() / PLATES.size * 100f
            _score.value = pct
            _state.value = ColorBlindState.FINISHED
            saveResult(pct)
        }
    }

    fun onRetry() {
        _state.value = ColorBlindState.IDLE
    }

    private fun saveResult(pct: Float) {
        val userId = session.loggedInUserId
        if (userId == SessionManager.NO_USER) return
        viewModelScope.launch {
            testResultDao.insert(
                TestResultEntity(userId = userId, testType = TestType.COLOR_BLINDNESS, score = pct)
            )
            val existing = leaderboardDao.getEntryForUser(userId, TestType.COLOR_BLINDNESS)
            if (existing == null) {
                leaderboardDao.insert(
                    LeaderboardEntity(userId = userId, testType = TestType.COLOR_BLINDNESS, bestScore = pct)
                )
            } else if (pct > existing.bestScore) {
                leaderboardDao.update(existing.copy(bestScore = pct, updatedAt = System.currentTimeMillis()))
            }
        }
    }
}
