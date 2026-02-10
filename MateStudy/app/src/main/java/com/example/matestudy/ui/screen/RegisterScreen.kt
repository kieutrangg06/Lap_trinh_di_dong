package com.example.matestudy.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.matestudy.ui.components.PasswordField
import com.example.matestudy.ui.viewmodel.AuthViewModel
import androidx.compose.foundation.text.KeyboardOptions

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: AuthViewModel
) {
    val username by viewModel.registerUsername.collectAsState()
    val email by viewModel.registerEmail.collectAsState()
    val nienKhoa by viewModel.registerNienKhoa.collectAsState()
    val password by viewModel.registerPassword.collectAsState()
    val confirmPassword by viewModel.registerConfirmPassword.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ĐĂNG KÝ MATESTUDY",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = viewModel::onRegisterUsernameChange,
            label = { Text("Tên đăng nhập *") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = viewModel::onRegisterEmailChange,
            label = { Text("Email * (ví dụ: you@edu.vn)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = nienKhoa,
            onValueChange = viewModel::onRegisterNienKhoaChange,
            label = { Text("Niên khóa * (VD: 2024)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        PasswordField(
            value = password,
            onValueChange = viewModel::onRegisterPasswordChange,
            label = "Mật khẩu * (ít nhất 8 ký tự)"
        )

        Spacer(Modifier.height(16.dp))

        PasswordField(
            value = confirmPassword,
            onValueChange = viewModel::onRegisterConfirmPasswordChange,
            label = "Xác nhận mật khẩu"
        )

        Spacer(Modifier.height(24.dp))

        if (error != null) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = { viewModel.register(onSuccess = onRegisterSuccess) },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(Modifier.size(24.dp))
            } else {
                Text("ĐĂNG KÝ NGAY")
            }
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onLoginClick) {
            Text("Đã có tài khoản? Đăng nhập ngay")
        }
    }
}