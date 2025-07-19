package it.puntoettore.fidelity.api.util

enum class NetworkEError : EError {
    REQUEST_TIMEOUT,
    UNAUTHORIZED,
    CONFLICT,
    TOO_MANY_REQUESTS,
    NO_INTERNET,
    PAYLOAD_TOO_LARGE,
    SERVER_ERROR,
    SERIALIZATION,
    NOT_FOUND,
    UNKNOWN;
}