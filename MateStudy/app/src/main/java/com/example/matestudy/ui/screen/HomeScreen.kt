package com.example.matestudy.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Search
import coil.compose.AsyncImage
import com.example.matestudy.data.Post
import com.example.matestudy.ui.viewmodel.ForumViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material.icons.filled.Send

@Composable
fun HomeScreen(
    forumViewModel: ForumViewModel,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToNewPost: () -> Unit
) {
    val filteredPosts by forumViewModel.filteredPosts.collectAsState()
    val currentCategory by forumViewModel.currentCategory.collectAsState()
    val searchQuery by forumViewModel.searchQuery.collectAsState()
    val error by forumViewModel.error.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Tìm kiếm
        TextField(
            value = searchQuery,
            onValueChange = forumViewModel::onSearchQueryChange,
            label = { Text("Tìm kiếm bài viết...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // Tabs danh mục
        val tabs = listOf(
            "Tất cả" to "all",
            "Nổi bật" to "featured",
            "Diễn đàn SV" to "forum",
            "Bản tin" to "news",
            "Chợ SV" to "market",
            "Việc làm" to "job",
            "Mất đồ" to "lost"
        )
        TabRow(selectedTabIndex = tabs.indexOfFirst { it.second == currentCategory }) {
            tabs.forEach { (title, cat) ->
                Tab(
                    selected = currentCategory == cat,
                    onClick = { forumViewModel.setCategory(cat) },
                    text = { Text(title) }
                )
            }
        }

        // Danh sách bài viết
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filteredPosts) { post ->
                PostCard(
                    post = post,
                    onClick = { onNavigateToDetail(post.id) },
                    onLike = { forumViewModel.toggleLike(post.id) }
                )
            }
        }

        if (error != null) {
            Text(
                text = error ?: "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Nút đăng bài mới (FAB)
        FloatingActionButton(
            onClick = onNavigateToNewPost,
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Đăng bài mới")
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    onClick: () -> Unit,
    onLike: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onClick
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Avatar (nên thay bằng post.tacGiaAvatar nếu có)
            AsyncImage(
                model = "https://example.com/avatar.png",  // TODO: thay bằng post.tacGiaAvatar ?: placeholder
                contentDescription = "Avatar",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "sv${post.tacGiaId}",  // TODO: thay bằng tên thật nếu có
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formatDate(post.ngayDang),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = post.tieuDe,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = post.noiDung.take(120) + if (post.noiDung.length > 120) "..." else "",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (post.fileDinhKem != null) {
                    Text(
                        text = "File đính kèm: ${post.fileDinhKem}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onLike) {
                        Icon(
                            Icons.Default.ThumbUp,
                            contentDescription = null,
                            tint = if (post.isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text("${post.likeCount} Thích")
                    Spacer(modifier = Modifier.width(24.dp))
                    IconButton(onClick = onClick) {
                        Icon(Icons.Default.Comment, contentDescription = null)
                    }
                    Text("0 Bình luận")  // TODO: thay bằng post.commentCount nếu có field
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPostScreen(
    viewModel: ForumViewModel,
    onBack: () -> Unit,
    onPostSuccess: () -> Unit = {}  // Thêm tham số này để fix lỗi ở navigation
) {
    var tieuDe by remember { mutableStateOf("") }
    var noiDung by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("forum") }
    var filePath by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { filePath = it.path }  // Lưu path tạm, sau này copy file nếu cần
    }

    val categories = listOf(
        "forum" to "Diễn đàn SV",
        "news" to "Bản tin",
        "market" to "Chợ SV",
        "job" to "Việc làm",
        "lost" to "Mất đồ"
    )

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = tieuDe,
            onValueChange = { tieuDe = it },
            label = { Text("Tiêu đề") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = noiDung,
            onValueChange = { noiDung = it },
            label = { Text("Nội dung") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            maxLines = 5
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown chọn danh mục
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = categories.find { it.first == selectedCategory }?.second ?: "Chọn danh mục",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { (key, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            selectedCategory = key
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { launcher.launch("*/*") }) {
            Text("Chọn file đính kèm")
        }
        if (filePath != null) {
            Text("Đã chọn: $filePath", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = onBack) {
                Text("Hủy")
            }
            Button(onClick = {
                if (tieuDe.trim().isBlank()) {
                    // TODO: có thể thêm snackbar thông báo lỗi
                    return@Button
                }
                viewModel.createPost(
                    tieuDe = tieuDe.trim(),
                    noiDung = noiDung.trim(),
                    category = selectedCategory,
                    filePath = filePath
                )
                onPostSuccess()  // Gọi callback khi đăng thành công → pop back stack
                // onBack()       // Nếu muốn pop ngay, nhưng nên để onPostSuccess xử lý
            }) {
                Text("Đăng bài")
            }
        }
    }
}

@Composable
fun PostDetailScreen(
    viewModel: ForumViewModel,
    postId: Long,
    onBack: () -> Unit
) {
    LaunchedEffect(postId) {
        viewModel.loadPostDetail(postId)
    }

    val post by viewModel.selectedPost.collectAsState()
    val comments by viewModel.comments.collectAsState()
    var newComment by remember { mutableStateOf("") }

    if (post == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(modifier = Modifier.padding(16.dp)) {
        PostCard(
            post = post!!,
            onClick = {},
            onLike = { viewModel.toggleLike(postId) }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text("Bình luận", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(comments) { comment ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(
                        text = "sv${comment.tacGiaId}: ${comment.noiDung}",
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newComment,
                onValueChange = { newComment = it },
                label = { Text("Viết bình luận...") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = {
                if (newComment.isNotBlank()) {
                    viewModel.addComment(postId, newComment.trim())
                    newComment = ""
                }
            }) {
                Icon(Icons.Default.Send, contentDescription = "Gửi bình luận")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onBack, modifier = Modifier.align(Alignment.End)) {
            Text("Quay lại")
        }
    }
}

private fun formatDate(timestamp: Long?): String {
    if (timestamp == null || timestamp <= 0) return "Không xác định"
    // Giả sử timestamp là millis → nếu là giây thì bỏ * 1000
    return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(timestamp)
}