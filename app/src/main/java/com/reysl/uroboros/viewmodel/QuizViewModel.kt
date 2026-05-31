package com.reysl.uroboros.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.reysl.uroboros.components.MainApplication
import com.reysl.uroboros.data.MaterialQuiz
import com.reysl.uroboros.data.llm.LlmApiException
import com.reysl.uroboros.data.llm.LlmErrorMapper
import com.reysl.uroboros.data.llm.MaterialQuizGenerator
import com.reysl.uroboros.data.preferences.QuizGenerationLimiter
import com.reysl.uroboros.utils.MarkdownStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface QuizUiState {
    data object LoadingExisting : QuizUiState
    data class Idle(val remainingGenerations: Int) : QuizUiState
    data object Generating : QuizUiState
    data class Ready(
        val quiz: MaterialQuiz,
        val remainingGenerations: Int,
    ) : QuizUiState

    data class Error(
        val type: QuizErrorType,
        val remainingGenerations: Int,
        val detailMessage: String? = null,
    ) : QuizUiState
}

enum class QuizErrorType {
    MissingToken,
    DailyLimit,
    MaterialTooShort,
    EmptyResponse,
    InvalidResponse,
    ApiUnauthorized,
    ApiForbidden,
    ApiError,
    Network,
    Unknown,
}

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val quizDao = MainApplication.noteDatabase.getMaterialQuizDao()
    private val generator = MaterialQuizGenerator()
    private val generationLimiter = QuizGenerationLimiter(application)

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.LoadingExisting)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var currentNoteId: Long? = null

    fun loadQuiz(noteId: Long) {
        if (currentNoteId == noteId && _uiState.value !is QuizUiState.LoadingExisting) return
        currentNoteId = noteId
        viewModelScope.launch {
            _uiState.value = QuizUiState.LoadingExisting
            val quiz = withContext(Dispatchers.IO) { quizDao.getByNoteId(noteId) }
            val remaining = generationLimiter.remainingToday()
            _uiState.value = if (quiz != null) {
                QuizUiState.Ready(quiz = quiz, remainingGenerations = remaining)
            } else {
                QuizUiState.Idle(remainingGenerations = remaining)
            }
        }
    }

    fun generateQuiz(
        noteId: Long,
        title: String,
        markdownContent: String,
    ) {
        if (_uiState.value is QuizUiState.Generating) return

        if (!generationLimiter.canGenerate()) {
            _uiState.value = QuizUiState.Error(
                type = QuizErrorType.DailyLimit,
                remainingGenerations = generationLimiter.remainingToday(),
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = QuizUiState.Generating
            val plainText = MarkdownStorage.plainText(markdownContent)
            val result = withContext(Dispatchers.IO) {
                generator.generate(
                    noteId = noteId,
                    title = title,
                    materialText = plainText,
                )
            }

            result.fold(
                onSuccess = { quiz ->
                    if (!generationLimiter.tryConsumeGeneration()) {
                        _uiState.value = QuizUiState.Error(
                            type = QuizErrorType.DailyLimit,
                            remainingGenerations = generationLimiter.remainingToday(),
                        )
                        return@fold
                    }
                    withContext(Dispatchers.IO) {
                        quizDao.deleteByNoteId(noteId)
                        quizDao.upsert(quiz)
                    }
                    _uiState.value = QuizUiState.Ready(
                        quiz = quiz,
                        remainingGenerations = generationLimiter.remainingToday(),
                    )
                },
                onFailure = { error ->
                    val mapped = mapError(error)
                    _uiState.value = QuizUiState.Error(
                        type = mapped.first,
                        remainingGenerations = generationLimiter.remainingToday(),
                        detailMessage = mapped.second,
                    )
                },
            )
        }
    }

    fun remainingGenerations(): Int = generationLimiter.remainingToday()

    private fun mapError(error: Throwable): Pair<QuizErrorType, String?> {
        val message = error.message.orEmpty()
        return when {
            message == "missing_token" -> QuizErrorType.MissingToken to null
            message == "material_too_short" -> QuizErrorType.MaterialTooShort to null
            message == "empty_response" -> QuizErrorType.EmptyResponse to null
            message == "invalid_response" -> QuizErrorType.InvalidResponse to null
            error is LlmApiException -> mapApiError(error)
            LlmErrorMapper.isNetworkIssue(error) -> QuizErrorType.Network to error.localizedMessage
            else -> QuizErrorType.Unknown to error.localizedMessage
        }
    }

    private fun mapApiError(error: LlmApiException): Pair<QuizErrorType, String?> {
        val detail = "HTTP ${error.httpCode}: ${error.serverMessage}"
        val type = when (error.httpCode) {
            401 -> QuizErrorType.ApiUnauthorized
            403 -> QuizErrorType.ApiForbidden
            else -> QuizErrorType.ApiError
        }
        return type to detail
    }
}
