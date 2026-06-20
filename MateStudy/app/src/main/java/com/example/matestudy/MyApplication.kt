package com.example.matestudy

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate() //yêu cầu Android thực hiện các thiết lập Application
        try {
            FirebaseApp.initializeApp(this) //kết nối với Firebase
            Log.d("FirebaseInit", "FirebaseApp initialized successfully!")
        } catch (e: Exception) {
            Log.e("FirebaseInit", "Firebase init failed: ${e.message}", e)
        }
    }
}