package it.puntoettore.fidelity.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import it.puntoettore.fidelity.domain.User
import kotlinx.coroutines.flow.Flow


@Dao
interface UserDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    // non funziona non ho capito perchè!!!
    @Query("UPDATE user SET refreshToken = :refreshToken, accessToken = :accessToken WHERE _id = :id")
    fun updateTokens(id: Int, refreshToken: String?, accessToken: String?) : Int

    @Transaction
    @Query("SELECT * FROM user WHERE _id = :userId")
    fun getUserById(userId: Int): Flow<User?>

    @Transaction
    @Query("DELETE FROM user WHERE _id = :userId")
    suspend fun deleteUserById(userId: Int)
}