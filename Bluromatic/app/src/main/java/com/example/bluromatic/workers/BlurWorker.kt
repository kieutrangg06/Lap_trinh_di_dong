package com.example.bluromatic.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.bluromatic.DELAY_TIME_MILLIS
import com.example.bluromatic.KEY_BLUR_LEVEL
import com.example.bluromatic.KEY_IMAGE_URI
import com.example.bluromatic.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val TAG = "BlurWorker"

class BlurWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        makeStatusNotification(
            applicationContext.getString(R.string.blurring_image),
            applicationContext
        )

        return withContext(Dispatchers.IO) {
            delay(DELAY_TIME_MILLIS)

            try {
                val resourceUri = inputData.getString(KEY_IMAGE_URI)
                    ?: return@withContext Result.failure()

                val blurLevel = inputData.getInt(KEY_BLUR_LEVEL, 1)

                val resolver = applicationContext.contentResolver
                val inputStream = resolver.openInputStream(Uri.parse(resourceUri))

                val picture = BitmapFactory.decodeStream(inputStream)
                    ?: throw Exception("Failed to decode bitmap")

                // === SỬA Ở ĐÂY: Truyền applicationContext vào blurBitmap ===
                val output = blurBitmap(applicationContext, picture, blurLevel)

                val outputUri = writeBitmapToFile(applicationContext, output)

                val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())

                Result.success(outputData)

            } catch (throwable: Throwable) {
                Log.e(TAG, applicationContext.getString(R.string.error_applying_blur), throwable)
                Result.failure()
            }
        }
    }
}