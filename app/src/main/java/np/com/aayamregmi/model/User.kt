package np.com.aayamregmi.model

data class User(
    val uid: String,
    val firstname: String,
    val lastname: String,
    val middlename: String? = null,
    val email: String,
    val password: String
)
