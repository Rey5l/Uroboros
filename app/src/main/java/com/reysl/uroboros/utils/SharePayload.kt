package com.reysl.uroboros.utils

data class SharePayload(
    val suggestedTitle: String,
    val suggestedDescription: String,
    val rawText: String,
    val suggestedTag: String,
    val sourceUrl: String? = null,
) {
    fun withPageContent(page: PageContent): SharePayload {
        val articleText = page.text.trim()
        val body = buildString {
            append(articleText)
            append("\n\n")
            append("Источник: ")
            append(page.url)
        }.trim()

        val description = when {
            page.metaDescription.isNotBlank() -> page.metaDescription
            suggestedDescription.isNotBlank() &&
                !ShareIntentParser.isUrl(suggestedDescription) &&
                suggestedDescription != page.title -> suggestedDescription
            else -> buildSourceDescription(page.url)
        }

        return copy(
            suggestedTitle = page.title.take(25).ifBlank { suggestedTitle },
            suggestedDescription = description.take(500),
            rawText = body,
            sourceUrl = page.url
        )
    }

    private fun buildSourceDescription(url: String): String {
        val host = url.removePrefix("https://")
            .removePrefix("http://")
            .substringBefore('/')
            .substringBefore('?')
        return "Импортировано с $host"
    }

    fun toStoredContent(title: String, description: String): String {
        val titleTrim = title.trim()
        val descriptionTrim = description.trim()
        val body = rawText.trim()

        return buildString {
            append("# ").append(titleTrim).append("\n\n")
            if (descriptionTrim.isNotBlank() &&
                descriptionTrim != titleTrim &&
                !ShareIntentParser.isUrl(descriptionTrim) &&
                !body.contains(descriptionTrim)
            ) {
                append("> ").append(descriptionTrim).append("\n\n")
            }
            if (body.isNotBlank()) {
                append(body)
            }
        }.trim()
    }
}
