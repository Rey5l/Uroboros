package com.reysl.uroboros.data.llm

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class LlmErrorMapperTest {

    @Test
    fun `toApiException parses error field from JSON body`() {
        val body = """{"error":"Permission denied"}"""
        val response = Response.error<Any>(
            403,
            body.toResponseBody("application/json".toMediaType()),
        )
        val exception = HttpException(response)

        val apiException = LlmErrorMapper.toApiException(exception)

        assertTrue(apiException is LlmApiException)
        assertEquals(403, apiException.httpCode)
        assertTrue(apiException.serverMessage.contains("Permission denied"))
    }

    @Test
    fun `isNetworkIssue detects UnknownHostException`() {
        assertTrue(LlmErrorMapper.isNetworkIssue(UnknownHostException()))
    }

    @Test
    fun `isNetworkIssue detects SocketTimeoutException`() {
        assertTrue(LlmErrorMapper.isNetworkIssue(SocketTimeoutException()))
    }

    @Test
    fun `isNetworkIssue returns false for LlmApiException`() {
        assertFalse(LlmErrorMapper.isNetworkIssue(LlmApiException(403, "Forbidden")))
    }

    @Test
    fun `isNetworkIssue returns true for generic IOException`() {
        assertTrue(LlmErrorMapper.isNetworkIssue(IOException("generic")))
    }
}
