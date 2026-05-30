package com.reysl.uroboros.utils

object MarkdownUtils {

    fun toPlainText(markdown: String): String {
        var text = markdown
        text = text.replace(Regex("```[\\s\\S]*?```"), " ")
        text = text.replace(Regex("`[^`\n]+`"), " ")
        text = text.replace(Regex("\\[([^\\]]+)]\\([^)]+\\)"), "$1")
        text = text.replace(Regex("^#{1,6}\\s+", RegexOption.MULTILINE), "")
        text = text.replace(Regex("^>\\s?", RegexOption.MULTILINE), "")
        text = text.replace(Regex("^[-*+]\\s+", RegexOption.MULTILINE), "")
        text = text.replace(Regex("^\\d+\\.\\s+", RegexOption.MULTILINE), "")
        text = text.replace(Regex("\\*\\*([^*]+)\\*\\*"), "$1")
        text = text.replace(Regex("\\*([^*]+)\\*"), "$1")
        text = text.replace(Regex("~~([^~]+)~~"), "$1")
        text = text.replace(Regex("<[^>]+>"), "")
        text = text.replace(Regex("\\s+"), " ")
        return text.trim()
    }

    fun looksLikeHtml(text: String): Boolean {
        val trimmed = text.trimStart()
        return trimmed.startsWith("<") && trimmed.contains(">")
    }

    fun htmlToMarkdown(html: String): String {
        var result = html.trim()

        result = result.replace(
            Regex("<pre><code>([\\s\\S]*?)</code></pre>", RegexOption.IGNORE_CASE)
        ) { match ->
            val code = decodeHtmlEntities(stripInnerTags(match.groupValues[1]))
            "```\n$code\n```"
        }

        result = result.replace(
            Regex("<code>([\\s\\S]*?)</code>", RegexOption.IGNORE_CASE)
        ) { match ->
            "`${decodeHtmlEntities(stripInnerTags(match.groupValues[1]))}`"
        }

        result = result.replace(
            Regex("<a\\s+[^>]*href=\"([^\"]*)\"[^>]*>([\\s\\S]*?)</a>", RegexOption.IGNORE_CASE)
        ) { match ->
            val url = match.groupValues[1]
            val label = decodeHtmlEntities(stripInnerTags(match.groupValues[2]))
            "[$label]($url)"
        }

        for (level in 6 downTo 1) {
            result = result.replace(
                Regex("<h$level[^>]*>([\\s\\S]*?)</h$level>", RegexOption.IGNORE_CASE)
            ) { match ->
                "${"#".repeat(level)} ${decodeHtmlEntities(stripInnerTags(match.groupValues[1]))}\n\n"
            }
        }

        result = result.replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
        result = result.replace(Regex("</p>", RegexOption.IGNORE_CASE), "\n\n")
        result = result.replace(Regex("<p[^>]*>", RegexOption.IGNORE_CASE), "")

        result = result.replace(
            Regex("<(?:strong|b)>([\\s\\S]*?)</(?:strong|b)>", RegexOption.IGNORE_CASE)
        ) { match -> "**${decodeHtmlEntities(stripInnerTags(match.groupValues[1]))}**" }

        result = result.replace(
            Regex("<(?:em|i)>([\\s\\S]*?)</(?:em|i)>", RegexOption.IGNORE_CASE)
        ) { match -> "*${decodeHtmlEntities(stripInnerTags(match.groupValues[1]))}*" }

        result = result.replace(
            Regex("<(?:del|s|strike)>([\\s\\S]*?)</(?:del|s|strike)>", RegexOption.IGNORE_CASE)
        ) { match -> "~~${decodeHtmlEntities(stripInnerTags(match.groupValues[1]))}~~" }

        result = result.replace(Regex("<[^>]+>"), "")
        result = decodeHtmlEntities(result)
        result = result.replace(Regex("\\n{3,}"), "\n\n")
        return result.trim()
    }

    private fun stripInnerTags(text: String): String = text.replace(Regex("<[^>]+>"), "")

    private fun decodeHtmlEntities(text: String): String = text
        .replace("&nbsp;", " ")
        .replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
}
