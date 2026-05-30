package com.reysl.uroboros.utils

object MarkdownStorage {

    const val LEGACY_HTML_MARKER = "<!--uroboros-html-->"

    fun normalize(stored: String): String {
        val content = stored.trim()
        return when {
            content.isEmpty() -> ""
            content.startsWith(LEGACY_HTML_MARKER) ->
                MarkdownUtils.htmlToMarkdown(content.removePrefix(LEGACY_HTML_MARKER))
            MarkdownUtils.looksLikeHtml(content) -> MarkdownUtils.htmlToMarkdown(content)
            else -> content
        }
    }

    fun save(state: MarkdownEditorState): String = state.text

    fun plainText(stored: String): String = MarkdownUtils.toPlainText(normalize(stored))
}
