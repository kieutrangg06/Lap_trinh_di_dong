package com.nikoniche.booki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.nikoniche.booki.personalData.local_database.DataGraph // Sử dụng DataGraph thay cho Graph
import com.nikoniche.booki.architecture.MainView
import com.nikoniche.booki.ui.theme.BookiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase không cần context để khởi tạo như Room (databaseBuilder),
        // nhưng nếu bạn giữ DataGraph để quản lý các Repository thì gọi hàm rỗng
        // hoặc loại bỏ nếu DataGraph chỉ dùng lazy delegate.
        DataGraph.provide(this.applicationContext)

        // Ép buộc chế độ sáng (Light Mode)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setContent {
            BookiTheme(darkTheme = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    BookiTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            // Đảm bảo R.color.primaryContrastColor tồn tại trong res/values/colors.xml
            color = colorResource(id = R.color.primaryContrastColor)
        ) {
            MainView()
        }
    }
}