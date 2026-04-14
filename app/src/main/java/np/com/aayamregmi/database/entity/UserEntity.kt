package np.com.aayamregmi.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,
    val firstname: String,
    val lastname: String,
    val middlename: String? = null,
    val email: String,
    val password: String
)
