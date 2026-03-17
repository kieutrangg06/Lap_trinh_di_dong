package com.example.matestudy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matestudy.ui.viewmodel.ThongBaoViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.example.matestudy.data.entity.ThongBaoEntity
import androidx.compose.foundation.shape.CircleShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: ThongBaoViewModel,
    onNavigateToPost: (Long) -> Unit,     // → PostDetailScreen
    onNavigateToReview: (Long) -> Unit,   // → màn hình chi tiết đánh giá (tùy bạn)
    onBack: () -> Unit
) {
    val thongBaos by viewModel.thongBaos.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông báo") },
                actions = {
                    if (unreadCount > 0) {
                        TextButton(onClick = { viewModel.markAllAsRead() }) {
                            Text("Đánh dấu tất cả đã đọc")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Tab giả lập giống ảnh (có thể thay bằng TabRow thật nếu cần)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = true,
                    onClick = { /* TODO: lọc tất cả */ },
                    label = { Text("Tất cả $unreadCount") }
                )
                FilterChip(
                    selected = false,
                    onClick = { /* TODO: lọc học tập */ },
                    label = { Text("Học tập") }
                )
                FilterChip(
                    selected = false,
                    onClick = { /* TODO: lọc bài viết & đánh giá */ },
                    label = { Text("Bài viết & Đánh giá") }
                )
            }

            if (thongBaos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Chưa có thông báo nào", color = Color.Gray)
                }
            } else {
                LazyColumn {
                    items(thongBaos) { tb ->
                        NotificationItem(
                            thongBao = tb,
                            onClick = {
                                viewModel.markAsRead(tb.id)
                                when (tb.loai) {
                                    "bai_viet" -> {
                                        if (tb.loaiLienQuan == "post" && tb.idLienQuan != null) {
                                            onNavigateToPost(tb.idLienQuan)
                                        }
                                    }
                                    "danh_gia" -> {
                                        if (tb.loaiLienQuan == "review" && tb.idLienQuan != null) {
                                            onNavigateToReview(tb.idLienQuan)
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    thongBao: ThongBaoEntity,
    onClick: () -> Unit
) {
    val formatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (!thongBao.daDoc) Color(0xFFF0F8FF) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon bên trái
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (thongBao.loai == "bai_viet") Color(0xFF1976D2) else Color(0xFF4CAF50),
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = thongBao.tieuDe,
                    fontWeight = if (!thongBao.daDoc) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = thongBao.noiDung,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatter.format(Date(thongBao.ngayTao)),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            if (!thongBao.daDoc) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color(0xFF1976D2), shape = CircleShape)
                )
            }
        }
    }
}