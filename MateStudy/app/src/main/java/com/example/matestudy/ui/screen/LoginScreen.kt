package com.example.matestudy.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matestudy.ui.components.PasswordField
import com.example.matestudy.ui.theme.*
import com.example.matestudy.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onRegisterClick: () -> Unit, viewModel: AuthViewModel) {
    val emailOrUsername by viewModel.emailOrUsername.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.White, Color(0xFFFFF4F7))))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(modifier = Modifier.size(90.dp), shape = CircleShape, color = PrimaryLight) {
                Icon(Icons.Default.AutoAwesome, "Logo", modifier = Modifier.padding(20.dp), tint = PrimaryPink)
            }
            Spacer(Modifier.height(32.dp))
            Text("Chào mừng trở lại!", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, letterSpacing = (-1).sp))
            Text("Cùng nâng cao kết quả học tập nhé", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(48.dp))

            OutlinedTextField(
                value = emailOrUsername,
                onValueChange = { viewModel.onEmailOrUsernameChange(it) },
                label = { Text("Tên đăng nhập hoặc Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )
            Spacer(Modifier.height(16.dp))
            PasswordField(value = password, onValueChange = { viewModel.onPasswordChange(it) }, label = "Mật khẩu")

            error?.let { Text(it, color = ErrorRed, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 8.dp)) }

            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { viewModel.login(onSuccess = onLoginSuccess) },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = MaterialTheme.shapes.medium,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp), // Dùng Modifier để chỉnh kích thước
                        color = Color.White,
                        strokeWidth = 2.dp // (Tùy chọn) Chỉnh độ dày của vòng quay cho mảnh hơn
                    )
                } else {
                    Text("ĐĂNG NHẬP", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(24.dp))
            TextButton(onClick = onRegisterClick) {
                Text("Chưa có tài khoản? ", color = TextSecondary)
                Text("Tạo ngay", color = PrimaryPink, fontWeight = FontWeight.Bold)
            }
        }
    }
}