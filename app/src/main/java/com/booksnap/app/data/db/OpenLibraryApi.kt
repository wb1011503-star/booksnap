package com.booksnap.app.data.db

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

data class OpenLibrarySearchResponse(
    @SerializedName("docs") val docs: List<OpenLibraryBook> = emptyList(),
    @SerializedName("numFound") val numFound: Int = 0
)

data class OpenLibraryBook(
    @SerializedName("key") val key: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("author_name") val authorName: List<String>? = null,
    @SerializedName("cover_i") val coverId: Int? = null,
    @SerializedName("first_publish_year") val firstPublishYear: Int? = null
) {
    fun coverUrl(): String? = coverId?.let {
        "https://covers.openlibrary.org/b/id/$it-M.jpg"
    }
    fun authorDisplay(): String = authorName?.firstOrNull() ?: "저자 미상"
}

interface OpenLibraryApi {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20,
        @Query("fields") fields: String = "key,title,author_name,cover_i,first_publish_year"
    ): OpenLibrarySearchResponse
}
