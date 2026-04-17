package com.example.matestudy.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matestudy.ui.components.PasswordField
import com.example.matestudy.ui.theme.*
import com.example.matestudy.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onLoginClick: () -> Unit, viewModel: AuthViewModel) {
    val username by viewModel.registerUsername.collectAsState()
    val email by viewModel.registerEmail.collectAsState()
    val nienKhoa by viewModel.registerNienKhoa.collectAsState()
    val password by viewModel.registerPassword.collectAsState()
    val confirmPassword by viewModel.registerConfirmPassword.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFF8F9FF), Color.White)))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))
            Text("Tạo tài khoản", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black, color = PrimaryPink))
            Text("Gia nhập cộng đồng MateStudy ngay hôm nay", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

            Spacer(Modifier.height(32.dp))
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    OutlinedTextField(value = username, onValueChange = viewModel::onRegisterUsernameChange, label = { Text("Tên người dùng") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = email, onValueChange = viewModel::onRegisterEmailChange, label = { Text("Email sinh viên") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = nienKhoa, onValueChange = viewModel::onRegisterNienKhoaChange, label = { Text("Niên khóa (VD: 2024)") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    Spacer(Modifier.height(12.dp))
                    PasswordField(value = password, onValueChange = viewModel::onRegisterPasswordChange, label = "Mật khẩu")
                    Spacer(Modifier.height(12.dp))
                    PasswordField(value = confirmPassword, onValueChange = viewModel::onRegisterConfirmPasswordChange, label = "Xác nhận mật khẩu")
                }
            }

            error?.let { Text(it, color = ErrorRed, modifier = Modifier.padding(top = 12.dp)) }

            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { viewModel.register(onSuccess = onRegisterSuccess) },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("BẮT ĐẦU NGAY", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            TextButton(onClick = onLoginClick, modifier = Modifier.padding(16.dp)) {
                Text("Đã có tài khoản? Đăng nhập", color = PrimaryPink)
            }
        }
    }
}