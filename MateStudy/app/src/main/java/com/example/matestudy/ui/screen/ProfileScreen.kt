// ui/screen/ProfileScreen.kt
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.matestudy.ui.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    onChangePasswordClick: () -> Unit,
    viewModel: AuthViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Các trường có thể chỉnh sửa
    var editedUsername by remember { mutableStateOf(currentUser.tenDangNhap) }
    var editedEmail by remember { mutableStateOf(currentUser.email) }
    var editedNienKhoa by remember { mutableStateOf(currentUser.nienKhoa?.toString() ?: "") }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    // Đồng bộ lại khi currentUser thay đổi (sau khi lưu thành công hoặc reload)
    LaunchedEffect(currentUser) {
        editedUsername = currentUser.tenDangNhap
        editedEmail = currentUser.email
        editedNienKhoa = currentUser.nienKhoa?.toString() ?: ""
        selectedImageUri = null // reset ảnh đã chọn nếu reload
    }

    // Launcher chọn ảnh
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedImageUri = it.toString() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            val displayImage = selectedImageUri ?: currentUser.anhDaiDien
            if (!displayImage.isNullOrBlank()) {
                AsyncImage(
                    model = displayImage,
                    contentDescription = "Ảnh đại diện",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Chọn ảnh đại diện mới")
        }

        Spacer(Modifier.height(32.dp))

        // Thông tin cá nhân – tất cả các trường có thể sửa đều là OutlinedTextField
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Tên đăng nhập
                OutlinedTextField(
                    value = editedUsername,
                    onValueChange = { editedUsername = it },
                    label = { Text("Tên đăng nhập") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    singleLine = true,
                    enabled = !isLoading
                )

                // Email
                OutlinedTextField(
                    value = editedEmail,
                    onValueChange = { editedEmail = it },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    singleLine = true,
                    enabled = !isLoading
                )

                // Niên khóa
                OutlinedTextField(
                    value = editedNienKhoa,
                    onValueChange = { editedNienKhoa = it },
                    label = { Text("Niên khóa (VD: 2023)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    singleLine = true,
                    enabled = !isLoading
                )

                // Vai trò - chỉ hiển thị, không sửa
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Vai trò:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (currentUser.vaiTro == "sinh_vien") "Sinh viên" else "Admin",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // Nút hành động
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    // Reset về giá trị gốc
                    editedUsername = currentUser.tenDangNhap
                    editedEmail = currentUser.email
                    editedNienKhoa = currentUser.nienKhoa?.toString() ?: ""
                    selectedImageUri = null
                },
                modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) {
                Text("HỦY")
            }

            Button(
                onClick = {
                    viewModel.updateProfile(
                        newUsername = editedUsername,
                        newEmail = editedEmail,
                        newNienKhoa = editedNienKhoa.toIntOrNull(),
                        newAvatarUri = selectedImageUri
                    )
                },
                enabled = !isLoading,
                modifier = Modifier.weight(1f)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("LƯU THAY ĐỔI")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onChangePasswordClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ĐỔI MẬT KHẨU")
        }

        if (!error.isNullOrBlank()) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}