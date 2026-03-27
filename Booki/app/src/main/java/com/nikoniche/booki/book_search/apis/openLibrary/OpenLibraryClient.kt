package com.nikoniche.booki.book_search.apis.openLibrary

import OpenLibraryService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private object RetrofitClient {
    private const val BASE_URL = "https://openlibrary.org/api/volumes/brief/"

    // Create OkHttpClient with the interceptor
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HeaderInterceptor())
        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


}

object OpenLibraryClient {
    val openLibraryService: OpenLibraryService by lazy {
        RetrofitClient.retrofit.create(OpenLibraryService::class.java)
    }
}