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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matestudy.data.entity.UserEntity
import com.example.matestudy.ui.theme.*
import com.example.matestudy.ui.viewmodel.AdminUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(viewModel: AdminUserViewModel) {
    val users by viewModel.filteredUsers.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))) {
        // Thanh công cụ tìm kiếm & lọc
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.onSearchChange(it) },
                    placeholder = { Text("Tìm tên đăng nhập, email...", fontSize = 14.sp) },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    leadingIcon = { Icon(Icons.Default.Search, null) }
                )
                Spacer(Modifier.width(8.dp))
                // Có thể thêm nút lọc nâng cao ở đây như hình
                IconButton(onClick = {}, modifier = Modifier.background(Color(0xFFA52A2A), RoundedCornerShape(8.dp))) {
                    Icon(Icons.Default.FilterAlt, null, tint = Color.White)
                }
            }
        }

        // Bảng danh sách
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text("SINH VIÊN", Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.DarkGray)
                    Text("NIÊN KHÓA", Modifier.weight(0.8f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.DarkGray)
                    Text("TRẠNG THÁI", Modifier.weight(0.8f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.DarkGray)
                    Text("HÀNH ĐỘNG", Modifier.weight(0.7f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.DarkGray)
                }
            }

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

@Composable
fun UserAdminRow(user: UserEntity, onToggleLock: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cột Sinh viên (Avatar + Info)
            Row(Modifier.weight(1.5f), verticalAlignment = Alignment.CenterVertically) {
                Surface(Modifier.size(40.dp).clip(CircleShape), color = Color.LightGray) {
                    Icon(Icons.Default.Person, null, Modifier.padding(8.dp), tint = Color.White)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(user.tenDangNhap, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(user.email, fontSize = 12.sp, color = Color.Gray)
                }
            }

            // Cột Niên khóa
            Text("K${user.nienKhoa ?: ""}", Modifier.weight(0.8f), fontSize = 13.sp)

            // Cột Trạng thái
            Box(Modifier.weight(0.8f)) {
                val isActive = user.trangThai == "hoat_dong"
                Surface(
                    color = if (isActive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isActive) "Hoạt động" else "Bị khóa",
                        color = if (isActive) Color(0xFF2E7D32) else Color(0xFFC62828),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Cột Hành động
            Row(Modifier.weight(0.7f), horizontalArrangement = Arrangement.End) {
                IconButton(
                    onClick = onToggleLock,
                    modifier = Modifier.size(32.dp).background(Color(0xFFFFC107), RoundedCornerShape(4.dp))
                ) {
                    Icon(
                        if (user.trangThai == "hoat_dong") Icons.Default.LockOpen else Icons.Default.Lock,
                        null, tint = Color.White, modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp).background(Color(0xFFE91E63), RoundedCornerShape(4.dp))
                ) {
                    Icon(Icons.Default.Delete, null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}