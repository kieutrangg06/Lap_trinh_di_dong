package com.nikoniche.booki.personalData.local_database

import android.content.Context
import com.nikoniche.booki.data.FirebaseRepository

object DataGraph {
    val firebaseRepository by lazy {
        FirebaseRepository()
    }

    fun provide(context: Context) {
        // Không cần khởi tạo Room nữa
    }
}