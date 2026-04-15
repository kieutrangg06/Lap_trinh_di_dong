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
import androidx.compose.ui.text.style.TextOverflow
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
                title = { Text("Quản lý bài viết", fontWeight = FontWeight.Bold) },
                actions = {
                    Box {
                        Button(onClick = { expandedFilter = true }) {
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
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Thanh tìm kiếm
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.setSearchQuery(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 8.dp),
                placeholder = { Text("Tìm kiếm theo tiêu đề hoặc nội dung...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Danh sách bài viết dạng Card
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(posts) { post ->
                    AdminPostCard(
                        post = post,
                        onApprove = { viewModel.approvePost(post.id) },
                        onHide = { viewModel.hidePost(post.id) },
                        onDelete = { viewModel.deletePost(post.id) }
                    )
                }

                if (posts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Không tìm thấy bài viết nào",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
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
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val dateStr = post.ngayDang?.let { sdf.format(Date(it)) } ?: "Không rõ"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Thông tin người đăng và thời gian
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = post.tacGiaAvatar?.takeIf { it.isNotBlank() }
                        ?: "https://ui-avatars.com/api/?name=${post.tacGiaTen.ifBlank { "SV" }}&background=random",
                    contentDescription = null,
                    modifier = Modifier.size(32.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Tác giả: ${post.tacGiaTen.ifBlank { "Sinh viên ${post.tacGiaId}" }}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = dateStr,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.height(12.dp))

            // Tiêu đề
            Text(
                text = post.tieuDe,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF8B1A1A),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))

            // Nội dung bài viết
            Text(
                text = post.noiDung ?: "Không có nội dung",
                fontSize = 14.sp,
                lineHeight = 20.sp,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                color = Color.DarkGray
            )

            // Hiển thị hình ảnh nếu có
//            if (!post.hinhAnh.isNullOrEmpty()) {
//                Spacer(Modifier.height(12.dp))
//                AsyncImage(
//                    model = post.hinhAnh,
//                    contentDescription = "Hình ảnh bài viết",
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp)
//                        .clip(RoundedCornerShape(12.dp)),
//                    contentScale = ContentScale.Crop
//                )
//            }

            // Hiển thị file đính kèm nếu có
            if (!post.fileDinhKem.isNullOrEmpty()) {
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AttachFile, contentDescription = null, tint = Color.Blue)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "File: ${post.fileDinhKem}",
                        fontSize = 13.sp,
                        color = Color.Blue
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Trạng thái
            val (statusText, statusColor) = when (post.trangThai) {
                "da_duyet" -> "Đã duyệt" to Color(0xFF4CAF50)
                "cho_duyet" -> "Chờ duyệt" to Color(0xFFFF9800)
                "bi_an" -> "Bị ẩn" to Color(0xFF9E9E9E)
                else -> "Không rõ" to Color.Gray
            }

            Surface(
                color = statusColor,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = statusText,
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Nút hành động
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (post.trangThai != "da_duyet") {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Duyệt")
                    }
                } else {
                    Button(
                        onClick = onHide,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.VisibilityOff, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Ẩn bài")
                    }
                }

                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Xóa")
                }
            }
        }
    }
}