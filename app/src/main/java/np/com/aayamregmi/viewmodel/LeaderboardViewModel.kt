package np.com.aayamregmi.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import np.com.aayamregmi.database.AppDatabase
import np.com.aayamregmi.database.entity.TestType
import np.com.aayamregmi.session.SessionManager

data class LeaderboardRow(
    val rank: Int,
    val name: String,
    val score: Float,
    val displayScore: String,
    val isCurrentUser: Boolean
)

class LeaderboardViewModel(app: Application) : AndroidViewModel(app) {
    private val leaderboardDao = AppDatabase.getInstance(app).leaderboardDao()
    private val userDao = AppDatabase.getInstance(app).userDao()
    private val session = SessionManager.getInstance(app)

    private val _testType = MutableStateFlow(TestType.REFLEX)
    val testType: StateFlow<TestType> = _testType.asStateFlow()

    private val _rows = MutableStateFlow<List<LeaderboardRow>>(emptyList())
    val rows: StateFlow<List<LeaderboardRow>> = _rows.asStateFlow()

    init { load(TestType.REFLEX) }

    fun selectTest(type: TestType) {
        _testType.value = type
        load(type)
    }

    private fun load(type: TestType) {
        viewModelScope.launch {
            val currentUserId = session.loggedInUserId
            val entries = leaderboardDao.getLeaderboardForTest(type)

            // REFLEX: lower ms = better (ASC), HEARING: higher Hz = better (DESC)
            val sorted = when (type) {
                TestType.REFLEX          -> entries.sortedBy { it.bestScore }
                TestType.HEARING         -> entries.sortedByDescending { it.bestScore }
                TestType.COLOR_BLINDNESS -> entries.sortedByDescending { it.bestScore }
            }

            _rows.value = sorted.mapIndexed { index, entry ->
                val user = userDao.getUserById(entry.userId)
                val name = if (user != null) "${user.firstname} ${user.lastname}" else "User #${entry.userId}"
                LeaderboardRow(
                    rank          = index + 1,
                    name          = name,
                    score         = entry.bestScore,
                    displayScore  = formatScore(type, entry.bestScore),
                    isCurrentUser = entry.userId == currentUserId
                )
            }
        }
    }

    private fun formatScore(type: TestType, score: Float): String = when (type) {
        TestType.REFLEX          -> "${score.toInt()} ms"
        TestType.HEARING         -> "${score.toInt()} Hz"
        TestType.COLOR_BLINDNESS -> "${score.toInt()}%"
    }
}
