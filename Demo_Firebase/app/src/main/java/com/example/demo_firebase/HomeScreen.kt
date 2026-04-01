package com.example.demo_firebase

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

// Giao diện màu sắc dựa trên ảnh mẫu
val PrimaryYellow = Color(0xFFFDCB58) // Màu nền vàng nghệ
val StatusPurple = Color(0xFF6200EE) // Màu tím thanh trạng thái
val MainRed = Color(0xFFB71C1C) // Màu đỏ của nút và tiêu đề

@Composable
fun HomeScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()

    // Bố cục chính là một Column để xếp các phần tử từ trên xuống dưới
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryYellow), // Màu nền vàng
        horizontalAlignment = Alignment.CenterHorizontally // Căn giữa các phần tử theo chiều ngang
    ) {
        // --- 1. Tiêu đề "PIZZERIA" ---
        Spacer(modifier = Modifier.height(60.dp)) // Tạo khoảng trống phía trên
        Text(
            text = "PIZZERIA",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MainRed,
            textAlign = TextAlign.Center
        )

        // --- 2. Dòng chữ chào mừng ---
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Delivering\nDeliciousness right\nto your door!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp, // Khoảng cách giữa các dòng
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        // --- 3. Pager Indicator (Các dấu chấm) ---
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            // Đây là các dấu chấm tĩnh để minh họa. Trong thực tế, bạn sẽ dùng HorizontalPager.
            // Các dấu chấm màu kem nhạt
            repeat(1) { PagerIndicatorDot(color = Color(0xFFFFF3E0)) }
            // Dấu chấm màu đen
            repeat(1) { PagerIndicatorDot(color = Color.Black) }
            // Các dấu chấm màu kem nhạt
            repeat(1) { PagerIndicatorDot(color = Color(0xFFFFF3E0)) }
        }

        // --- 4. Nút chính "START ORDER" ---
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = { /* Xử lý khi nhấn START ORDER */ },
            modifier = Modifier
                .fillMaxWidth(0.6f) // Độ rộng bằng 60% màn hình
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MainRed),
            shape = RoundedCornerShape(16.dp), // Bo cong nhiều
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp) // Thêm đổ bóng
        ) {
            Text(
                text = "START ORDER",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        // --- 5. Nút đăng xuất "SignOut" ---
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                // Logic đăng xuất thực tế
                auth.signOut()
                // Quay lại màn hình đăng nhập và xóa stack
                navController.navigate("login") {
                    popUpTo(0) // Xóa tất cả các màn hình trước đó
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.4f) // Độ rộng bằng 40% màn hình
                .height(45.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MainRed),
            shape = RoundedCornerShape(12.dp), // Bo cong
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp) // Thêm đổ bóng
        ) {
            Text(
                text = "SignOut",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        // --- 6. Hình ảnh Banner ---
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = R.drawable.baner), // File ảnh trong drawable
            contentDescription = "Delivery Driver",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Hình ảnh chiếm phần còn lại của màn hình
            contentScale = ContentScale.FillWidth // Lấp đầy độ rộng
        )
    }
}

// Hàm hỗ trợ để tạo dấu chấm Pager
@Composable
fun PagerIndicatorDot(color: Color) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .background(color, shape = CircleShape)
    )
}