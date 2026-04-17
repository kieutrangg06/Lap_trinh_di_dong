package com.example.matestudy.ui.screen

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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
    var searchQuery by remember { mutableStateOf("") }

    val filterOptions = listOf("Tất cả", "Chờ duyệt", "Đã duyệt", "Bị ẩn")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Phê duyệt bài viết", fontWeight = FontWeight.Bold) },
                actions = {
                    Box {
                        Button(
                            onClick = { expandedFilter = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink)
                        ) {
                            Text(currentFilter)
                            Icon(Icons.Default.ArrowDropDown, null)
                        }
                        DropdownMenu(
                            expanded = expandedFilter,
                            onDismissRequest = { expandedFilter = false }
                        ) {
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
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // ────────────────────────────────────────────────
            // 1. THANH TÌM KIẾM
            // ────────────────────────────────────────────────
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.setSearchQuery(it)
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Tìm theo tiêu đề hoặc tác giả...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(12.dp)
            )

            // ────────────────────────────────────────────────
            // 2. DANH SÁCH BÀI VIẾT
            // ────────────────────────────────────────────────
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(posts) { post ->
                    AdminPostCard(
                        post = post,
                        onApprove = { viewModel.approvePost(post.id) },
                        onHide = { viewModel.hidePost(post.id) },
                        onDelete = { viewModel.deletePost(post.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminPostCard(
    post: Post,
    onApprove: () -> Unit,
    onHide: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        .format(Date(post.ngayDang ?: 0L))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // ────────────────────────────────────────────────
            // 3. THÔNG TIN TÁC GIẢ & THỜI GIAN
            // ────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = post.tacGiaAvatar?.takeIf { it.isNotBlank() }
                        ?: "https://ui-avatars.com/api/?name=${post.tacGiaTen}&background=random",
                    contentDescription = null,
                    modifier = Modifier.size(36.dp).clip(CircleShape)
                )
                Spacer(Modifier.width(8.dp))
                Text(post.tacGiaTen, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Text(dateStr, fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(Modifier.height(12.dp))
            Text(post.tieuDe, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF8B1A1A))
            Text(post.noiDung, fontSize = 14.sp, color = Color.DarkGray)

            // ────────────────────────────────────────────────
            // 4. HÌNH ẢNH ĐÍNH KÈM
            // ────────────────────────────────────────────────
            if (!post.fileDinhKem.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                AsyncImage(
                    model = post.fileDinhKem,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(16.dp))

            // ────────────────────────────────────────────────
            // 5. TRẠNG THÁI & CÁC NÚT THAO TÁC
            // ────────────────────────────────────────────────
            val (statusText, statusColor) = when (post.trangThai) {
                "da_duyet" -> "Đã duyệt" to Color(0xFF4CAF50)
                "cho_duyet" -> "Đang chờ" to Color(0xFFFF9800)
                "bi_an" -> "Đã ẩn" to Color(0xFF9E9E9E)
                else -> "Lỗi" to Color.Red
            }
            Surface(color = statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                Text(statusText, color = statusColor, modifier = Modifier.padding(8.dp, 4.dp), fontSize = 12.sp)
            }

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (post.trangThai != "da_duyet") {
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Icon(Icons.Default.Check, null)
                        Text("Duyệt")
                    }
                } else {
                    OutlinedButton(onClick = onHide, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.VisibilityOff, null)
                        Text("Ẩn bài")
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                }
            }
        }
    }
}