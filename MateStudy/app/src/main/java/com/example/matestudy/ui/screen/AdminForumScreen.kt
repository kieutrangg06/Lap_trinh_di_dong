package com.example.matestudy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matestudy.data.Post
import com.example.matestudy.ui.theme.*
import com.example.matestudy.ui.viewmodel.AdminForumViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminForumScreen(viewModel: AdminForumViewModel) {
    val posts by viewModel.filteredPosts.collectAsState()
    val currentFilter by viewModel.filterStatus.collectAsState()
    var expandedFilter by remember { mutableStateOf(false) }

    val filterOptions = listOf("Tất cả", "Chờ duyệt", "Đã duyệt", "Bị ẩn")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý bài viết", fontWeight = FontWeight.Bold) },
                actions = {
                    Box {
                        Button(onClick = { expandedFilter = true }) {
                            Text(currentFilter)
                            Icon(Icons.Default.ArrowDropDown, null)
                        }
                        DropdownMenu(expanded = expandedFilter, onDismissRequest = { expandedFilter = false }) {
                            filterOptions.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status) },
                                    onClick = {
                                        viewModel.setFilter(status)
                                        expandedFilter = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Header bảng
            item {
                Row(
                    Modifier.fillMaxWidth().background(Color.LightGray.copy(0.3f)).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("TIÊU ĐỀ", Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("TRẠNG THÁI", Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("HÀNH ĐỘNG", Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            items(posts) { post ->
                AdminPostRow(
                    post = post,
                    onApprove = { viewModel.approvePost(post.id) },
                    onHide = { viewModel.hidePost(post.id) },
                    onDelete = { viewModel.deletePost(post.id) }
                )
                Divider(color = Color.LightGray.copy(0.5f))
            }
        }
    }
}

@Composable
fun AdminPostRow(post: Post, onApprove: () -> Unit, onHide: () -> Unit, onDelete: () -> Unit) {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val dateStr = post.ngayDang?.let { sdf.format(Date(it)) } ?: ""

    Row(
        Modifier.fillMaxWidth().padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cột Tiêu đề & Tác giả
        Column(Modifier.weight(1.5f)) {
            Text(post.tieuDe, fontWeight = FontWeight.Bold, color = Color(0xFF8B1A1A))
            Text("Tác giả: ${post.tacGiaId}", fontSize = 11.sp, color = Color.Gray)
            Text(dateStr, fontSize = 10.sp, color = Color.Gray)
        }

        // Cột Trạng thái (Chip)
        Box(Modifier.weight(1f)) {
            val (text, color) = when (post.trangThai) {
                "da_duyet" -> "Đã duyệt" to Color(0xFF4CAF50)
                "cho_duyet" -> "Chờ duyệt" to Color(0xFFFF9800)
                else -> "Bị ẩn" to Color.Gray
            }
            Surface(color = color, shape = RoundedCornerShape(12.dp)) {
                Text(text, color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
            }
        }

        // Cột Hành động
        Row(Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
            if (post.trangThai != "da_duyet") {
                IconButton(onClick = onApprove) {
                    Icon(Icons.Default.CheckCircle, "Duyệt", tint = Color(0xFF4CAF50))
                }
            } else {
                IconButton(onClick = onHide) {
                    Icon(Icons.Default.VisibilityOff, "Ẩn", tint = Color(0xFFFF9800))
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Xóa", tint = Color.Red)
            }
        }
    }
}