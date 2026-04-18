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

data class BestScores(
    val colorBlindPct: Float? = null,
    val reflexMs: Float? = null,
    val hearingHz: Float? = null
)

class DashboardViewModel(app: Application) : AndroidViewModel(app) {
    private val testResultDao = AppDatabase.getInstance(app).testResultDao()
    private val session = SessionManager.getInstance(app)

    private val _scores = MutableStateFlow(BestScores())
    val scores: StateFlow<BestScores> = _scores.asStateFlow()

    init { refresh() }

    fun refresh() {
        val userId = session.loggedInUserId
        if (userId == SessionManager.NO_USER) return
        viewModelScope.launch {
            _scores.value = BestScores(
                colorBlindPct = testResultDao.getMaxScore(userId, TestType.COLOR_BLINDNESS),
                reflexMs      = testResultDao.getMinScore(userId, TestType.REFLEX),
                hearingHz     = testResultDao.getMaxScore(userId, TestType.HEARING)
            )
        }
    }
}
