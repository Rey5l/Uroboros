package com.reysl.uroboros.data.llm

import com.squareup.moshi.Json

data class ChatMessage(
    val role: String,
    val content: String,
)

data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    @Json(name = "max_tokens") val maxTokens: Int,
    val temperature: Double,
)

data class ChatResponse(
    val choices: List<ChatChoice>,
)

data class ChatChoice(
    val message: ChatMessageContent,
)

data class ChatMessageContent(
    val content: String,
    val role: String? = null,
)

data class GeneratedQuizPayload(
    val question: String,
    val answer: String,
    val options: List<String>,
    @Json(name = "correct_index") val correctIndex: Int,
)
