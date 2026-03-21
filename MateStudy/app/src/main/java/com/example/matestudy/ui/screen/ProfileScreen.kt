package com.example.matestudy.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
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
fun ProfileScreen(onChangePasswordClick: () -> Unit, viewModel: AuthViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var editedUsername by remember { mutableStateOf(currentUser.tenDangNhap) }
    var editedEmail by remember { mutableStateOf(currentUser.email) }
    var editedNienKhoa by remember { mutableStateOf(currentUser.nienKhoa?.toString() ?: "") }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentUser) {
        editedUsername = currentUser.tenDangNhap
        editedEmail = currentUser.email
        editedNienKhoa = currentUser.nienKhoa?.toString() ?: ""
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { selectedImageUri = it.toString() }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Surface(modifier = Modifier.size(130.dp), shape = CircleShape, color = Color(0xFFF0F0F0), border = BorderStroke(4.dp, Color.White), shadowElevation = 8.dp) {
                val displayImage = selectedImageUri ?: currentUser.anhDaiDien
                if (!displayImage.isNullOrBlank()) {
                    AsyncImage(model = displayImage, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                } else {
                    Icon(Icons.Default.Person, null, modifier = Modifier.padding(30.dp), tint = Color.LightGray)
                }
            }
            SmallFloatingActionButton(onClick = { imagePickerLauncher.launch("image/*") }, containerColor = PrimaryPink, contentColor = Color.White, shape = CircleShape) {
                Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(Modifier.height(32.dp))

        Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
            Column(Modifier.padding(20.dp)) {
                ProfileTextField("Tên hiển thị", editedUsername) { editedUsername = it }
                ProfileTextField("Email liên hệ", editedEmail, KeyboardType.Email) { editedEmail = it }
                ProfileTextField("Niên khóa", editedNienKhoa, KeyboardType.Number) { editedNienKhoa = it }

                Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Chức vụ", fontWeight = FontWeight.Bold)
                    Text(if (currentUser.vaiTro == "sinh_vien") "Sinh viên" else "Quản trị viên", color = PrimaryPink)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = { /* Reset */ }, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.medium) { Text("HỦY") }
            Button(onClick = { viewModel.updateProfile(editedUsername, editedEmail, editedNienKhoa.toIntOrNull(), selectedImageUri) }, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.medium, enabled = !isLoading) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp), // Sửa ở đây
                        color = Color.White,             // Đảm bảo màu trắng để nổi bật trên nút
                        strokeWidth = 2.dp               // Giảm độ dày để trông tinh tế hơn
                    )
                } else {
                    Text("LƯU")
                }
            }
        }

        TextButton(onClick = onChangePasswordClick, modifier = Modifier.padding(top = 16.dp)) {
            Icon(Icons.Default.VpnKey, null, Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Yêu cầu đổi mật khẩu")
        }
    }
}

@Composable
fun ProfileTextField(label: String, value: String, keyboardType: KeyboardType = KeyboardType.Text, onValueChange: (String) -> Unit) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
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