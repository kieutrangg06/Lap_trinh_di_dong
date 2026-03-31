package com.nikoniche.booki.book_search

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import java.io.ByteArrayOutputStream

/**
 * Chuyển đổi Bitmap từ Camera thành Uri
 */
fun getImageUri(context: Context, bitmap: Bitmap): Uri {
    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(
        context.contentResolver,
        bitmap,
        "Book_${System.currentTimeMillis()}",
        null
    )
    return Uri.parse(path)
}

/**
 * Kiểm tra Uri có hợp lệ và có thể mở được không
 */
fun isValidUri(context: Context, uri: Uri): Boolean {
    return try {
        context.contentResolver.openInputStream(uri)?.use { it.available() }
        true
    } catch (e: Exception) {
        false
    }
}

/**
 * Xin quyền truy cập vĩnh viễn (Persistable)
 * Đặc biệt quan trọng khi chọn file từ Drive hoặc Downloads
 */
fun triggerPersistentUriPermission(uri: Uri, context: Context) {
    try {
        val contentResolver = context.contentResolver
        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION

        // Kiểm tra xem URI có hỗ trợ quyền persistable không (thường là content://)
        contentResolver.takePersistableUriPermission(uri, takeFlags)
        println("Quyền truy cập lâu dài đã được cấp cho: $uri")
    } catch (e: SecurityException) {
        // Một số nhà cung cấp dữ liệu (như Drive hoặc một số Folder hệ thống)
        // có thể không cho phép 'takePersistableUriPermission'.
        // Chúng ta log lại để biết nhưng không để App bị crash.
        e.printStackTrace()
        println("Không thể duy trì quyền lâu dài cho URI này. Lỗi: ${e.message}")
    }
}

@Composable
fun rememberImagePickerLauncher(
    onImagePicked: (Uri) -> Unit,
    onCameraImageTaken: (Bitmap) -> Unit
): Pair<() -> Unit, () -> Unit> {

    // Sử dụng GetContent để mở trình chọn file hệ thống (bao gồm Drive, Downloads)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onImagePicked(it)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            onCameraImageTaken(it)
        }
    }

    return Pair(
        { galleryLauncher.launch("image/*") },
        { cameraLauncher.launch(null) }
    )
}