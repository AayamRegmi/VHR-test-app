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
import np.com.aayamregmi.database.entity.UserEntity

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegisterSuccess: Boolean = false
)

class RegisterViewModel(app: Application) : AndroidViewModel(app) {

    private val userDao = AppDatabase.getInstance(app).userDao()

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onFullNameChange(value: String) {
        _uiState.update { it.copy(fullName = value, errorMessage = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }
    }

    fun onRegisterClick() {
        val state = _uiState.value

        val error = validate(state)
        if (error != null) {
            _uiState.update { it.copy(errorMessage = error) }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        val nameParts = state.fullName.trim().split(" ", limit = 2)
        val firstName = nameParts[0]
        val lastName = if (nameParts.size > 1) nameParts[1] else ""

        viewModelScope.launch {
            try {
                userDao.insert(
                    UserEntity(
                        firstname = firstName,
                        lastname = lastName,
                        email = state.email.trim(),
                        password = state.password
                    )
                )
                _uiState.update { it.copy(isLoading = false, isRegisterSuccess = true) }
            } catch (e: Exception) {
                val message = if (e.message?.contains("UNIQUE") == true)
                    "An account with that email already exists"
                else
                    "Registration failed, please try again"
                _uiState.update { it.copy(isLoading = false, errorMessage = message) }
            }
        }
    }

    fun onRegisterHandled() {
        _uiState.update { it.copy(isRegisterSuccess = false) }
    }

    private fun validate(state: RegisterUiState): String? {
        if (state.fullName.isBlank()) return "Full name is required"
        if (state.email.isBlank()) return "Email is required"
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) return "Enter a valid email"
        if (state.password.isBlank()) return "Password is required"
        if (state.password.length < 3) return "Password must be at least 3 characters"
        if (state.password != state.confirmPassword) return "Passwords do not match"
        return null
    }
}
