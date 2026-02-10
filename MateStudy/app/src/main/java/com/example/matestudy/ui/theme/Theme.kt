package com.example.matestudy.ui.theme

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Màu chính đồng bộ với CSS (hồng #E91E63)
private val Primary       = Color(0xFFE91E63)   // hồng đậm chính (logo, button, link, focus)
private val PrimaryDark   = Color(0xFFD81B60)   // hover/active của button
private val PrimaryDarker = Color(0xFFC2185B)   // variant đậm hơn cho một số hover/link
private val Accent        = Color(0xFFFF4081)   // hồng sáng accent (nếu cần dùng sau)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF5F8),       // rất nhạt hồng, khớp background gradient CSS
    onPrimaryContainer = Primary,
    secondary = Accent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE4EC),     // hồng nhạt hơn cho card/container
    onSecondaryContainer = Primary,
    tertiary = Color(0xFFAB47BC),               // tím phụ (giữ hoặc bỏ nếu không dùng)
    background = Color(0xFFFFF5F8),             // nền body
    onBackground = Color(0xFF1F272B),           // text chính
    surface = Color.White,
    onSurface = Color(0xFF1F272B),
    surfaceVariant = Color(0xFFFAFAFA),         // input background
    onSurfaceVariant = Color(0xFF757575),       // label, tagline, secondary text
    error = Color(0xFFD32F2F),
    outline = Color(0xFFE0E0E0)                 // border input
)

// Dark theme (nếu bạn muốn hỗ trợ sau này, hiện tại có thể để đơn giản)
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF880E4F),
    onPrimaryContainer = Color.White,
    secondary = Accent,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    // ... bạn có thể tinh chỉnh thêm nếu bật dark mode
)

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MateStudyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Tắt dynamic color để buộc dùng màu custom của chúng ta
    // Nếu muốn thử dynamic thì đổi lại true, nhưng sẽ mất màu hồng
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) androidx.compose.material3.dynamicDarkColorScheme(context)
            else androidx.compose.material3.dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,  // giữ nguyên hoặc import font Poppins nếu bạn đã thêm
        content = content
    )
}