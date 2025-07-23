package it.puntoettore.fidelity.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import it.puntoettore.fidelity.api.datamodel.Attributes
import it.puntoettore.fidelity.api.datamodel.AttributesBillFidelity
import it.puntoettore.fidelity.api.datamodel.AttributesTicket
import it.puntoettore.fidelity.api.datamodel.AttributesVecchioCliente
import it.puntoettore.fidelity.api.datamodel.BillFidelity
import it.puntoettore.fidelity.api.datamodel.CreditiFidelity
import it.puntoettore.fidelity.api.datamodel.Data
import it.puntoettore.fidelity.api.datamodel.DataWrapper
import it.puntoettore.fidelity.api.datamodel.DatiFidelity
import it.puntoettore.fidelity.api.datamodel.ResponseGeneric
import it.puntoettore.fidelity.api.datamodel.ResponseVecchioCliente
import it.puntoettore.fidelity.api.util.NetworkEError
import it.puntoettore.fidelity.api.util.NetworkError
import it.puntoettore.fidelity.api.util.Result
import it.puntoettore.fidelity.custom.BuildConfig
import kotlinx.serialization.SerializationException

class ApiDataClientNextLogin(
    private val httpClient: HttpClient
) {
    private lateinit var uid: String

    // impostato al primo caricamento in ViewModel
    // serve perchè ciascuna chiamata richiede di specificare nuovamente uid
    // anche se ricevabile da Token... logica Marco!
    fun setUid(_uid: String) {
        uid = _uid
    }

    suspend fun postDatiFidelity(): Result<DatiFidelity, NetworkError> {
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
            return Result.Error(NetworkError(message = "", error = NetworkEError.NO_INTERNET))
        } catch (e: SerializationException) {
            return Result.Error(NetworkError(message = "", error = NetworkEError.SERIALIZATION))
        } catch (e: Exception) {
            return Result.Error(NetworkError(message = "", error = NetworkEError.NO_INTERNET))
        }

        return when (response.status.value) {
            in 200..299 -> {
                val data = response.body<DatiFidelity>()
                Result.Success(data)
            }

            401 -> {
                val data = response.body<ResponseGeneric>()
                Result.Error(
                    NetworkError(
                        message = data.message ?: "",
                        error = NetworkEError.UNAUTHORIZED
                    )
                )
            }

            409 -> Result.Error(NetworkError(message = "", error = NetworkEError.CONFLICT))
            408 -> Result.Error(NetworkError(message = "", error = NetworkEError.REQUEST_TIMEOUT))
            413 -> Result.Error(NetworkError(message = "", error = NetworkEError.PAYLOAD_TOO_LARGE))
            in 500..599 -> Result.Error(
                NetworkError(
                    message = "",
                    error = NetworkEError.SERVER_ERROR
                )
            )

            else -> Result.Error(NetworkError(message = "", error = NetworkEError.UNKNOWN))
        }
    }

    suspend fun postCreditiFidelity(): Result<List<CreditiFidelity>, NetworkError> {
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
            return Result.Error(NetworkError(message = "", error = NetworkEError.NO_INTERNET))
        } catch (e: SerializationException) {
            return Result.Error(NetworkError(message = "", error = NetworkEError.SERIALIZATION))
        } catch (e: Exception) {
            return Result.Error(NetworkError(message = "", error = NetworkEError.NO_INTERNET))
        }

        return when (response.status.value) {
            in 200..299 -> {
                val data = response.body<List<CreditiFidelity>>()
                Result.Success(data)
            }

            401 -> {
                val data = response.body<ResponseGeneric>()
                Result.Error(
                    NetworkError(
                        message = data.message ?: "",
                        error = NetworkEError.UNAUTHORIZED
                    )
                )
            }

            409 -> Result.Error(NetworkError(message = "", error = NetworkEError.CONFLICT))
            408 -> Result.Error(NetworkError(message = "", error = NetworkEError.REQUEST_TIMEOUT))
            413 -> Result.Error(NetworkError(message = "", error = NetworkEError.PAYLOAD_TOO_LARGE))
            in 500..599 -> Result.Error(
                NetworkError(
                    message = "",
                    error = NetworkEError.SERVER_ERROR
                )
            )

            else -> Result.Error(NetworkError(message = "", error = NetworkEError.UNKNOWN))
        }
    }

    suspend fun postBillFidelity(
        codice: String,
        matricola: String
    ): Result<BillFidelity, NetworkError> {
        val response = try {
            httpClient.post(urlString = "${BuildConfig.END_POINT}/index.php?entryPoint=billFidelity") {
                contentType(ContentType.Application.Json)
                setBody(
                    DataWrapper(
                        data = Data(
                            type = "xa_xApi",
                            attributes = AttributesBillFidelity(
                                uid = uid,
                                codice = codice,
                                matricola = matricola
                            )
                        )
                    )
                )
            }
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkError(message = "", error = NetworkEError.NO_INTERNET))
        } catch (e: SerializationException) {
            return Result.Error(NetworkError(message = "", error = NetworkEError.SERIALIZATION))
        } catch (e: Exception) {
            return Result.Error(NetworkError(message = "", error = NetworkEError.NO_INTERNET))
        }

        return when (response.status.value) {
            in 200..299 -> {
                val data = response.body<BillFidelity>()
                Result.Success(data)
            }

            401 -> {
                val data = response.body<ResponseGeneric>()
                Result.Error(
                    NetworkError(
                        message = data.message ?: "",
                        error = NetworkEError.UNAUTHORIZED
                    )
                )
            }

            409 -> Result.Error(NetworkError(message = "", error = NetworkEError.CONFLICT))
            408 -> Result.Error(NetworkError(message = "", error = NetworkEError.REQUEST_TIMEOUT))
            413 -> Result.Error(NetworkError(message = "", error = NetworkEError.PAYLOAD_TOO_LARGE))
            in 500..599 -> Result.Error(
                NetworkError(
                    message = "",
                    error = NetworkEError.SERVER_ERROR
                )
            )

            else -> Result.Error(NetworkError(message = "", error = NetworkEError.UNKNOWN))
        }
    }

    suspend fun postVecchioCliente(oldId: String): Result<ResponseVecchioCliente, NetworkError> {
        val response = try {
            httpClient.post(urlString = "${BuildConfig.END_POINT}/index.php?entryPoint=vecchioCliente") {
                contentType(ContentType.Application.Json)
                setBody(
                    DataWrapper(
                        data = Data(
                            type = "xa_xApi",
                            attributes = AttributesVecchioCliente(uid = uid, oldId = oldId)
                        )
                    )
                )
            }
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkError(message = "", error = NetworkEError.NO_INTERNET))
        } catch (e: SerializationException) {
            return Result.Error(NetworkError(message = "", error = NetworkEError.SERIALIZATION))
        } catch (e: Exception) {
            return Result.Error(NetworkError(message = "", error = NetworkEError.NO_INTERNET))
        }

        return when (response.status.value) {
            in 200..299 -> {
                val data = response.body<ResponseVecchioCliente>()
                Result.Success(data)
            }

            401 -> {
                val data = response.body<ResponseGeneric>()
                Result.Error(
                    NetworkError(
                        message = data.message ?: "",
                        error = NetworkEError.UNAUTHORIZED
                    )
                )
            }

            409 -> Result.Error(NetworkError(message = "", error = NetworkEError.CONFLICT))
            408 -> Result.Error(NetworkError(message = "", error = NetworkEError.REQUEST_TIMEOUT))
            413 -> Result.Error(NetworkError(message = "", error = NetworkEError.PAYLOAD_TOO_LARGE))
            in 500..599 -> Result.Error(
                NetworkError(
                    message = "",
                    error = NetworkEError.SERVER_ERROR
                )
            )

            else -> Result.Error(NetworkError(message = "", error = NetworkEError.UNKNOWN))
        }
    }

    suspend fun postTicket(ticket: String): Result<ResponseGeneric, NetworkError> {
        val response = try {
            httpClient.post(urlString = "${BuildConfig.END_POINT}/index.php?entryPoint=newTicket") {
                contentType(ContentType.Application.Json)
                setBody(
                    DataWrapper(
                        data = Data(
                            type = "xa_xApi",
                            attributes = AttributesTicket(uid = uid, msg = ticket)
                        )
                    )
                )
            }
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkError(message = "", error = NetworkEError.NO_INTERNET))
        } catch (e: SerializationException) {
            return Result.Error(NetworkError(message = "", error = NetworkEError.SERIALIZATION))
        } catch (e: Exception) {
            return Result.Error(NetworkError(message = "", error = NetworkEError.NO_INTERNET))
        }

        return when (response.status.value) {
            in 200..299 -> {
                val data = response.body<ResponseGeneric>()
                Result.Success(data)
            }

            401 -> {
                val data = response.body<ResponseGeneric>()
                Result.Error(
                    NetworkError(
                        message = data.message ?: "",
                        error = NetworkEError.UNAUTHORIZED
                    )
                )
            }

            409 -> Result.Error(NetworkError(message = "", error = NetworkEError.CONFLICT))
            408 -> Result.Error(NetworkError(message = "", error = NetworkEError.REQUEST_TIMEOUT))
            413 -> Result.Error(NetworkError(message = "", error = NetworkEError.PAYLOAD_TOO_LARGE))
            in 500..599 -> Result.Error(
                NetworkError(
                    message = "",
                    error = NetworkEError.SERVER_ERROR
                )
            )

            else -> Result.Error(NetworkError(message = "", error = NetworkEError.UNKNOWN))
        }
    }
}