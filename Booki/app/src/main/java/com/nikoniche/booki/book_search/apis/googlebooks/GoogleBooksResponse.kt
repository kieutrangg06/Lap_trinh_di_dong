package com.nikoniche.booki.book_search.apis.googlebooks

data class GoogleBooksResponse(
    val kind: String,
    val totalItems: Int,
    val items: List<BookItem>
)

data class BookItem(
    val kind: String,
    val id: String,
    val etag: String,
    val selfLink: String,
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String,
    val authors: List<String> = emptyList(),
    val publisher: String? = null,
    val publishedDate: String? = null,
    val description: String? = null,
    val industryIdentifiers: List<IndustryIdentifier> = emptyList(),
    val pageCount: Int? = null,
    val categories: List<String> = emptyList(),
    val averageRating: Float? = null,
    val ratingsCount: Int? = null,
    val imageLinks: ImageLinks? = null,
    val language: String? = null
)

data class IndustryIdentifier(
    val type: String,
    val identifier: String
)

data class ImageLinks(
    val smallThumbnail: String? = null,
    val thumbnail: String? = null
)