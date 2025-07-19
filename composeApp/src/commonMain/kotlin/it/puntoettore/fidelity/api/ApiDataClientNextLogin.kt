package it.puntoettore.fidelity.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import it.puntoettore.fidelity.api.datamodel.Attributes
import it.puntoettore.fidelity.api.datamodel.AttributesBillFidelity
import it.puntoettore.fidelity.api.datamodel.AuthDetail
import it.puntoettore.fidelity.api.datamodel.BillFidelity
import it.puntoettore.fidelity.api.datamodel.CreditiFidelity
import it.puntoettore.fidelity.api.datamodel.Data
import it.puntoettore.fidelity.api.datamodel.DataUid
import it.puntoettore.fidelity.api.datamodel.DataWrapper
import it.puntoettore.fidelity.api.datamodel.DatiFidelity
import it.puntoettore.fidelity.api.datamodel.Offers
import it.puntoettore.fidelity.api.datamodel.UserDetail
import it.puntoettore.fidelity.api.util.NetworkEError
import it.puntoettore.fidelity.api.util.Result
import it.puntoettore.fidelity.custom.BuildConfig
import kotlinx.serialization.SerializationException
import kotlin.coroutines.cancellation.CancellationException

class ApiDataClientNextLogin(
    private val httpClient: HttpClient
) {
    private lateinit var uid: String

    fun setUid(_uid: String) {
        uid = _uid
    }

    suspend fun getAccess(uid: String): Result<AuthDetail, NetworkEError> {
        val response = try {
            httpClient.get(urlString = "${BuildConfig.END_POINT}/index.php?entryPoint=getAccess") {
                contentType(ContentType.Application.Json)
                // setBody(DataUser(token = uid))
                headers {
                    append("uid", uid)
                }
            }
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkEError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkEError.SERIALIZATION)
        } catch (e: CancellationException) {
            println("Coroutine Il job è stato annullato come previsto.")
            throw e // Rilancia l'eccezione per completare l'annullamento
        } catch (e: Exception) {
            return Result.Error(NetworkEError.UNKNOWN)
        }

        return when (response.status.value) {
            in 200..299 -> {
                val body = response.body<AuthDetail>()
                Result.Success(body)
            }

            401 -> Result.Error(NetworkEError.UNAUTHORIZED)
            404 -> Result.Error(NetworkEError.NOT_FOUND)
            409 -> Result.Error(NetworkEError.CONFLICT)
            408 -> Result.Error(NetworkEError.REQUEST_TIMEOUT)
            413 -> Result.Error(NetworkEError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkEError.SERVER_ERROR)
            else -> Result.Error(NetworkEError.UNKNOWN)
        }
    }

