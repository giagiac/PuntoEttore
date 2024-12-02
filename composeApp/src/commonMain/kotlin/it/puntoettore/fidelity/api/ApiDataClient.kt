package it.puntoettore.fidelity.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import it.puntoettore.fidelity.Res
import it.puntoettore.fidelity.api.datamodel.DataUid
import it.puntoettore.fidelity.api.datamodel.DataUser
import it.puntoettore.fidelity.api.datamodel.Offers
import it.puntoettore.fidelity.api.datamodel.UserDetail
import it.puntoettore.fidelity.api.util.NetworkEError
import it.puntoettore.fidelity.api.util.Result
import it.puntoettore.fidelity.url_endpoint
import kotlinx.serialization.SerializationException
import org.jetbrains.compose.resources.getString

class ApiDataClient(
    private val httpClient: HttpClient
) {

    suspend fun sendData(token: String): Result<String, NetworkEError> {
        val response = try {
            httpClient.post(urlString = "${getString(Res.string.url_endpoint)}/data") {
                contentType(ContentType.Application.Json)
                setBody(DataUser(token = token))
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
                val censoredText = response.body<String>()
                Result.Success(censoredText)
            }

            401 -> Result.Error(NetworkEError.UNAUTHORIZED)
            409 -> Result.Error(NetworkEError.CONFLICT)
            408 -> Result.Error(NetworkEError.REQUEST_TIMEOUT)
            413 -> Result.Error(NetworkEError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkEError.SERVER_ERROR)
            else -> Result.Error(NetworkEError.UNKNOWN)
        }
    }


    suspend fun getUserDetail(uid: String): Result<UserDetail, NetworkEError> {
        val response = try {
            httpClient.post(urlString = "${getString(Res.string.url_endpoint)}/userDetail") {
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
            httpClient.post(urlString = "${getString(Res.string.url_endpoint)}/offers") {
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