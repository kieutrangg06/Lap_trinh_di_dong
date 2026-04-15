package com.example.bluromatic.data

import android.content.Context
import android.net.Uri
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.bluromatic.KEY_BLUR_LEVEL
import com.example.bluromatic.KEY_IMAGE_URI
import com.example.bluromatic.getImageUri
import com.example.bluromatic.workers.BlurWorker
import com.example.bluromatic.workers.CleanupWorker
import com.example.bluromatic.workers.SaveImageToFileWorker

class WorkManagerBluromaticRepository(context: Context) : BluromaticRepository {

    private val workManager = WorkManager.getInstance(context)
    private val imageUri: Uri = context.getImageUri()

    override fun applyBlur(blurLevel: Int) {
        // 1. Cleanup chạy đầu tiên (không cần input)
        val cleanupRequest = OneTimeWorkRequestBuilder<CleanupWorker>().build()

        // 2. BlurWorker CHỈ ĐỊNH rõ input từ Repository
        val blurRequest = OneTimeWorkRequestBuilder<BlurWorker>()
            .setInputData(createInputDataForWorkRequest(blurLevel, imageUri.toString())) // Gửi URI và Level ở đây
            .build()

        // 3. SaveWorker nhận input tự động từ kết quả của BlurWorker
        val saveRequest = OneTimeWorkRequestBuilder<SaveImageToFileWorker>()
            .addTag("save_image")
            .build()

        // Sửa lại chuỗi:
        workManager.beginWith(cleanupRequest)
            .then(blurRequest)
            .then(saveRequest)
            .enqueue()
    }

    private fun createInputDataForWorkRequest(blurLevel: Int, imageUri: String): Data =
        Data.Builder()
            .putString(KEY_IMAGE_URI, imageUri)
            .putInt(KEY_BLUR_LEVEL, blurLevel)
            .build()
}