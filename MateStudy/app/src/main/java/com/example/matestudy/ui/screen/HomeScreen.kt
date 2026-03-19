package com.example.matestudy.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import coil.compose.AsyncImage
import com.example.matestudy.data.Comment
import com.example.matestudy.data.Post
import com.example.matestudy.ui.theme.*
import com.example.matestudy.ui.viewmodel.ForumViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        containerColor = BackgroundGradientEnd,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNewPost,
                containerColor = PrimaryPink,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Đăng bài mới", modifier = Modifier.size(28.dp))
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Thanh tìm kiếm cao cấp
            Surface(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = forumViewModel::onSearchQueryChange,
                    placeholder = { Text("Tìm kiếm bài viết...", color = TextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = PrimaryPink) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )
            }

            // Danh mục dạng Tab cuộn
            val tabs = listOf(
                "Tất cả" to "all",
                "Nổi bật" to "featured",
                "Diễn đàn SV" to "forum",
                "Bản tin" to "news",
                "Chợ SV" to "market",
                "Việc làm" to "job",
                "Mất đồ" to "lost"
            )

            ScrollableTabRow(
                selectedTabIndex = tabs.indexOfFirst { it.second == currentCategory }.coerceAtLeast(0),
                containerColor = Color.Transparent,
                edgePadding = 16.dp,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[tabs.indexOfFirst { it.second == currentCategory }.coerceAtLeast(0)]),
                        color = PrimaryPink
                    )
                }
            ) {
                tabs.forEach { (title, cat) ->
                    val selected = currentCategory == cat
                    Tab(
                        selected = selected,
                        onClick = { forumViewModel.setCategory(cat) },
                        text = {
                            Text(
                                title,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) PrimaryPink else TextSecondary
                            )
                        }
                    )
                }
            }

            // List bài viết
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredPosts) { post ->
                    PostCard(
                        post = post,
                        onClick = { onNavigateToDetail(post.id) },
                        onLike = { forumViewModel.toggleLike(post.id) }
                    )
                }
                item { Spacer(modifier = Modifier.height(70.dp)) }
            }

            error?.let {
                Text(it, color = ErrorRed, modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun PostCard(post: Post, onClick: () -> Unit, onLike: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = "https://ui-avatars.com/api/?name=${post.tacGiaId}&background=random",
                    contentDescription = null,
                    modifier = Modifier.size(42.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Sinh viên ${post.tacGiaId}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(formatDate(post.ngayDang), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
                Spacer(Modifier.weight(1f))
                Surface(color = PrimaryLight, shape = RoundedCornerShape(8.dp)) {
                    Text(
                        post.category.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryPink,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(post.tieuDe, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold))
            Spacer(Modifier.height(6.dp))
            Text(
                text = post.noiDung,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                maxLines = 3
            )

            if (post.fileDinhKem != null) {
                Surface(
                    modifier = Modifier.padding(top = 10.dp),
                    color = Color(0xFFF5F5F5),
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AttachFile, null, Modifier.size(14.dp), tint = Color.Gray)
                        Text(post.fileDinhKem, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    Modifier.clickable { onLike() }.padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                        contentDescription = null,
                        tint = if (post.isLiked) PrimaryPink else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("${post.likeCount}", color = if (post.isLiked) PrimaryPink else TextSecondary, fontWeight = FontWeight.Medium)
                }

                Spacer(Modifier.width(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.ChatBubbleOutline, null, Modifier.size(20.dp), tint = TextSecondary)
                    Spacer(Modifier.width(6.dp))
                    Text("Bình luận", color = TextSecondary, fontSize = 14.sp)
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
    onPostSuccess: () -> Unit = {}
) {
    var tieuDe by remember { mutableStateOf("") }
    var noiDung by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("forum") }
    var filePath by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val categories = listOf("forum" to "Diễn đàn SV", "news" to "Bản tin", "market" to "Chợ SV", "job" to "Việc làm", "lost" to "Mất đồ")
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> filePath = uri?.path }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tạo bài viết", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.Close, null) } },
                actions = {
                    TextButton(onClick = {
                        if (tieuDe.isNotBlank()) {
                            viewModel.createPost(tieuDe.trim(), noiDung.trim(), selectedCategory, filePath)
                            onPostSuccess()
                        }
                    }) {
                        Text("ĐĂNG", fontWeight = FontWeight.Black, color = PrimaryPink)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(20.dp).fillMaxSize()) {
            OutlinedTextField(
                value = tieuDe,
                onValueChange = { tieuDe = it },
                placeholder = { Text("Tiêu đề bài viết") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPink,
                    unfocusedBorderColor = Color.LightGray,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(Modifier.height(16.dp))

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = categories.find { it.first == selectedCategory }?.second ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Danh mục") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    categories.forEach { (key, label) ->
                        DropdownMenuItem(text = { Text(label) }, onClick = { selectedCategory = key; expanded = false })
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            TextField(
                value = noiDung,
                onValueChange = { noiDung = it },
                placeholder = { Text("Nội dung bài viết...") },
                modifier = Modifier.fillMaxWidth().weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            HorizontalDivider(Modifier.padding(vertical = 12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { launcher.launch("*/*") }) {
                    Icon(Icons.Default.AttachFile, null, tint = SecondaryBlue)
                }
                Text("Đính kèm tài liệu", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                if (filePath != null) {
                    Spacer(Modifier.width(8.dp))
                    Text("✓", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    viewModel: ForumViewModel,
    postId: Long,
    onBack: () -> Unit
) {
    // CHỈ gọi load dữ liệu khi ID thay đổi hoặc màn hình bắt đầu
    LaunchedEffect(postId) {
        viewModel.loadPostDetail(postId)
    }

    val post by viewModel.selectedPost.collectAsState()
    val comments by viewModel.comments.collectAsState()
    var commentText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết bài viết", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                modifier = Modifier.imePadding() // Tự đẩy lên khi hiện bàn phím
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Viết bình luận...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryPink,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                viewModel.addComment(postId, commentText.trim())
                                commentText = ""
                            }
                        },
                        enabled = commentText.isNotBlank(),
                        modifier = Modifier
                            .background(if (commentText.isNotBlank()) PrimaryPink else Color.LightGray, CircleShape)
                    ) {
                        Icon(Icons.Default.Send, null, tint = Color.White)
                    }
                }
            }
        }
    ) { padding ->
        if (post != null) {
            // ĐOẠN LAZYCOLUMN ĐÃ SỬA LỖI TRÙNG KEY
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA)),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                // Sử dụng key duy nhất cho bài viết gốc bằng tiền tố "post_"
                item(key = "post_header_${postId}") {
                    PostCard(
                        post = post!!,
                        onClick = {},
                        onLike = { viewModel.toggleLike(postId) }
                    )

                    Text(
                        text = "Bình luận (${comments.size})",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                if (comments.isEmpty()) {
                    item(key = "empty_state") {
                        Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("Chưa có bình luận nào.", color = TextSecondary)
                        }
                    }
                } else {
                    // Sử dụng key duy nhất cho từng bình luận bằng tiền tố "comment_"
                    items(
                        items = comments,
                        key = { comment -> "comment_${comment.id}" }
                    ) { comment ->
                        CommentItem(comment)
                    }
                }
            }
        } else {
            // Màn hình chờ tải dữ liệu
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryPink)
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        AsyncImage(
            model = "https://ui-avatars.com/api/?name=${comment.tacGiaId}&background=random",
            contentDescription = null,
            modifier = Modifier.size(34.dp).clip(CircleShape)
        )

        Spacer(Modifier.width(10.dp))

        Column {
            Surface(
                color = Color(0xFFE9ECEF),
                shape = RoundedCornerShape(0.dp, 16.dp, 16.dp, 16.dp)
            ) {
                Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(
                        text = "Sinh viên ${comment.tacGiaId}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = PrimaryPink
                    )
                    Text(text = comment.noiDung, color = Color.Black, fontSize = 14.sp)
                }
            }
            Text(
                text = formatDate(comment.ngayTao),
                fontSize = 10.sp,
                color = TextSecondary,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}

private fun formatDate(timestamp: Long?): String {
    if (timestamp == null || timestamp <= 0) return "Vừa xong"
    return SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(timestamp))
}