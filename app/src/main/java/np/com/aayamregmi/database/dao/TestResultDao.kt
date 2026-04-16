package np.com.aayamregmi.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import np.com.aayamregmi.database.entity.TestResultEntity
import np.com.aayamregmi.database.entity.TestType

@Dao
interface TestResultDao {

    @Insert
    suspend fun insert(result: TestResultEntity)

    @Query("SELECT * FROM test_results WHERE userId = :userId AND testType = :testType")
    suspend fun getResultsForUser(userId: Int, testType: TestType): List<TestResultEntity>

    @Query("SELECT MIN(score) FROM test_results WHERE userId = :userId AND testType = :testType")
    suspend fun getMinScore(userId: Int, testType: TestType): Float?

    @Query("SELECT MAX(score) FROM test_results WHERE userId = :userId AND testType = :testType")
    suspend fun getMaxScore(userId: Int, testType: TestType): Float?
}
