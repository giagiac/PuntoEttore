package it.puntoettore.fidelity.api.datamodel

import it.puntoettore.fidelity.api.util.NetworkEError
import kotlinx.serialization.Serializable

@Serializable
data class CensoredText(val result: String)

@Serializable
data class DataWrapper<T>(val data: DataObj<T>)

@Serializable
data class DataObj<T>(val type: String = "xa_xApi", val attributes: T)

@Serializable
data class AttributesBillFidelity(val codice: String, val matricola: String)

@Serializable
data class AttributesVecchioCliente(val oldId: String)

@Serializable
data class AttributesTicket(val msg: String)

@Serializable
data class AttributesUpdAnagFidelity(val type: String, val data: AttributesUpdAnagFidelitySub)
@Serializable
data class AttributesUpdAnagFidelitySub(val displayName: String, val phone: String, val data_nascita: String)

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
    val punteggio: String?,

    // non fa parte delle api
    val punteggioPercentuale: Int?
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
    val message: String?
)

@Serializable
data class ResponseUptAnagFidelity(
    val operazione: String
)

@Serializable
data class ResponseGeneric(
    val message: String?,
    // Att.ne non proviene dall'api
    val error: NetworkEError?
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
data class DatiFidelityResponse(
    val firstName: String,
    val allineata: String,
    val points: Int,
    val fascia: String,
    // ---------- new fields
    val vecchio_cliente: String?,
    val oldId: String?,
    val phone: String?,
    val dataNascita: String?
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