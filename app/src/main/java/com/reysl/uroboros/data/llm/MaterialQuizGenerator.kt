package com.reysl.uroboros.data.llm

import com.reysl.uroboros.data.MaterialQuiz

class MaterialQuizGenerator(
    private val api: HuggingFaceApi = HuggingFaceClient.api,
    private val parser: LlmQuizResponseParser = HuggingFaceClient.quizResponseParser,
) {

    suspend fun generate(
        noteId: Long,
        title: String,
        materialText: String,
    ): Result<MaterialQuiz> {
        if (LlmConfig.huggingFaceToken.isEmpty()) {
            return Result.failure(IllegalStateException("missing_token"))
        }

        val trimmedMaterial = materialText.trim()
        if (trimmedMaterial.length < 40) {
            return Result.failure(IllegalStateException("material_too_short"))
        }

        val content = trimmedMaterial.take(LlmConfig.MAX_MATERIAL_CHARS)
        val prompt = buildPrompt(title = title, material = content)
        val request = ChatRequest(
            model = LlmConfig.DEFAULT_MODEL,
            messages = listOf(ChatMessage(role = "user", content = prompt)),
            maxTokens = LlmConfig.DEFAULT_MAX_TOKENS,
            temperature = LlmConfig.DEFAULT_TEMPERATURE,
        )

        return runCatching {
            val response = api.chatCompletions(request)
            if (response.choices.isEmpty()) {
                throw IllegalStateException("empty_response")
            }
            val raw = response.choices.first().message.content
            val payload = parser.parse(raw)
                ?: throw IllegalStateException("invalid_response")

            validatePayload(payload)
            payload.toMaterialQuiz(noteId)
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(LlmErrorMapper.wrap(it)) },
        )
    }

    private fun validatePayload(payload: GeneratedQuizPayload) {
        require(payload.question.isNotBlank()) { "invalid_response" }
        require(payload.answer.isNotBlank()) { "invalid_response" }
        require(payload.options.size == 4) { "invalid_response" }
        require(payload.options.all { it.isNotBlank() }) { "invalid_response" }
        require(payload.correctIndex in 0..3) { "invalid_response" }
    }

    private fun buildPrompt(title: String, material: String): String = """
        Ты создаёшь один учебный вопрос с вариантами ответа по материалу пользователя.

        Правила:
        - Используй только факты из текста материала, ничего не выдумывай.
        - Язык вопроса и ответов = язык материала.
        - Вопрос должен проверять понимание, а не мелкие детали форматирования.
        - Ровно 4 варианта ответа, только один правильный.
        - Неправильные варианты должны быть правдоподобными, но явно неверными.
        - Верни строго JSON без текста вне JSON.

        Формат:
        {
          "question": "текст вопроса",
          "answer": "правильный ответ",
          "options": ["вариант 1", "вариант 2", "вариант 3", "вариант 4"],
          "correct_index": 0
        }

        Название материала: "$title"

        Материал:
        """
        .trimIndent() + "\n\"\"\"\n$material\n\"\"\""
}

private fun GeneratedQuizPayload.toMaterialQuiz(noteId: Long): MaterialQuiz {
    val normalizedOptions = options.map { it.trim() }
    return MaterialQuiz(
        noteId = noteId,
        question = question.trim(),
        answer = answer.trim(),
        optionA = normalizedOptions[0],
        optionB = normalizedOptions[1],
        optionC = normalizedOptions[2],
        optionD = normalizedOptions[3],
        correctOptionIndex = correctIndex,
    )
}
