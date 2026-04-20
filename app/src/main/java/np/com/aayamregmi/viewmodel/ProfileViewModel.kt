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

data class ProfileState(
    val displayName: String = "",
    val initial: String = "?",
    val email: String = "",
    val testsTaken: Int = 0,
    val bestColorBlindPct: Float? = null
)

class ProfileViewModel(app: Application) : AndroidViewModel(app) {

    private val userDao = AppDatabase.getInstance(app).userDao()
    private val testResultDao = AppDatabase.getInstance(app).testResultDao()
    private val session = SessionManager.getInstance(app)

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init { load() }

    private fun load() {
        val userId = session.loggedInUserId
        if (userId == SessionManager.NO_USER) return
        viewModelScope.launch {
            val user = userDao.getUserById(userId) ?: return@launch
            val totalResults = TestType.values().sumOf { type ->
                testResultDao.getResultsForUser(userId, type).size
            }
            val bestColorBlind = testResultDao.getMaxScore(userId, TestType.COLOR_BLINDNESS)
            _state.value = ProfileState(
                displayName = "${user.firstname} ${user.lastname}",
                initial = user.firstname.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                email = user.email,
                testsTaken = totalResults,
                bestColorBlindPct = bestColorBlind
            )
        }
    }

    fun logout() {
        session.clearSession()
    }
}
