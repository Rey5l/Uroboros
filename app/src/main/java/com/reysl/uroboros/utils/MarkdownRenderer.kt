package com.reysl.uroboros.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

sealed class MarkdownBlock {
    data class RichText(val annotated: AnnotatedString) : MarkdownBlock()

    data class FencedCode(
        val language: String?,
        val code: String,
    ) : MarkdownBlock()
}

data class FencedCodeBlockRange(
    val language: String?,
    val contentRange: IntRange,
    val openingFenceRange: IntRange,
    val closingFenceRange: IntRange?,
)

object MarkdownRenderer {

    fun parseBlocks(
        markdown: String,
        accentColor: Color,
        inlineCodeBackground: Color,
        textColor: Color,
        fontFamily: FontFamily,
    ): List<MarkdownBlock> {
        val blocks = mutableListOf<MarkdownBlock>()
        val lines = markdown.split('\n')
        val richTextBuffer = StringBuilder()
        var inCodeBlock = false
        var codeLanguage: String? = null
        val codeLines = mutableListOf<String>()

        fun flushRichText() {
            if (richTextBuffer.isEmpty()) return
            blocks.add(
                MarkdownBlock.RichText(
                    toAnnotatedString(
                        markdown = richTextBuffer.toString().trimEnd('\n'),
                        accentColor = accentColor,
                        inlineCodeBackground = inlineCodeBackground,
                        textColor = textColor,
                        fontFamily = fontFamily,
                    )
                )
            )
            richTextBuffer.clear()
        }

        for (line in lines) {
            val trimmedStart = line.trimStart()
            if (trimmedStart.startsWith("```")) {
                if (inCodeBlock) {
                    blocks.add(
                        MarkdownBlock.FencedCode(
                            language = codeLanguage?.takeIf { it.isNotBlank() },
                            code = codeLines.joinToString("\n"),
                        )
                    )
                    codeLines.clear()
                    codeLanguage = null
                    inCodeBlock = false
                } else {
                    flushRichText()
                    inCodeBlock = true
                    codeLanguage = trimmedStart.removePrefix("```").trim().ifBlank { null }
                }
                continue
            }

            if (inCodeBlock) {
                codeLines.add(line)
            } else {
                if (richTextBuffer.isNotEmpty()) richTextBuffer.append('\n')
                richTextBuffer.append(line)
            }
        }

        if (inCodeBlock) {
            blocks.add(
                MarkdownBlock.FencedCode(
                    language = codeLanguage?.takeIf { it.isNotBlank() },
                    code = codeLines.joinToString("\n"),
                )
            )
        } else {
            flushRichText()
        }

        return blocks
    }

    fun findFencedCodeBlockRanges(text: String): List<FencedCodeBlockRange> {
        val ranges = mutableListOf<FencedCodeBlockRange>()
        val lines = text.split('\n')
        var index = 0
        var i = 0

        while (i < lines.size) {
            val line = lines[i]
            val lineStart = index
            val lineEnd = index + line.length

            if (line.trimStart().startsWith("```")) {
                val openingFenceRange = lineStart until lineEnd + 1
                val language = line.trim().removePrefix("```").trim().ifBlank { null }
                val contentStart = lineEnd + 1
                i++
                index = contentStart
                val contentLines = mutableListOf<String>()

                while (i < lines.size && !lines[i].trimStart().startsWith("```")) {
                    contentLines.add(lines[i])
                    index += lines[i].length + 1
                    i++
                }

                val content = contentLines.joinToString("\n")
                val contentRange = contentStart until (contentStart + content.length)

                val closingFenceRange = if (i < lines.size) {
                    val closeStart = index
                    val closeEnd = closeStart + lines[i].length
                    index = closeEnd + 1
                    closeStart until closeEnd + 1
                } else {
                    null
                }

                ranges.add(
                    FencedCodeBlockRange(
                        language = language,
                        contentRange = contentRange,
                        openingFenceRange = openingFenceRange,
                        closingFenceRange = closingFenceRange,
                    )
                )
                i++
                continue
            }

            index = lineEnd + 1
            i++
        }

        return ranges
    }

