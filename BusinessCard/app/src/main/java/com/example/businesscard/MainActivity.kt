package com.example.businesscard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.businesscard.ui.theme.BusinessCardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {//Hàm chạy khi app mở
        super.onCreate(savedInstanceState)//bắt buộc
        enableEdgeToEdge()//UI hiển thị full màn hình
        setContent {//UI bằng Jetpack Compose
            BusinessCardTheme {  // nếu chưa có theme → thay bằng MaterialTheme
                Surface(//lớp nền chính
                    modifier = Modifier.fillMaxSize(),//toàn màn
                    color = Color(0xFFFFF0F5)
                ) {
                    BusinessCard()//hàm vẽ giao diện danh thiếp
                }
            }
        }
    }
}

@Composable//bắt buộc
fun BusinessCard() {
    Column(//chiều dọc
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        //Căn giữa cả ngang & dọc
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Phần logo + tên + chức danh (giữa màn hình)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            //Chiếm phần lớn không gian, Đẩy phần liên hệ xuống dưới
            modifier = Modifier.weight(1f)
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.kieutrang),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        Color(0xFFFFC1CC).copy(alpha = 0.25f),
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(20.dp)
            )
            //khoảng trống
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Kiều Trang",
                fontSize = 50.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4A4A6A)
            )

            Text(
                text = "24IT280",
                fontSize = 22.sp,
                color = Color(0xFFFF8CAB),
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp,
                textAlign = TextAlign.Center
            )
        }

        // Phần thông tin liên hệ (dưới cùng)
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 64.dp)
        ) {
            ContactItem(
                icon = Icons.Default.Phone,
                text = "0354491100",
                contentDescription = "Số điện thoại",
                accentColor = Color(0xFFFF8CAB)
            )
            ContactItem(
                icon = Icons.Default.Share,
                text = "trangltk.24it@vku.udn.vn",
                contentDescription = "Tài khoản mạng xã hội",
                accentColor = Color(0xFFFF8CAB)
            )
            ContactItem(
                icon = Icons.Default.Email,
                text = "trangltk.24it@vku.udn.vn",
                contentDescription = "Email",
                accentColor = Color(0xFFFF8CAB)
            )
        }
    }
}

@Composable
fun ContactItem(
    icon: ImageVector,
    text: String,
    contentDescription: String,
    accentColor: Color = Color(0xFFFF8CAB)
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = accentColor,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(28.dp))
        Text(
            text = text,
            color = Color(0xFF4A4A6A),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Start
        )
    }
}

//xem trước UI không cần chạy app
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BusinessCardPreview() {
    BusinessCardTheme {
        Surface(color = Color(0xFFFFF0F5)) {
            BusinessCard()
        }
    }
}