package com.reysl.uroboros.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reysl.uroboros.R
import com.reysl.uroboros.data.MaterialQuiz
import com.reysl.uroboros.ui.theme.acherusFeral
import com.reysl.uroboros.ui.theme.appGreen
import com.reysl.uroboros.ui.theme.appLightGreenSurface
import com.reysl.uroboros.viewmodel.QuizErrorType
import com.reysl.uroboros.viewmodel.QuizUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialQuizContent(
    uiState: QuizUiState,
    onGenerate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        when (uiState) {
            QuizUiState.LoadingExisting,
            QuizUiState.Generating -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(color = appGreen())
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(
                            if (uiState is QuizUiState.Generating) {
                                R.string.quiz_generating
                            } else {
                                R.string.quiz_loading
                            },
                        ),
                        fontFamily = acherusFeral,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            is QuizUiState.Idle -> {
                QuizEmptyState(
                    remainingGenerations = uiState.remainingGenerations,
                    onGenerate = onGenerate,
                )
            }

            is QuizUiState.Ready -> {
                QuizQuestionView(
                    quiz = uiState.quiz,
                    remainingGenerations = uiState.remainingGenerations,
                    onGenerate = onGenerate,
                )
            }

            is QuizUiState.Error -> {
                QuizErrorState(
                    errorType = uiState.type,
                    remainingGenerations = uiState.remainingGenerations,
                    detailMessage = uiState.detailMessage,
                    onGenerate = onGenerate,
                )
            }
        }
    }
}

@Composable
private fun QuizEmptyState(
    remainingGenerations: Int,
    onGenerate: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.quiz_empty_hint),
            fontFamily = acherusFeral,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.quiz_remaining_today, remainingGenerations),
            fontFamily = acherusFeral,
            fontSize = 13.sp,
            color = appGreen(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        GenerateQuizButton(
            onClick = onGenerate,
            enabled = remainingGenerations > 0,
        )
    }
}

@Composable
private fun QuizErrorState(
    errorType: QuizErrorType,
    remainingGenerations: Int,
    detailMessage: String?,
    onGenerate: () -> Unit,
) {
    val messageRes = when (errorType) {
        QuizErrorType.MissingToken -> R.string.quiz_error_missing_token
        QuizErrorType.DailyLimit -> R.string.quiz_error_daily_limit
        QuizErrorType.MaterialTooShort -> R.string.quiz_error_material_short
        QuizErrorType.EmptyResponse,
        QuizErrorType.InvalidResponse -> R.string.quiz_error_invalid_response
        QuizErrorType.ApiUnauthorized -> R.string.quiz_error_api_unauthorized
        QuizErrorType.ApiForbidden -> R.string.quiz_error_api_forbidden
        QuizErrorType.ApiError -> R.string.quiz_error_api
        QuizErrorType.Network -> R.string.quiz_error_network
        QuizErrorType.Unknown -> R.string.quiz_error_unknown
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(messageRes),
            fontFamily = acherusFeral,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.error,
        )
        if (!detailMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = detailMessage,
                fontFamily = acherusFeral,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (errorType != QuizErrorType.DailyLimit) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.quiz_remaining_today, remainingGenerations),
                fontFamily = acherusFeral,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (errorType != QuizErrorType.DailyLimit &&
            errorType != QuizErrorType.MissingToken &&
            errorType != QuizErrorType.ApiForbidden &&
            errorType != QuizErrorType.ApiUnauthorized
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            GenerateQuizButton(onClick = onGenerate)
        }
    }
}

@Composable
private fun QuizQuestionView(
    quiz: MaterialQuiz,
    remainingGenerations: Int,
    onGenerate: () -> Unit,
) {
    var selectedIndex by rememberSaveable(quiz.id) { mutableIntStateOf(-1) }

    Text(
        text = stringResource(R.string.quiz_question_label),
        fontFamily = acherusFeral,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = appGreen(),
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = quiz.question,
        fontFamily = acherusFeral,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Medium,
    )

    Spacer(modifier = Modifier.height(16.dp))

    quiz.options().forEachIndexed { index, option ->
        QuizOptionCard(
            text = option,
            index = index,
            selectedIndex = selectedIndex,
            correctIndex = quiz.correctOptionIndex,
            onClick = { if (selectedIndex == -1) selectedIndex = index },
        )
        Spacer(modifier = Modifier.height(8.dp))
    }

    if (selectedIndex != -1) {
        Spacer(modifier = Modifier.height(8.dp))
        val isCorrect = selectedIndex == quiz.correctOptionIndex
        Text(
            text = stringResource(
                if (isCorrect) R.string.quiz_answer_correct else R.string.quiz_answer_incorrect,
            ),
            fontFamily = acherusFeral,
            fontWeight = FontWeight.Bold,
            color = if (isCorrect) appGreen() else MaterialTheme.colorScheme.error,
        )
        if (!isCorrect) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.quiz_correct_answer, quiz.answer),
                fontFamily = acherusFeral,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    Spacer(modifier = Modifier.height(20.dp))
    Text(
        text = stringResource(R.string.quiz_remaining_today, remainingGenerations),
        fontFamily = acherusFeral,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedButton(
        onClick = {
            selectedIndex = -1
            onGenerate()
        },
        enabled = remainingGenerations > 0,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ) {
        Text(
            text = stringResource(R.string.quiz_regenerate),
            fontFamily = acherusFeral,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun QuizOptionCard(
    text: String,
    index: Int,
    selectedIndex: Int,
    correctIndex: Int,
    onClick: () -> Unit,
) {
    val labels = listOf("A", "B", "C", "D")
    val isSelected = selectedIndex == index
    val isRevealed = selectedIndex != -1
    val isCorrectOption = index == correctIndex

    val containerColor = when {
        !isRevealed -> MaterialTheme.colorScheme.surface
        isCorrectOption -> appLightGreenSurface()
        isSelected -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surface
    }
    val borderColor = when {
        !isRevealed && isSelected -> appGreen()
        isRevealed && isCorrectOption -> appGreen()
        isRevealed && isSelected -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    Card(
        onClick = onClick,
        enabled = !isRevealed,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor),
    ) {
        Text(
            text = "${labels[index]}. $text",
            fontFamily = acherusFeral,
            fontSize = 15.sp,
            lineHeight = 21.sp,
            modifier = Modifier.padding(14.dp),
        )
    }
}

@Composable
private fun GenerateQuizButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = appGreen(),
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        Text(
            text = stringResource(R.string.quiz_generate),
            fontFamily = acherusFeral,
            fontWeight = FontWeight.Bold,
        )
    }
}
