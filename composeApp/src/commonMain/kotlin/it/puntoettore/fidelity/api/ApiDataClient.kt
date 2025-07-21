package it.puntoettore.fidelity.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import it.puntoettore.fidelity.api.datamodel.AuthDetail
import it.puntoettore.fidelity.api.util.NetworkEError
import it.puntoettore.fidelity.api.util.Result
import it.puntoettore.fidelity.custom.BuildConfig
import kotlinx.serialization.SerializationException
import kotlin.coroutines.cancellation.CancellationException

class ApiDataClient(
    private val httpClient: HttpClient
) {
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
}