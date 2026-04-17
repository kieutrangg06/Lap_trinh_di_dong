package com.example.matestudy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.example.matestudy.data.Comment
import com.example.matestudy.data.Post
import com.example.matestudy.ui.theme.*
import com.example.matestudy.ui.viewmodel.ForumViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ────────────────────────────────────────────────
// 1. MÀN HÌNH CHÍNH DIỄN ĐÀN (HOME)
// ────────────────────────────────────────────────

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

// ────────────────────────────────────────────────
// 2. MÀN HÌNH TẠO BÀI VIẾT MỚI
// ────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPostScreen(
    viewModel: ForumViewModel,
    onBack: () -> Unit,
    onPostSuccess: () -> Unit = {}
) {
    var tieuDe by remember { mutableStateOf("") }
    var noiDung by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("forum") }
    var expanded by remember { mutableStateOf(false) }

    val categories = listOf(
        "forum" to "Diễn đàn SV",
        "news" to "Bản tin",
        "market" to "Chợ SV",
        "job" to "Việc làm",
        "lost" to "Mất đồ"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tạo bài viết mới", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.Close, null) }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (tieuDe.isNotBlank() && noiDung.isNotBlank()) {
                                viewModel.createPost(
                                    tieuDe.trim(),
                                    noiDung.trim(),
                                    selectedCategory,
                                    imageUrl.trim().ifBlank { null }
                                )
                                onPostSuccess()
                            }
                        },
                        enabled = tieuDe.isNotBlank() && noiDung.isNotBlank()
                    ) {
                        Text("ĐĂNG", fontWeight = FontWeight.Black, color = PrimaryPink)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = tieuDe,
                onValueChange = { tieuDe = it },
                placeholder = { Text("Tiêu đề bài viết") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPink,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
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

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Link ảnh minh họa (URL)") },
                placeholder = { Text("https://example.com/image.png") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, tint = SecondaryBlue) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            TextField(
                value = noiDung,
                onValueChange = { noiDung = it },
                placeholder = { Text("Nội dung bài viết...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFFBFBFB),
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(Modifier.height(20.dp))

            Text(
                "Lưu ý: Bài viết sau khi đăng sẽ ở trạng thái Chờ duyệt bởi Admin.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

// ────────────────────────────────────────────────
// 3. MÀN HÌNH CHI TIẾT BÀI VIẾT
// ────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
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
                modifier = Modifier.imePadding()
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
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA)),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
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
                    items(
                        items = comments,
                        key = { comment -> "comment_${comment.id}" }
                    ) { comment ->
                        CommentItem(comment)
                    }
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryPink)
            }
        }
    }
}

// ────────────────────────────────────────────────
// 4. CÁC COMPONENT HIỂN THỊ (CARD & ITEM)
// ────────────────────────────────────────────────

@Composable
fun PostCard(
    post: Post,
    onClick: () -> Unit,
    onLike: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = post.tacGiaAvatar?.takeIf { it.isNotBlank() }
                        ?: "https://ui-avatars.com/api/?name=${post.tacGiaTen.ifBlank { "SV" }}&background=random",
                    contentDescription = null,
                    modifier = Modifier.size(42.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = post.tacGiaTen.ifBlank { "Sinh viên ${post.tacGiaId}" },
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        formatDate(post.ngayDang),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
                Spacer(Modifier.weight(1f))
                Surface(
                    color = PrimaryLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
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

            Text(
                post.tieuDe,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = post.noiDung,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            if (!post.fileDinhKem.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                AsyncImage(
                    model = post.fileDinhKem,
                    contentDescription = "Hình ảnh bài viết",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 250.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = android.R.drawable.ic_menu_gallery)
                )
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
                    Text(
                        "${post.likeCount}",
                        color = if (post.isLiked) PrimaryPink else TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
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

@Composable
fun CommentItem(comment: Comment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        AsyncImage(
            model = comment.tacGiaAvatar?.takeIf { it.isNotBlank() }
                ?: "https://ui-avatars.com/api/?name=${comment.tacGiaTen.ifBlank { comment.tacGiaId.toString() }}&background=random",
            contentDescription = null,
            modifier = Modifier.size(34.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(10.dp))

        Column {
            Surface(
                color = Color(0xFFE9ECEF),
                shape = RoundedCornerShape(0.dp, 16.dp, 16.dp, 16.dp)
            ) {
                Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(
                        text = comment.tacGiaTen.ifBlank { "Sinh viên ${comment.tacGiaId}" },
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