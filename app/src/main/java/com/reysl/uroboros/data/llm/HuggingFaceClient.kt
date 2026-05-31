package com.reysl.uroboros.data.llm

import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object HuggingFaceClient {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val authInterceptor = Interceptor { chain ->
        val token = LlmConfig.huggingFaceToken
        val request = if (token.isNotEmpty()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(LlmConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api: HuggingFaceApi = retrofit.create(HuggingFaceApi::class.java)

    val quizResponseParser = LlmQuizResponseParser(moshi)
}

class LlmQuizResponseParser(moshi: Moshi) {

    private val adapter = moshi.adapter(GeneratedQuizPayload::class.java)

    fun parse(rawText: String): GeneratedQuizPayload? {
        val json = extractJsonObject(rawText)
        return parseLenient(json)
    }

    private fun extractJsonObject(text: String): String {
        val trimmed = text.trim()
        val start = trimmed.indexOf('{')
        val end = trimmed.lastIndexOf('}')
        if (start != -1 && end != -1 && start < end) {
            return trimmed.substring(start, end + 1)
        }
        return trimmed
    }

    private fun parseLenient(json: String): GeneratedQuizPayload? {
        val buffer = Buffer().writeUtf8(json)
        val reader = JsonReader.of(buffer)
        reader.isLenient = true
        return adapter.fromJson(reader)
    }
}
