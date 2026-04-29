plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.compiler) apply false
    id("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false   // ← THÊM DÒNG NÀY
}