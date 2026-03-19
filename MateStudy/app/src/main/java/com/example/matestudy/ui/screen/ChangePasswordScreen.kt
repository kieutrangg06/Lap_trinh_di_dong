package com.example.matestudy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matestudy.ui.components.PasswordField
import com.example.matestudy.ui.theme.*
import com.example.matestudy.ui.viewmodel.AuthViewModel

@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit,
    viewModel: AuthViewModel
) {
    val oldPass by viewModel.oldPassword.collectAsState()
    val newPass by viewModel.newPassword.collectAsState()
    val confirmNewPass by viewModel.confirmNewPassword.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color.White, BackgroundGradientEnd)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))

            Surface(
                modifier = Modifier.size(80.dp),
                shape = androidx.compose.foundation.shape.CircleShape,
                color = PrimaryLight
            ) {
                Icon(
                    Icons.Default.LockReset,
                    contentDescription = null,
                    modifier = Modifier.padding(20.dp),
                    tint = PrimaryPink
                )
            }

            Spacer(Modifier.height(24.dp))
            Text(
                "Đổi mật khẩu",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black)
            )
            Text(
                "Hãy sử dụng mật khẩu mạnh để bảo vệ tài khoản",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    PasswordField(value = oldPass, onValueChange = viewModel::onOldPasswordChange, label = "Mật khẩu hiện tại")
                    Spacer(Modifier.height(16.dp))

                    PasswordField(value = newPass, onValueChange = viewModel::onNewPasswordChange, label = "Mật khẩu mới")
                    Text("Ít nhất 8 ký tự", style = MaterialTheme.typography.labelSmall, color = TextSecondary, modifier = Modifier.padding(start = 4.dp, top = 4.dp))

                    Spacer(Modifier.height(16.dp))
                    PasswordField(value = confirmNewPass, onValueChange = viewModel::onConfirmNewPasswordChange, label = "Xác nhận mật khẩu mới")
                }
            }

            error?.let {
                Text(it, color = ErrorRed, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 16.dp))
            }

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = { viewModel.changePassword(onSuccess = onBack) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
                else Text("CẬP NHẬT MẬT KHẨU", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("HỦY BỎ", color = TextSecondary)
            }
        }
    }
}