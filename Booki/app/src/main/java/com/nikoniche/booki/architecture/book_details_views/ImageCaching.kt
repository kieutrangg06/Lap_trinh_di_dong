package com.nikoniche.booki.architecture.book_details_views

import android.content.Context
import coil.ImageLoader
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File

const val CACHE_SIZE = 30L // MB
fun createImageLoader(context: Context): ImageLoader {
    // Define the disk cache directory
    val diskCacheDir = File(context.cacheDir, "book_covers_cache")

    // Create a Cache instance
    val cache = Cache(diskCacheDir, CACHE_SIZE * 1024 * 1024) // 10 MB cache

    return ImageLoader.Builder(context)
        .okHttpClient {
            OkHttpClient.Builder()
                .cache(cache)
                .build()
        }
        .build()
}