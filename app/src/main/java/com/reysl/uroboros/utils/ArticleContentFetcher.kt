package com.reysl.uroboros.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object ArticleContentFetcher {

    private const val MAX_CONTENT_LENGTH = 50_000
    private const val TIMEOUT_MS = 15_000

    private val userAgent =
        "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"

    private val contentSelectors = listOf(
        "article",
        "main",
        "[role=main]",
        ".post-content",
        ".article-content",
        ".entry-content",
        ".article-body",
        ".story-body",
        "#content",
        ".content",
    )

    suspend fun fetch(url: String): PageContent = withContext(Dispatchers.IO) {
        val doc = Jsoup.connect(url)
            .userAgent(userAgent)
            .timeout(TIMEOUT_MS)
            .followRedirects(true)
            .ignoreHttpErrors(false)
            .get()

        doc.select("script, style, noscript, nav, footer, header, aside, iframe, svg, form").remove()

        val title = doc.select("meta[property=og:title]").attr("content").trim()
            .ifBlank { doc.select("meta[name=twitter:title]").attr("content").trim() }
            .ifBlank { doc.title().trim() }

        val metaDescription = doc.select("meta[property=og:description]").attr("content").trim()
            .ifBlank { doc.select("meta[name=description]").attr("content").trim() }
            .ifBlank { doc.select("meta[name=twitter:description]").attr("content").trim() }

        val text = extractMainText(doc)
            .replace(Regex("\\n{3,}"), "\n\n")
            .trim()
            .take(MAX_CONTENT_LENGTH)

        if (text.length < 50) {
            error("Недостаточно текста на странице")
        }

        PageContent(
            title = title,
            text = text,
            url = url,
            metaDescription = metaDescription.take(500)
        )
    }

    private fun extractMainText(doc: Document): String {
        for (selector in contentSelectors) {
            val element = doc.select(selector).firstOrNull() ?: continue
            val text = element.text().trim()
            if (text.length >= 200) return text
        }
        return doc.body().text().trim()
    }
}
