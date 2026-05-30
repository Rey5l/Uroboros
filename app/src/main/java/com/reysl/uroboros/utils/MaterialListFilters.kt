package com.reysl.uroboros.utils

import com.reysl.uroboros.data.Note

enum class MaterialSort {
    NEWEST,
    OLDEST,
    TITLE_ASC,
    TITLE_DESC,
}

enum class MaterialFilterType {
    ALL,
    FAVOURITES,
    TAG,
}

data class MaterialFilter(
    val type: MaterialFilterType = MaterialFilterType.ALL,
    val tag: String? = null,
)

fun List<Note>.filterMaterials(filter: MaterialFilter): List<Note> = when (filter.type) {
    MaterialFilterType.ALL -> this
    MaterialFilterType.FAVOURITES -> filter { it.isFavourite }
    MaterialFilterType.TAG -> filter { it.tag == filter.tag }
}

fun List<Note>.sortMaterials(sort: MaterialSort): List<Note> = when (sort) {
    MaterialSort.NEWEST -> sortedByDescending { it.time }
    MaterialSort.OLDEST -> sortedBy { it.time }
    MaterialSort.TITLE_ASC -> sortedBy { it.title.lowercase() }
    MaterialSort.TITLE_DESC -> sortedByDescending { it.title.lowercase() }
}

fun List<Note>.prepareMaterials(
    filter: MaterialFilter,
    sort: MaterialSort,
): List<Note> = filterMaterials(filter).sortMaterials(sort)
