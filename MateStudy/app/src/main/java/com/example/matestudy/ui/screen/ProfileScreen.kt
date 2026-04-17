package com.example.matestudy.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.matestudy.ui.theme.PrimaryPink
import com.example.matestudy.ui.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    onChangePasswordClick: () -> Unit,
    viewModel: AuthViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var editedUsername by remember { mutableStateOf("") }
    var editedEmail by remember { mutableStateOf("") }
    var editedNienKhoa by remember { mutableStateOf("") }
    var avatarUrlInput by remember { mutableStateOf("") }

    LaunchedEffect(currentUser) {
        editedUsername = currentUser.tenDangNhap
        editedEmail = currentUser.email
        editedNienKhoa = currentUser.nienKhoa?.toString() ?: ""
        avatarUrlInput = currentUser.anhDaiDien ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ────────────────────────────────────────────────
        // 1. AVATAR PREVIEW
        // ────────────────────────────────────────────────
        Box(contentAlignment = Alignment.BottomEnd) {
            Surface(
                modifier = Modifier.size(130.dp),
                shape = CircleShape,
                color = Color(0xFFF0F0F0),
                border = BorderStroke(4.dp, Color.White),
                shadowElevation = 8.dp
            ) {
                val displayImage = avatarUrlInput.ifBlank { currentUser.anhDaiDien }

                if (!displayImage.isNullOrBlank()) {
                    AsyncImage(
                        model = displayImage,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        placeholder = null
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.padding(30.dp),
                        tint = Color.LightGray
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // ────────────────────────────────────────────────
        // 2. FORM THÔNG TIN CÁ NHÂN
        // ────────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(20.dp)) {
                ProfileTextField(
                    label = "Tên hiển thị",
                    value = editedUsername,
                    onValueChange = { editedUsername = it }
                )

                ProfileTextField(
                    label = "Email liên hệ",
                    value = editedEmail,
                    keyboardType = KeyboardType.Email,
                    onValueChange = { editedEmail = it }
                )

                ProfileTextField(
                    label = "Niên khóa (ví dụ: 2024)",
                    value = editedNienKhoa,
                    keyboardType = KeyboardType.Number,
                    onValueChange = { editedNienKhoa = it }
                )

                ProfileTextField(
                    label = "Link ảnh đại diện (URL)",
                    value = avatarUrlInput,
                    onValueChange = { avatarUrlInput = it }
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Chức vụ", fontWeight = FontWeight.Bold)
                    Text(
                        text = if (currentUser.vaiTro == "sinh_vien") "Sinh viên" else "Quản trị viên",
                        color = PrimaryPink,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ────────────────────────────────────────────────
        // 3. THÔNG BÁO LỖI & CÁC NÚT ĐIỀU KHIỂN
        // ────────────────────────────────────────────────
        if (!error.isNullOrBlank()) {
            Text(
                text = error!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(Modifier.height(8.dp))
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = {
                    editedUsername = currentUser.tenDangNhap
                    editedEmail = currentUser.email
                    editedNienKhoa = currentUser.nienKhoa?.toString() ?: ""
                    avatarUrlInput = currentUser.anhDaiDien ?: ""
                },
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("HỦY")
            }

            Button(
                onClick = {
                    val finalAvatarUrl = avatarUrlInput.ifBlank { null }
                    viewModel.updateProfile(
                        newUsername = editedUsername,
                        newEmail = editedEmail,
                        newNienKhoa = editedNienKhoa.toIntOrNull(),
                        newAvatarUrl = finalAvatarUrl
                    )
                },
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.medium,
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("LƯU THAY ĐỔI")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onChangePasswordClick) {
            Icon(Icons.Default.VpnKey, null, Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Yêu cầu đổi mật khẩu")
        }
    }
}

// ────────────────────────────────────────────────
// 4. COMPONENTS HỖ TRỢ
// ────────────────────────────────────────────────

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyLarge,
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}