package com.example.managestudent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
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
import com.example.managestudent.ui.theme.MainRed
import com.example.managestudent.ui.theme.PrimaryYellow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var isAdmin by remember { mutableStateOf(false) }
    val currentUser = auth.currentUser

    // Lấy thông tin role của người dùng
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    isAdmin = document.getString("role") == "admin"
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Trang Chủ",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MainRed),
                navigationIcon = {
                    IconButton(onClick = {
                        auth.signOut()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Đăng xuất",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryYellow)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Tiêu đề chính
            Text(
                text = "QUẢN LÝ SINH VIÊN",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MainRed,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Lời chào theo vai trò
            Text(
                text = if (isAdmin)
                    "Xin chào Admin!\nBạn có quyền quản lý toàn bộ sinh viên"
                else
                    "Xin chào Sinh Viên!\nChào mừng bạn đến với hệ thống",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Nút quản lý chỉ hiển thị với Admin
            if (isAdmin) {
                Button(
                    onClick = { navController.navigate("student_list") },
                    modifier = Modifier.fillMaxWidth(0.75f).height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MainRed),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "QUẢN LÝ DANH SÁCH SINH VIÊN",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Banner hình ảnh
            Image(
                painter = painterResource(id = R.drawable.baner),
                contentDescription = "Banner",
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}