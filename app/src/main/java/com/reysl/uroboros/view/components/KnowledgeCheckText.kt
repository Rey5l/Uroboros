package com.reysl.uroboros.view.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.reysl.uroboros.ui.theme.appGreen
import com.reysl.uroboros.ui.theme.appKnowledgeCheckHiddenBg
import com.reysl.uroboros.utils.KnowledgeCheckSegment
import com.reysl.uroboros.ui.theme.acherusFeral

private const val WORD_ANNOTATION = "hidden_word"

@Composable
fun KnowledgeCheckText(
    segments: List<KnowledgeCheckSegment>,
    revealedWordIds: Set<Int>,
    onRevealWord: (Int) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(
        fontFamily = acherusFeral,
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onBackground
    ),
) {
    val hiddenBackground = appKnowledgeCheckHiddenBg()
    val hiddenForeground = appGreen()

    val annotated = buildKnowledgeCheckAnnotatedString(
        segments = segments,
        revealedWordIds = revealedWordIds,
        hiddenBackground = hiddenBackground,
        hiddenForeground = hiddenForeground,
        revealedColor = MaterialTheme.colorScheme.onBackground
    )

    ClickableText(
        text = annotated,
        style = textStyle,
        modifier = modifier,
        onClick = { offset ->
            annotated
                .getStringAnnotations(WORD_ANNOTATION, offset, offset)
                .firstOrNull()
                ?.let { onRevealWord(it.item.toInt()) }
        }
    )
}

private fun buildKnowledgeCheckAnnotatedString(
    segments: List<KnowledgeCheckSegment>,
    revealedWordIds: Set<Int>,
    hiddenBackground: Color,
    hiddenForeground: Color,
    revealedColor: Color,
): AnnotatedString = buildAnnotatedString {
    segments.forEach { segment ->
        when (segment) {
            is KnowledgeCheckSegment.Plain -> append(segment.text)
            is KnowledgeCheckSegment.HiddenWord -> {
                val revealed = revealedWordIds.contains(segment.id)
                pushStringAnnotation(WORD_ANNOTATION, segment.id.toString())
                withStyle(
                    SpanStyle(
                        background = if (revealed) Color.Transparent else hiddenBackground,
                        color = if (revealed) revealedColor else hiddenForeground,
                        fontWeight = if (revealed) FontWeight.Normal else FontWeight.Bold
                    )
                ) {
                    append(
                        if (revealed) {
                            segment.text
                        } else {
                            "·".repeat(segment.text.length.coerceIn(3, 16))
                        }
                    )
                }
                pop()
            }
        }
    }
}
