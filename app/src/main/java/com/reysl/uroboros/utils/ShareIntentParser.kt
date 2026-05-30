package com.reysl.uroboros.utils

import android.content.Intent
import androidx.core.text.HtmlCompat

object ShareIntentParser {

    private val urlRegex = Regex("https?://\\S+")
    private val hrefRegex = Regex("""href=["']([^"']+)["']""", RegexOption.IGNORE_CASE)

    fun parse(intent: Intent?): SharePayload? {
        if (intent == null) return null

        val action = intent.action ?: return null
        if (action != Intent.ACTION_SEND) return null

        val mimeType = intent.type
        if (mimeType != null && !mimeType.startsWith("text/")) return null

        val rawText = extractText(intent) ?: return null
        if (rawText.isBlank()) return null

        val subject = intent.getStringExtra(Intent.EXTRA_SUBJECT)?.trim().orEmpty()
        return buildPayload(rawText = rawText, subject = subject)
    }

    fun isUrl(text: String): Boolean = urlRegex.matches(text.trim())

    fun extractUrl(text: String): String? = urlRegex.find(text.trim())?.value

    private fun extractText(intent: Intent): String? {
        val extra = intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()?.trim().orEmpty()
        if (extra.isNotEmpty()) {
            return normalizeText(extra)
        }

        intent.clipData?.let { clip ->
            if (clip.itemCount > 0) {
                val itemText = clip.getItemAt(0).text?.toString()?.trim().orEmpty()
                if (itemText.isNotEmpty()) {
                    return normalizeText(itemText)
                }
            }
        }

        return null
    }

    private fun normalizeText(text: String): String {
        if (text.contains('<') && text.contains('>')) {
            return extractFromHtml(text)
        }
        return text.trim()
    }

    private fun extractFromHtml(html: String): String {
        val href = hrefRegex.find(html)?.groupValues?.getOrNull(1)?.trim()
        val plain = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY).toString().trim()

        return when {
            href != null && plain.isNotBlank() && plain != href -> "$plain\n$href"
            href != null -> href
            else -> plain
        }
    }

    private fun buildPayload(rawText: String, subject: String): SharePayload {
        val url = urlRegex.find(rawText)?.value
        val firstLine = rawText.lines().firstOrNull { it.isNotBlank() }.orEmpty()

        val title = when {
            subject.isNotBlank() -> subject
            url != null && !isUrl(firstLine) -> firstLine
            url != null -> titleFromUrl(url)
            else -> firstLine
        }.take(25)

        val description = extractDescription(rawText = rawText, subject = subject, url = url).take(500)

        val tag = when {
            url?.contains("t.me", ignoreCase = true) == true -> "Telegram"
            url != null -> "Ссылка"
            else -> "Импорт"
        }

        val body = buildMaterialBody(rawText = rawText, url = url, subject = subject)

        return SharePayload(
            suggestedTitle = title.ifBlank { "Новый материал" },
            suggestedDescription = description,
            rawText = body,
            suggestedTag = tag,
            sourceUrl = url
        )
    }

    /**
     * Тело материала: всегда включает ссылку и текст, если они есть.
     */
    private fun buildMaterialBody(rawText: String, url: String?, subject: String): String {
        val lines = rawText.lines().map { it.trim() }.filter { it.isNotBlank() }.toMutableList()

        if (url != null && lines.none { it.contains(url) }) {
            lines.add(url)
        }

        val uniqueLines = lines.distinct()
        return uniqueLines.joinToString("\n")
    }

    private fun extractDescription(rawText: String, subject: String, url: String?): String {
        val candidates = when {
            subject.isNotBlank() && subject != rawText -> {
                rawText.removePrefix(subject).trim().lines()
            }
            url != null -> {
                rawText.replace(url, "").trim().lines()
            }
            else -> rawText.lines().drop(1)
        }

        return candidates
            .map { it.trim() }
            .firstOrNull { line ->
                line.isNotBlank() &&
                    line != subject &&
                    !isUrl(line) &&
                    line != url
            }
            .orEmpty()
    }

    private fun titleFromUrl(url: String): String {
        val withoutProtocol = url.removePrefix("https://").removePrefix("http://")
        val host = withoutProtocol.substringBefore('/').substringBefore('?')
        return host.ifBlank { url }.take(25)
    }
}
