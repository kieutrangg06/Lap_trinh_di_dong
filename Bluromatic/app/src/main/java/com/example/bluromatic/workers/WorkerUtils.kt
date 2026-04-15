package com.example.bluromatic.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.renderscript.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.example.bluromatic.OUTPUT_PATH
import com.example.bluromatic.R
import java.io.File
import java.io.FileOutputStream
import java.util.*

lateinit var renderEffect: RenderEffect
private const val TAG = "WorkerUtils"
private const val CHANNEL_ID = "Blur_O_Matic_Notifications"

// ==================== NOTIFICATION ====================

fun makeStatusNotification(message: String, context: Context) {
    createNotificationChannel(context)

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Blur-O-Matic")
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(UUID.randomUUID().hashCode(), notification)
}

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Blur-O-Matic Notifications"
        val descriptionText = "Notifications for image blurring process"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }
}

// ==================== BLUR IMAGE ====================

fun blurBitmap(context: Context, inputBitmap: Bitmap, blurLevel: Int): Bitmap {
    val radius = when (blurLevel) {
        1 -> 5f
        2 -> 15f
        3 -> 25f // Max radius
        else -> 10f
    }

    var output = inputBitmap

    // Nếu level là 2 hoặc 3, ta chạy làm mờ 2-3 lần để tăng độ đậm đặc
    val iterations = if (blurLevel > 1) blurLevel else 1

    try {
        repeat(iterations) {
            val tempBitmap = Bitmap.createBitmap(output.width, output.height, Bitmap.Config.ARGB_8888)
            output = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                blurWithRenderEffect(output, tempBitmap, radius)
            } else {
                blurWithRenderScript(context, output, radius)
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "Blur failed", e)
    }
    return output
}

// ===== API 31+ =====
@RequiresApi(Build.VERSION_CODES.S)
private fun blurWithRenderEffect(
    inputBitmap: Bitmap,
    outputBitmap: Bitmap,
    radius: Float
): Bitmap {

    val canvas = Canvas(outputBitmap)

    val paint = Paint().apply {
        renderEffect = RenderEffect.createBlurEffect(
            radius,
            radius,
            Shader.TileMode.CLAMP
        )
    }

    canvas.drawBitmap(inputBitmap, 0f, 0f, paint)

    Log.i(TAG, "Blur completed with RenderEffect")
    return outputBitmap
}

// ===== API < 31 =====
private fun blurWithRenderScript(
    context: Context,
    inputBitmap: Bitmap,
    radius: Float
): Bitmap {

    val outputBitmap = Bitmap.createBitmap(
        inputBitmap.width,
        inputBitmap.height,
        Bitmap.Config.ARGB_8888
    )

    var rs: RenderScript? = null

    return try {
        rs = RenderScript.create(context)

        val input = Allocation.createFromBitmap(rs, inputBitmap)
        val output = Allocation.createFromBitmap(rs, outputBitmap)

        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        script.setRadius(radius)
        script.setInput(input)
        script.forEach(output)

        output.copyTo(outputBitmap)

        Log.i(TAG, "Blur completed with RenderScript")
        outputBitmap

    } catch (e: Exception) {
        Log.e(TAG, "RenderScript blur failed", e)
        inputBitmap
    } finally {
        rs?.destroy()
    }
}

// ==================== SAVE FILE ====================

fun writeBitmapToFile(context: Context, bitmap: Bitmap): Uri {

    val name = "blur-filter-output-${System.currentTimeMillis()}.png"

    val outputDir = File(context.filesDir, OUTPUT_PATH)

    if (!outputDir.exists()) {
        outputDir.mkdirs()
    }

    val outputFile = File(outputDir, name)

    FileOutputStream(outputFile).use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
    }

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        outputFile
    )
}