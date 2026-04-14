package np.com.aayamregmi.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import np.com.aayamregmi.database.dao.LeaderboardDao
import np.com.aayamregmi.database.dao.TestResultDao
import np.com.aayamregmi.database.dao.UserDao
import np.com.aayamregmi.database.entity.LeaderboardEntity
import np.com.aayamregmi.database.entity.TestResultEntity
import np.com.aayamregmi.database.entity.TestType
import np.com.aayamregmi.database.entity.UserEntity

class TestTypeConverter {
    @TypeConverter
    fun fromTestType(value: TestType): String = value.name

    @TypeConverter
    fun toTestType(value: String): TestType = TestType.valueOf(value)
}

@Database(
    entities = [
        UserEntity::class,
        TestResultEntity::class,
        LeaderboardEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(TestTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun testResultDao(): TestResultDao
    abstract fun leaderboardDao(): LeaderboardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vhr_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
