package it.puntoettore.fidelity.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import it.puntoettore.fidelity.domain.AppSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppSettings(appSettings: AppSettings)

    @Update
    suspend fun updateAppSettings(appSettings: AppSettings)

    @Transaction
    @Query("SELECT * FROM appSettings where _id = ${AppSettings.ID}")
    fun getAppSettings(): Flow<AppSettings?>

    @Transaction
    @Query("DELETE FROM appSettings where _id = ${AppSettings.ID}")
    suspend fun deleteAppSettingsById()
}