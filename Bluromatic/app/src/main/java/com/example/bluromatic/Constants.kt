package com.example.bluromatic

import android.net.Uri
import android.content.Context

const val KEY_IMAGE_URI = "IMAGE_URI"
const val KEY_BLUR_LEVEL = "BLUR_LEVEL"
const val OUTPUT_PATH = "blur_filter_outputs"
const val DELAY_TIME_MILLIS = 3000L   // chỉ dùng để demo, chậm lại để dễ quan sát

// Helper để lấy URI hình ảnh cupcake
fun Context.getImageUri(): Uri {
    return Uri.parse(
        "android.resource://${packageName}/${R.drawable.android_cupcake}"
    )
}