//    suspend fun getRefresh(): Result<AuthDetail, NetworkEError> {
//        val response = try {
//            httpClient.get(urlString = "${BuildConfig.END_POINT}/index.php?entryPoint=getRefresh") {
//                contentType(ContentType.Application.Json)
//                headers {
//                    append("refreshToken", refreshToken)
//                }
//            }
//        } catch (e: UnresolvedAddressException) {
//            return Result.Error(NetworkEError.NO_INTERNET)
//        } catch (e: SerializationException) {
//            return Result.Error(NetworkEError.SERIALIZATION)
//        } catch (e: Exception) {
//            return Result.Error(NetworkEError.NO_INTERNET)
//        }
//
//        return when (response.status.value) {
//            in 200..299 -> {
//                val body = response.body<AuthDetail>()
//                this.refreshToken = body.refresh_token
//                Result.Success(body)
//            }
//
//            401 -> Result.Error(NetworkEError.UNAUTHORIZED)
//            409 -> Result.Error(NetworkEError.CONFLICT)
//            408 -> Result.Error(NetworkEError.REQUEST_TIMEOUT)
//            413 -> Result.Error(NetworkEError.PAYLOAD_TOO_LARGE)
//            in 500..599 -> Result.Error(NetworkEError.SERVER_ERROR)
//            else -> Result.Error(NetworkEError.UNKNOWN)
//        }
//    }

    // TODO : sostituire con quella nuova (dove usata)
    suspend fun getUserDetail(uid: String): Result<UserDetail, NetworkEError> {
        val response = try {
            httpClient.post(urlString = "${BuildConfig.END_POINT}/index.php?entryPoint=datiFidelity") {
                contentType(ContentType.Application.Json)
                setBody(DataUid(uid = uid))
            }
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkEError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkEError.SERIALIZATION)
        } catch (e: Exception) {
            return Result.Error(NetworkEError.NO_INTERNET)
        }

        return when (response.status.value) {
            in 200..299 -> {
                val data = response.body<UserDetail>()
                Result.Success(data)
            }

            401 -> {
                return Result.Error(NetworkEError.UNAUTHORIZED)
            }
            409 -> Result.Error(NetworkEError.CONFLICT)
            408 -> Result.Error(NetworkEError.REQUEST_TIMEOUT)
            413 -> Result.Error(NetworkEError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkEError.SERVER_ERROR)
            else -> Result.Error(NetworkEError.UNKNOWN)
        }
    }

    suspend fun getDatiFidelity(): Result<DatiFidelity, NetworkEError> {
        val response = try {
            httpClient.post(urlString = "${BuildConfig.END_POINT}/index.php?entryPoint=datiFidelity") {
                contentType(ContentType.Application.Json)
                setBody(
                    DataWrapper(
                        data = Data(
                            attributes = Attributes(uid = uid)
                        )
                    )
                )
            }
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkEError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkEError.SERIALIZATION)
        } catch (e: Exception) {
            return Result.Error(NetworkEError.NO_INTERNET)
        }

        return when (response.status.value) {
            in 200..299 -> {
                val data = response.body<DatiFidelity>()
                Result.Success(data)
            }

            401 -> Result.Error(NetworkEError.UNAUTHORIZED)
            409 -> Result.Error(NetworkEError.CONFLICT)
            408 -> Result.Error(NetworkEError.REQUEST_TIMEOUT)
            413 -> Result.Error(NetworkEError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkEError.SERVER_ERROR)
            else -> Result.Error(NetworkEError.UNKNOWN)
        }
    }

    suspend fun getCreditiFidelity(): Result<List<CreditiFidelity>, NetworkEError> {
        val response = try {
            httpClient.post(urlString = "${BuildConfig.END_POINT}/index.php?entryPoint=creditiFidelity") {
                contentType(ContentType.Application.Json)
                setBody(
                    DataWrapper(
                        data = Data(
                            type = "xa_xApi",
                            attributes = Attributes(uid = uid)
                        )
                    )
                )
            }
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkEError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkEError.SERIALIZATION)
        } catch (e: Exception) {
            println("OK : FAIL ${e.message}")
            return Result.Error(NetworkEError.NO_INTERNET)
        }

        return when (response.status.value) {
            in 200..299 -> {
                val data = response.body<List<CreditiFidelity>>()
                Result.Success(data)
            }

            401 -> Result.Error(NetworkEError.UNAUTHORIZED)
            409 -> Result.Error(NetworkEError.CONFLICT)
            408 -> Result.Error(NetworkEError.REQUEST_TIMEOUT)
            413 -> Result.Error(NetworkEError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkEError.SERVER_ERROR)
            else -> Result.Error(NetworkEError.UNKNOWN)
        }
    }

    suspend fun getBillFidelity(codice:String, matricola: String): Result<BillFidelity, NetworkEError> {
        val response = try {
            httpClient.post(urlString = "${BuildConfig.END_POINT}/index.php?entryPoint=billFidelity") {
                contentType(ContentType.Application.Json)
                setBody(
                    DataWrapper(
                        data = Data(
                            type = "xa_xApi",
                            attributes = AttributesBillFidelity(uid = uid, codice = codice, matricola = matricola)
                        )
                    )
                )
            }
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkEError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkEError.SERIALIZATION)
        } catch (e: Exception) {
            return Result.Error(NetworkEError.NO_INTERNET)
        }

        return when (response.status.value) {
            in 200..299 -> {
                val data = response.body<BillFidelity>()
                Result.Success(data)
            }

            401 -> Result.Error(NetworkEError.UNAUTHORIZED)
            409 -> Result.Error(NetworkEError.CONFLICT)
            408 -> Result.Error(NetworkEError.REQUEST_TIMEOUT)
            413 -> Result.Error(NetworkEError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkEError.SERVER_ERROR)
            else -> Result.Error(NetworkEError.UNKNOWN)
        }
    }

    suspend fun getOffers(uid: String): Result<Offers, NetworkEError> {
        val response = try {
            httpClient.post(urlString = "${BuildConfig.END_POINT}/offers") {
                contentType(ContentType.Application.Json)
                setBody(DataUid(uid = uid))
            }
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkEError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkEError.SERIALIZATION)
        } catch (e: Exception) {
            return Result.Error(NetworkEError.NO_INTERNET)
        }

        return when (response.status.value) {
            in 200..299 -> {
                val data = response.body<Offers>()
                Result.Success(data)
            }

            401 -> Result.Error(NetworkEError.UNAUTHORIZED)
            409 -> Result.Error(NetworkEError.CONFLICT)
            408 -> Result.Error(NetworkEError.REQUEST_TIMEOUT)
            413 -> Result.Error(NetworkEError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkEError.SERVER_ERROR)
            else -> Result.Error(NetworkEError.UNKNOWN)
        }
    }
}