package com.nikoniche.booki.book_search.apis.googlebooks

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface GoogleBooksService {
    @GET("volumes")
    fun getBookByISBN(
        @Query("q") query: String,
        @Query("key") apiKey: String
    ): Call<GoogleBooksResponse>
}