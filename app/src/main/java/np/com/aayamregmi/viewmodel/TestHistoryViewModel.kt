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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HistoryEntry(
    val displayScore: String,
    val takenAt: Long,
    val displayDate: String
)

class TestHistoryViewModel(app: Application) : AndroidViewModel(app) {
    private val testResultDao = AppDatabase.getInstance(app).testResultDao()
    private val session = SessionManager.getInstance(app)

    private val _selectedType = MutableStateFlow(TestType.REFLEX)
    val selectedType: StateFlow<TestType> = _selectedType.asStateFlow()

    private val _entries = MutableStateFlow<List<HistoryEntry>>(emptyList())
    val entries: StateFlow<List<HistoryEntry>> = _entries.asStateFlow()

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy  HH:mm", Locale.getDefault())

    init { load(TestType.REFLEX) }

    fun selectTest(type: TestType) {
        _selectedType.value = type
        load(type)
    }

    fun refresh() = load(_selectedType.value)

    private fun load(type: TestType) {
        val userId = session.loggedInUserId
        if (userId == SessionManager.NO_USER) {
            _entries.value = emptyList()
            return
        }
        viewModelScope.launch {
            val results = testResultDao.getResultsForUser(userId, type)
            _entries.value = results
                .sortedByDescending { it.takenAt }
                .map { r ->
                    HistoryEntry(
                        displayScore = formatScore(type, r.score),
                        takenAt      = r.takenAt,
                        displayDate  = dateFormat.format(Date(r.takenAt))
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
