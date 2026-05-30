package com.reysl.uroboros.utils

import kotlin.random.Random

sealed class KnowledgeCheckSegment {
    data class Plain(val text: String) : KnowledgeCheckSegment()
    data class HiddenWord(
        val id: Int,
        val text: String,
    ) : KnowledgeCheckSegment()
}

object KnowledgeCheckGenerator {

    private val stopWords = setOf(
        "и", "в", "во", "на", "но", "а", "то", "что", "как", "это", "для", "при",
        "из", "по", "не", "ни", "же", "ли", "бы", "у", "о", "от", "до", "за",
        "the", "and", "or", "a", "an", "in", "on", "at", "to", "of", "is", "it",
        "that", "for", "with", "as", "be", "are", "was", "were"
    )

    fun generate(
        plainText: String,
        seed: Long,
        hideRatio: Float = 0.35f,
        minWordLength: Int = 4
    ): List<KnowledgeCheckSegment> {
        if (plainText.isBlank()) return listOf(KnowledgeCheckSegment.Plain(plainText))

        val tokenRegex = Regex("""\S+|\s+""")
        val tokens = tokenRegex.findAll(plainText).map { it.value }.toList()

        val hideableIndices = tokens.mapIndexedNotNull { index, token ->
            if (token.matches(Regex("\\s+"))) return@mapIndexedNotNull null
            val normalized = token.filter { it.isLetter() }.lowercase()
            if (normalized.length < minWordLength) return@mapIndexedNotNull null
            if (normalized in stopWords) return@mapIndexedNotNull null
            index
        }

        if (hideableIndices.isEmpty()) {
            return tokens.map { KnowledgeCheckSegment.Plain(it) }
        }

        val hideCount = (hideableIndices.size * hideRatio)
            .toInt()
            .coerceIn(1, hideableIndices.size)

        val indicesToHide = hideableIndices
            .shuffled(Random(seed))
            .take(hideCount)
            .toSet()

        val result = mutableListOf<KnowledgeCheckSegment>()
        var wordId = 0

        tokens.forEachIndexed { index, token ->
            if (index in indicesToHide) {
                result += KnowledgeCheckSegment.HiddenWord(wordId++, token)
            } else {
                result += KnowledgeCheckSegment.Plain(token)
            }
        }

        return result
    }
}
