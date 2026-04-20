package np.com.aayamregmi.viewmodel

import android.app.Application
import androidx.annotation.DrawableRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import np.com.aayamregmi.R
import np.com.aayamregmi.database.AppDatabase
import np.com.aayamregmi.database.entity.LeaderboardEntity
import np.com.aayamregmi.database.entity.TestResultEntity
import np.com.aayamregmi.database.entity.TestType
import np.com.aayamregmi.session.SessionManager

private data class IshiharaPlate(@DrawableRes val imageRes: Int, val answer: String)

private val PLATES = listOf(
    IshiharaPlate(R.drawable.ishihara_plate_1,   "12"),
    IshiharaPlate(R.drawable.ishihara_plate_2,   "8"),
    IshiharaPlate(R.drawable.ishihara_plate_3,   "29"),
    IshiharaPlate(R.drawable.ishihara_plate_4,   "5"),
    IshiharaPlate(R.drawable.ishihara_plate_5,   "3"),
    IshiharaPlate(R.drawable.ishihara_plate_6,   "15"),
    IshiharaPlate(R.drawable.ishihara_plate_7,   "74"),
    IshiharaPlate(R.drawable.ishihara_plate_8,   "6"),
    IshiharaPlate(R.drawable.ishihara_plate_9,   "45"),
    IshiharaPlate(R.drawable.ishihara_plate_10,  "5"),
    IshiharaPlate(R.drawable.ishihara_plate_11,  "7"),
    IshiharaPlate(R.drawable.ishihara_plate_12,  "16"),
    IshiharaPlate(R.drawable.ishihara_plate_13,  "73"),
    IshiharaPlate(R.drawable.ishihara_plate_14,  "26"),
    IshiharaPlate(R.drawable.ishihara_plate_15,  "42"),
    IshiharaPlate(R.drawable.ishihara_nothing_1, "0"),
    IshiharaPlate(R.drawable.ishihara_nothing_2, "0"),
    IshiharaPlate(R.drawable.ishihara_nothing_3, "0"),
    IshiharaPlate(R.drawable.ishihara_nothing_4, "0"),
    IshiharaPlate(R.drawable.ishihara_nothing_5, "0"),
    IshiharaPlate(R.drawable.ishihara_nothing_6, "0"),
    IshiharaPlate(R.drawable.ishihara_nothing_7, "0"),
    IshiharaPlate(R.drawable.ishihara_nothing_8, "0"),
    IshiharaPlate(R.drawable.ishihara_nothing_9, "0"),
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

    private val _currentImageRes = MutableStateFlow(PLATES[0].imageRes)
    val currentImageRes: StateFlow<Int> = _currentImageRes.asStateFlow()

    private val _score = MutableStateFlow(0f)
    val score: StateFlow<Float> = _score.asStateFlow()

    private var correctCount = 0
    private var activePlates: List<IshiharaPlate> = PLATES

    fun onStart() {
        activePlates = PLATES.shuffled()
        correctCount = 0
        _plateIndex.value = 0
        _currentImageRes.value = activePlates[0].imageRes
        _score.value = 0f
        _state.value = ColorBlindState.TESTING
    }

    fun onSubmit(userInput: String) {
        if (userInput.trim() == activePlates[_plateIndex.value].answer) correctCount++

        val next = _plateIndex.value + 1
        if (next < activePlates.size) {
            _plateIndex.value = next
            _currentImageRes.value = activePlates[next].imageRes
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
