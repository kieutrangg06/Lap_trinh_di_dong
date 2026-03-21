package com.example.matestudy.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matestudy.ui.viewmodel.ThongBaoViewModel
import com.example.matestudy.data.entity.ThongBaoEntity
import com.example.matestudy.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: ThongBaoViewModel,
    onNavigateToPost: (Long) -> Unit,
    onNavigateToReview: (Long) -> Unit,
    onBack: () -> Unit
) {
    val thongBaos by viewModel.thongBaos.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()

    Scaffold(
        containerColor = BackgroundGradientEnd,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Thông báo", fontWeight = FontWeight.Bold) },
                actions = {
                    if (unreadCount > 0) {
                        TextButton(onClick = { viewModel.markAllAsRead() }) {
                            Text("Đọc tất cả", color = PrimaryPink, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Filter Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SuggestionChip(
                    onClick = { },
                    label = { Text("Tất cả ($unreadCount)") },
                    border = null,
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = PrimaryPink, labelColor = Color.White)
                )
                SuggestionChip(onClick = { }, label = { Text("Bài viết") })
                SuggestionChip(onClick = { }, label = { Text("Đánh giá") })
            }

            if (thongBaos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.NotificationsActive, null, Modifier.size(64.dp), tint = Color.LightGray)
                        Text("Không có thông báo mới", color = TextSecondary, modifier = Modifier.padding(top = 16.dp))
                    }
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(thongBaos) { tb ->
                        NotificationItem(
                            thongBao = tb,
                            onClick = {
                                viewModel.markAsRead(tb.id)
                                when (tb.loai) {
                                    "bai_viet" -> if (tb.idLienQuan != null) onNavigateToPost(tb.idLienQuan)
                                    "danh_gia" -> if (tb.idLienQuan != null) onNavigateToReview(tb.idLienQuan)
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
fun NotificationItem(thongBao: ThongBaoEntity, onClick: () -> Unit) {
    val formatter = remember { SimpleDateFormat("HH:mm, dd/MM", Locale.getDefault()) }
    val isUnread = !thongBao.daDoc

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = if (isUnread) Color.White else Color(0xFFF9FAFB)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnread) 4.dp else 0.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (thongBao.loai == "bai_viet") Color(0xFFE3F2FD) else Color(0xFFE8F5E9)
            ) {
                Icon(
                    imageVector = if (thongBao.loai == "bai_viet") Icons.Default.Assignment else Icons.Default.StarHalf,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = if (thongBao.loai == "bai_viet") Color(0xFF1E88E5) else Color(0xFF43A047)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(thongBao.tieuDe, fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Medium, fontSize = 15.sp)
                Text(thongBao.noiDung, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 2)
                Text(formatter.format(Date(thongBao.ngayTao)), style = MaterialTheme.typography.labelSmall, color = Color.LightGray, modifier = Modifier.padding(top = 4.dp))
            }

            if (isUnread) {
                Icon(Icons.Default.Circle, null, Modifier.size(10.dp).padding(top = 4.dp), tint = PrimaryPink)
            }
        }
    }
}