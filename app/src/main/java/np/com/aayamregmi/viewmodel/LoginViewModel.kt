package np.com.aayamregmi.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import np.com.aayamregmi.database.AppDatabase
import np.com.aayamregmi.session.SessionManager

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccess: Boolean = false
)

class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val userDao = AppDatabase.getInstance(app).userDao()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onLoginClick() {
        val state = _uiState.value

        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email and password are required") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val user = userDao.findByEmail(state.email.trim())
            when {
                user == null ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = "No account found with that email") }
                user.password != state.password ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Incorrect password") }
                else -> {
                    SessionManager.loggedInUserId = user.uid
                    _uiState.update { it.copy(isLoading = false, isLoginSuccess = true) }
                }
            }
        }
    }

    fun onLoginHandled() {
        _uiState.update { it.copy(isLoginSuccess = false) }
    }
}
