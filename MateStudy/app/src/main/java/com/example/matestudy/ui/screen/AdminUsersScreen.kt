package com.example.matestudy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.matestudy.data.entity.UserEntity
import com.example.matestudy.ui.theme.*
import com.example.matestudy.ui.viewmodel.AdminUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(viewModel: AdminUserViewModel) {
    val users by viewModel.filteredUsers.collectAsState()
    val query by viewModel.searchQuery.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // ────────────────────────────────────────────────
        // 1. THANH TÌM KIẾM
        // ────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onSearchChange(it) },
                placeholder = { Text("Tìm tên đăng nhập, email...", fontSize = 14.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFA52A2A),
                    unfocusedBorderColor = Color.LightGray,
                    focusedLeadingIconColor = Color.Gray,
                    unfocusedLeadingIconColor = Color.Gray,
                )
            )
        }

        // ────────────────────────────────────────────────
        // 2. HEADER BẢNG DANH SÁCH
        // ────────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("SINH VIÊN", modifier = Modifier.weight(1.6f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.DarkGray)
                Text("NIÊN KHÓA", modifier = Modifier.weight(0.9f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.DarkGray)
                Text("TRẠNG THÁI", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.DarkGray)
                Text("HÀNH ĐỘNG", modifier = Modifier.weight(0.9f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.DarkGray, textAlign = TextAlign.End)
            }
        }

        // ────────────────────────────────────────────────
        // 3. DANH SÁCH NGƯỜI DÙNG
        // ────────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 16.dp)
        ) {
            items(users) { user ->
                UserAdminRow(
                    user = user,
                    onToggleLock = { viewModel.toggleLock(user) },
                    onDelete = { viewModel.deleteUser(user.id) }
                )
            }
        }
    }
}

// ────────────────────────────────────────────────
// 4. THÀNH PHẦN HIỂN THỊ DÒNG NGƯỜI DÙNG
// ────────────────────────────────────────────────

@Composable
fun UserAdminRow(
    user: UserEntity,
    onToggleLock: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1.6f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    color = Color(0xFFE0E0E0)
                ) {
                    AsyncImage(
                        model = user.anhDaiDien?.takeIf { it.isNotBlank() } ?: "https://via.placeholder.com/150",
                        contentDescription = "Avatar của ${user.tenDangNhap}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        text = user.tenDangNhap,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = user.email,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

            Text(
                text = "K${user.nienKhoa ?: ""}",
                modifier = Modifier.weight(0.9f),
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Box(modifier = Modifier.weight(1f)) {
                val isActive = user.trangThai == "hoat_dong"

                Surface(
                    color = if (isActive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = if (isActive) "Hoạt động" else "Bị khóa",
                        color = if (isActive) Color(0xFF2E7D32) else Color(0xFFC62828),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.weight(0.9f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onToggleLock,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFFFC107), RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = if (user.trangThai == "hoat_dong") Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFE91E63), RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}