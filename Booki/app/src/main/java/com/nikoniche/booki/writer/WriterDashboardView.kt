package com.nikoniche.booki.writer

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nikoniche.booki.Story

val WattpadOrange = Color(0xFFFF6D00)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriterDashboardView(viewModel: WriterViewModel, onManageChapters: (Story) -> Unit) {
    val stories by viewModel.myStories
    var showEditStoryDialog by remember { mutableStateOf<Story?>(null) }

    LaunchedEffect(Unit) { viewModel.loadMyStories() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("STUDIO CỦA TÔI", fontWeight = FontWeight.Black, letterSpacing = 1.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showEditStoryDialog = Story() },
                containerColor = WattpadOrange,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Rounded.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("TẠO TRUYỆN MỚI")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8F9FA)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Panel Thống kê
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Brush.horizontalGradient(listOf(WattpadOrange, Color(0xFFFF9E40))))
                        .padding(24.dp)
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatItem("Lượt đọc", stories.sumOf { it.viewCount }.toString(), Icons.Rounded.BarChart)
                        StatItem("Bình chọn", stories.sumOf { it.voteCount }.toString(), Icons.Rounded.Favorite)
                        StatItem("Tác phẩm", stories.size.toString(), Icons.Rounded.MenuBook)
                    }
                }
            }

            item { Text("Truyện của bạn", fontWeight = FontWeight.Bold, fontSize = 18.sp) }

            items(stories) { story ->
                StoryWriterCard(
                    story = story,
                    onEditInfo = { showEditStoryDialog = story },
                    onManageChapters = { onManageChapters(story) }
                )
            }
        }
    }

    if (showEditStoryDialog != null) {
        EditStoryDialog(
            story = showEditStoryDialog!!,
            onDismiss = { showEditStoryDialog = null },
            onConfirm = { updatedStory ->
                viewModel.saveStory(updatedStory)
                showEditStoryDialog = null
            }
        )
    }
}

@Composable
fun StoryWriterCard(story: Story, onEditInfo: () -> Unit, onManageChapters: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row {
                AsyncImage(
                    model = story.coverUrl.ifEmpty { "https://via.placeholder.com/150" },
                    contentDescription = null,
                    modifier = Modifier.size(70.dp, 100.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Column(Modifier.padding(start = 16.dp).weight(1f)) {
                    Text(story.title, fontWeight = FontWeight.Bold, fontSize = 17.sp, maxLines = 2)
                    Text(story.genres, color = WattpadOrange, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(4.dp))
                    Text("${story.chapterCount} chương • ${story.status}", color = Color.Gray, fontSize = 12.sp)
                }
            }
            Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onEditInfo,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.Settings, null, Modifier.size(16.dp))
                    Text(" Sửa thông tin", fontSize = 13.sp)
                }
                Button(
                    onClick = onManageChapters,
                    modifier = Modifier.weight(1.2f),
                    colors = ButtonDefaults.buttonColors(containerColor = WattpadOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.List, null, Modifier.size(16.dp))
                    Text(" Viết chương", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(color = Color.White.copy(0.2f), shape = RoundedCornerShape(12.dp)) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.padding(8.dp).size(24.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(value, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
        Text(label.uppercase(), color = Color.White.copy(0.8f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun EditStoryDialog(story: Story, onDismiss: () -> Unit, onConfirm: (Story) -> Unit) {
    var title by remember { mutableStateOf(story.title) }
    var desc by remember { mutableStateOf(story.description) }
    var genre by remember { mutableStateOf(story.genres) }
    var cover by remember { mutableStateOf(story.coverUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if(story.id.isEmpty()) "Tạo truyện mới" else "Chỉnh sửa truyện") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Tên tác phẩm") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = cover, onValueChange = { cover = it }, label = { Text("Link ảnh bìa (URL)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = genre, onValueChange = { genre = it }, label = { Text("Thể loại") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Tóm tắt nội dung") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(story.copy(title = title, description = desc, genres = genre, coverUrl = cover)) }, colors = ButtonDefaults.buttonColors(WattpadOrange)) {
                Text("Lưu lại")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } }
    )
}