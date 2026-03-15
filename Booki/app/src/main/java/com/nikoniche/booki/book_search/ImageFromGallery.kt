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

fun getImageUri(context: Context, bitmap: Bitmap): Uri {
    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
    return Uri.parse(path)
}

fun isValidUri(context: Context, uri: Uri): Boolean {
    return try {
        context.contentResolver.openInputStream(uri)?.close()
        true
    } catch (e: Exception) {
        false
    }
}

fun triggerPersistentUriPermission(uri: Uri, context: Context) {
    val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    val resolver = context.contentResolver
    resolver.takePersistableUriPermission(uri, flags)
}

@Composable
fun rememberImagePickerLauncher(
    onImagePicked: (Uri) -> Unit,
    onCameraImageTaken: (Bitmap) -> Unit
): Pair<() -> Unit, () -> Unit> {
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onImagePicked(it)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        bitmap?.let {
            onCameraImageTaken(it)
        }
    }

    return Pair({ galleryLauncher.launch("image/*") }, { cameraLauncher.launch(null) })
}
