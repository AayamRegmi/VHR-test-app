package np.com.aayamregmi.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import np.com.aayamregmi.database.entity.LeaderboardEntity
import np.com.aayamregmi.database.entity.TestType

@Dao
interface LeaderboardDao {

    @Insert
    suspend fun insert(entry: LeaderboardEntity)

    @Update
    suspend fun update(entry: LeaderboardEntity)

    @Query("SELECT * FROM leaderboard WHERE testType = :testType")
    suspend fun getLeaderboardForTest(testType: TestType): List<LeaderboardEntity>

    @Query("SELECT * FROM leaderboard WHERE userId = :userId AND testType = :testType LIMIT 1")
    suspend fun getEntryForUser(userId: Int, testType: TestType): LeaderboardEntity?
}
