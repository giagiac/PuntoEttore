package it.puntoettore.fidelity.api.datamodel

import kotlinx.serialization.Serializable

@Serializable
data class CensoredText(val result: String)

@Serializable
data class DataUser(val token: String)

@Serializable
data class DataUid(val uid: String)

@Serializable
data class UserDetail(
    val name: String,
    val surname: String,
    val email: String,
    val dateOfBirth: String,
    val score: String,
    val listScores: List<Score>
)

@Serializable
data class Score(
    val dataScan: String,
    val points: String
)

@Serializable
data class Offers(
    val count: String,
    val listOffers: List<Offer>
)

@Serializable
data class Offer(
    val id: String,
    val url: String,
    val points: String,
    val dataExpire: String,
    val detail: String
)