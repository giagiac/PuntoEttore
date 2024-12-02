package it.puntoettore.fidelity.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val notifierToken: String,
    val privacy: Boolean,

    val uid: String,
    var displayName: String?,
    val email: String?,
    var phoneNumber: String?,
    var photoURL: String?,
    val isAnonymous: Boolean,
    val isEmailVerified: Boolean,
    val providerId: String
)