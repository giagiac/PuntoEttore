package it.puntoettore.fidelity.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appSettings")
data class AppSettings(
    @PrimaryKey(autoGenerate = false)
    val _id: Int = ID,
    val _idUser: Int,
    val darkMode: Boolean
) {
    companion object {
        const val ID = 0
    }
}
