// Top-level build file
plugins {
    // Android Gradle Plugin hỗ trợ API 35
    id("com.android.application") version "8.7.0" apply false
    id("com.android.library") version "8.7.0" apply false

    // Nâng lên Kotlin 2.0 để dùng plugin Compose mới nhất
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false

    // Plugin Compose Compiler mới (đi kèm Kotlin 2.0)
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false

    id("com.google.gms.google-services") version "4.4.1" apply false
}