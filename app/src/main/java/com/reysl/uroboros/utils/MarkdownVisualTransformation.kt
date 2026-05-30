package com.reysl.uroboros.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp

class MarkdownVisualTransformation(
    private val codeBlockBackground: Color,
    private val codeBlockTextColor: Color,
    private val fenceColor: Color,
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val blocks = MarkdownRenderer.findFencedCodeBlockRanges(text.text)
        if (blocks.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val transformed = buildAnnotatedString {
            append(text)
            blocks.forEach { block ->
                styleRange(
                    range = block.openingFenceRange,
                    textLength = text.length,
                    style = fenceStyle(),
                    endInclusive = true,
                )
                block.closingFenceRange?.let { closing ->
                    styleRange(
                        range = closing,
                        textLength = text.length,
                        style = fenceStyle(),
                        endInclusive = true,
                    )
                }
                if (block.contentRange.first <= block.contentRange.last) {
                    styleRange(
                        range = block.contentRange,
                        textLength = text.length,
                        style = SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            background = codeBlockBackground,
                            color = codeBlockTextColor,
                        ),
                        endInclusive = true,
                    )
                }
            }
        }

        return TransformedText(transformed, OffsetMapping.Identity)
    }

    private fun fenceStyle() = SpanStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = fenceColor,
        background = codeBlockBackground,
    )

    private fun AnnotatedString.Builder.styleRange(
        range: IntRange,
        textLength: Int,
        style: SpanStyle,
        endInclusive: Boolean = false,
    ) {
        if (range.isEmpty()) return
        val end = if (endInclusive) {
            (range.last + 1).coerceAtMost(textLength)
        } else {
            range.last.coerceAtMost(textLength)
        }
        val start = range.first.coerceAtMost(textLength)
        if (start < end) {
            addStyle(style, start, end)
        }
    }

}