    fun toAnnotatedString(
        markdown: String,
        accentColor: Color,
        inlineCodeBackground: Color,
        textColor: Color,
        fontFamily: FontFamily,
    ): AnnotatedString = buildAnnotatedString {
        val lines = markdown.split('\n')
        var i = 0

        while (i < lines.size) {
            val line = lines[i]

            if (line.isBlank()) {
                append('\n')
                i++
                continue
            }

            val headingMatch = HEADING.matchEntire(line.trimStart())
            if (headingMatch != null) {
                val level = headingMatch.groupValues[1].length
                val content = headingMatch.groupValues[2]
                val headingSize = when (level) {
                    1 -> 22.sp
                    2 -> 19.sp
                    3 -> 17.sp
                    else -> 16.sp
                }
                withStyle(ParagraphStyle(lineHeight = headingSize)) {
                    withStyle(
                        SpanStyle(
                            fontFamily = fontFamily,
                            fontSize = headingSize,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                        )
                    ) {
                        appendInline(content, accentColor, inlineCodeBackground)
                    }
                }
                append('\n')
                i++
                continue
            }

            val quoteMatch = QUOTE.matchEntire(line)
            if (quoteMatch != null) {
                withStyle(
                    SpanStyle(
                        fontFamily = fontFamily,
                        fontSize = 16.sp,
                        color = accentColor,
                        fontStyle = FontStyle.Italic,
                    )
                ) {
                    append("▎ ")
                    appendInline(quoteMatch.groupValues[1], accentColor, inlineCodeBackground)
                }
                append('\n')
                i++
                continue
            }

            val bulletMatch = BULLET.matchEntire(line)
            if (bulletMatch != null) {
                withStyle(SpanStyle(fontFamily = fontFamily, fontSize = 16.sp, color = textColor)) {
                    append("• ")
                    appendInline(bulletMatch.groupValues[1], accentColor, inlineCodeBackground)
                }
                append('\n')
                i++
                continue
            }

            val numberedMatch = NUMBERED.matchEntire(line)
            if (numberedMatch != null) {
                withStyle(SpanStyle(fontFamily = fontFamily, fontSize = 16.sp, color = textColor)) {
                    append("${numberedMatch.groupValues[1]}. ")
                    appendInline(numberedMatch.groupValues[2], accentColor, inlineCodeBackground)
                }
                append('\n')
                i++
                continue
            }

            withStyle(
                SpanStyle(
                    fontFamily = fontFamily,
                    fontSize = 16.sp,
                    color = textColor,
                )
            ) {
                appendInline(line, accentColor, inlineCodeBackground)
            }
            append('\n')
            i++
        }
    }

    private fun AnnotatedString.Builder.appendInline(
        text: String,
        accentColor: Color,
        inlineCodeBackground: Color,
    ) {
        var remaining = text
        while (remaining.isNotEmpty()) {
            val next = findNextInline(remaining) ?: break
            if (next.start > 0) {
                append(remaining.substring(0, next.start))
            }
            when (next.type) {
                InlineType.BOLD -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(next.content)
                }
                InlineType.ITALIC -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(next.content)
                }
                InlineType.STRIKE -> withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                    append(next.content)
                }
                InlineType.CODE -> withStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        background = inlineCodeBackground,
                        color = accentColor,
                    )
                ) {
                    append(next.content)
                }
                InlineType.LINK -> withStyle(
                    SpanStyle(
                        color = accentColor,
                        textDecoration = TextDecoration.Underline,
                    )
                ) {
                    append(next.content)
                }
                InlineType.UNDERLINE -> withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append(next.content)
                }
            }
            remaining = remaining.substring(next.end)
        }
        if (remaining.isNotEmpty()) {
            append(remaining)
        }
    }

    private fun findNextInline(text: String): InlineMatch? {
        val candidates = listOfNotNull(
            findPattern(BOLD, text, InlineType.BOLD),
            findPattern(STRIKE, text, InlineType.STRIKE),
            findPattern(CODE, text, InlineType.CODE),
            findPattern(LINK, text, InlineType.LINK),
            findPattern(UNDERLINE, text, InlineType.UNDERLINE),
            findPattern(ITALIC, text, InlineType.ITALIC),
        )
        return candidates.minByOrNull { it.start }
    }

    private fun findPattern(regex: Regex, text: String, type: InlineType): InlineMatch? {
        val match = regex.find(text) ?: return null
        return InlineMatch(type, match.range.first, match.range.last + 1, match.groupValues[1])
    }

    private enum class InlineType {
        BOLD, ITALIC, STRIKE, CODE, LINK, UNDERLINE
    }

    private data class InlineMatch(
        val type: InlineType,
        val start: Int,
        val end: Int,
        val content: String,
    )

    private val HEADING = Regex("^(#{1,6})\\s+(.+)$")
    private val QUOTE = Regex("^>\\s?(.*)$")
    private val BULLET = Regex("^[-*+]\\s+(.*)$")
    private val NUMBERED = Regex("^(\\d+)\\.\\s+(.*)$")
    private val BOLD = Regex("\\*\\*(.+?)\\*\\*")
    private val ITALIC = Regex("(?<!\\*)\\*(?!\\*)(.+?)(?<!\\*)\\*(?!\\*)")
    private val STRIKE = Regex("~~(.+?)~~")
    private val CODE = Regex("`([^`\n]+)`")
    private val LINK = Regex("\\[([^\\]]+)]\\(([^)]+)\\)")
    private val UNDERLINE = Regex("<u>(.+?)</u>", RegexOption.IGNORE_CASE)
}
