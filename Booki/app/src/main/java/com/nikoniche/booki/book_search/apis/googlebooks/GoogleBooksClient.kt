package com.nikoniche.booki.book_search.apis.googlebooks

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private object RetrofitClient {
    private const val BASE_URL = "https://www.googleapis.com/books/v1/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object GoogleBooksClient {
    val googleBooksService: GoogleBooksService by lazy {
        RetrofitClient.retrofit.create(GoogleBooksService::class.java)
    }
}