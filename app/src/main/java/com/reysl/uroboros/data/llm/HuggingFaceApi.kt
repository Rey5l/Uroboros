package com.reysl.uroboros.data.llm

import com.reysl.uroboros.BuildConfig
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface HuggingFaceApi {

    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun chatCompletions(
        @Body request: ChatRequest,
    ): ChatResponse
}

object LlmConfig {
    const val BASE_URL = "https://router.huggingface.co/"
    const val DEFAULT_MODEL = "Qwen/Qwen2.5-7B-Instruct"
    const val DEFAULT_MAX_TOKENS = 450
    const val DEFAULT_TEMPERATURE = 0.3
    const val MAX_MATERIAL_CHARS = 6000

    val huggingFaceToken: String
        get() = BuildConfig.HUGGING_FACE_TOKEN.trim()
}
