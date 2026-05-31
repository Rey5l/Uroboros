package com.reysl.uroboros.data.llm

import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class LlmApiException(
    val httpCode: Int,
    val serverMessage: String,
) : Exception("http_$httpCode: $serverMessage")

object LlmErrorMapper {

    fun wrap(throwable: Throwable): Throwable {
        if (throwable is LlmApiException) return throwable
        if (throwable is HttpException) {
            return toApiException(throwable)
        }
        return throwable
    }

    fun toApiException(error: HttpException): LlmApiException {
        val body = error.response()?.errorBody()?.string().orEmpty()
        val message = parseErrorMessage(body).ifBlank { error.message().orEmpty() }
        return LlmApiException(httpCode = error.code(), serverMessage = message)
    }

    private fun parseErrorMessage(body: String): String {
        if (body.isBlank()) return ""
        return runCatching {
            JSONObject(body).optString("error").takeIf { it.isNotBlank() }
                ?: JSONObject(body).optString("message").takeIf { it.isNotBlank() }
                ?: body.take(500)
        }.getOrDefault(body.take(500))
    }

    fun isNetworkIssue(throwable: Throwable): Boolean {
        return throwable is UnknownHostException ||
            throwable is SocketTimeoutException ||
            (throwable is IOException && throwable !is LlmApiException)
    }
}
