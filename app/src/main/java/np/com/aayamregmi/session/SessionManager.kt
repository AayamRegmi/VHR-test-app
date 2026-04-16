package np.com.aayamregmi.session

import android.content.Context
import android.content.SharedPreferences

class SessionManager private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val loggedInUserId: Int
        get() = prefs.getInt(KEY_USER_ID, NO_USER)

    val isLoggedIn: Boolean
        get() = loggedInUserId != NO_USER

    fun startSession(userId: Int) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "vhr_session"
        private const val KEY_USER_ID = "user_id"
        const val NO_USER = -1

        @Volatile private var INSTANCE: SessionManager? = null

        fun getInstance(context: Context): SessionManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SessionManager(context.applicationContext).also { INSTANCE = it }
            }
    }
}
