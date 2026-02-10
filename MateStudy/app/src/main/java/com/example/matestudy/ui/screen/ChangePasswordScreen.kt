package com.example.matestudy.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import com.example.matestudy.ui.components.PasswordField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import com.example.matestudy.ui.viewmodel.AuthViewModel
import androidx.compose.runtime.collectAsState
@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit,
    viewModel: AuthViewModel
) {
    val oldPass by viewModel.oldPassword.collectAsState()
    val newPass by viewModel.newPassword.collectAsState()
    val confirmNewPass by viewModel.confirmNewPassword.collectAsState()

    Column(modifier = Modifier.padding(24.dp)) {
        Text("ĐỔI MẬT KHẨU", style = typography.headlineSmall)

        Spacer(Modifier.height(24.dp))

        PasswordField(value = oldPass, onValueChange = viewModel::onOldPasswordChange, label = "Mật khẩu hiện tại")
        Spacer(Modifier.height(16.dp))

        PasswordField(value = newPass, onValueChange = viewModel::onNewPasswordChange, label = "Mật khẩu mới")
        Text("Ít nhất 8 ký tự", style = typography.bodySmall)

        Spacer(Modifier.height(16.dp))

        PasswordField(value = confirmNewPass, onValueChange = viewModel::onConfirmNewPasswordChange, label = "Xác nhận mật khẩu mới")

        Spacer(Modifier.height(32.dp))

        Button(onClick = {
            viewModel.changePassword(onSuccess = onBack)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("CẬP NHẬT MẬT KHẨU")
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("HỦY")
        }
    }
}