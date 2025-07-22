package it.puntoettore.fidelity.api.datamodel

import kotlinx.serialization.Serializable

@Serializable
data class CensoredText(val result: String)

@Serializable
data class DataUid(val uid: String)

@Serializable
data class DataWrapper<T>(val data: Data<T>)

@Serializable
data class Attributes(val uid: String)

@Serializable
data class Data<T>(val type: String = "xa_xApi", val attributes: T)

@Serializable
data class AttributesBillFidelity(val uid: String, val codice: String, val matricola: String)

@Serializable
data class AttributesVecchioCliente(val uid: String, val oldId: String)

@Serializable
data class AuthDetail(
    val token_type: String,
    val expires_in: String,
    val access_token: String,
    val refresh_token: String,
)

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
data class CreditiFidelity(
    val codscontrino: String?,
    val matricola: String?,
    val data_inserimento: String?,
    val punteggio: String?
)

@Serializable
data class BillFidelity(
    val codice: String?,
    val matricola: String?,
    val totale: String?,
    val articoli: List<Articolo>?
)

@Serializable
data class ResponseVecchioCliente(
    val codice: String?,
    val matricola: String?,
    val totale: String?,
    val articoli: List<Articolo>?
)

@Serializable
data class Articolo(
    val c_lordo: String?,
    val c_netto: String?,
    val sconto_finale: String?,
    val descrizione: String?,
    val qta: Int?
)

@Serializable
data class DatiFidelity(
    val uid: String,
    val firstName: String,
    val allineata: String,
    val points: Int,
    val fascia: String
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