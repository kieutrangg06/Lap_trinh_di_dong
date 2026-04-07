package com.nikoniche.booki.writer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nikoniche.booki.Chapter
import com.nikoniche.booki.Story

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterManagementView(story: Story, viewModel: WriterViewModel, onBack: () -> Unit) {
    val chapters by viewModel.chapters
    // State quản lý việc hiện Dialog (null là đóng, khác null là đang mở để thêm/sửa)
    var editingChapter by remember { mutableStateOf<Chapter?>(null) }

    LaunchedEffect(story.id) { viewModel.loadChapters(story.id) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(story.title, maxLines = 1, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Quản lý chương", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { editingChapter = Chapter(storyId = story.id, order = chapters.size + 1) },
                containerColor = WattpadOrange
            ) {
                Icon(Icons.Rounded.Add, null, tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize()) {
            items(chapters) { chapter ->
                ListItem(
                    headlineContent = { Text("Chương ${chapter.order}: ${chapter.title}", fontWeight = FontWeight.SemiBold) },
                    supportingContent = { Text("${chapter.content.take(40)}...", maxLines = 1, color = Color.Gray) },
                    trailingContent = {
                        Row {
                            IconButton(onClick = { editingChapter = chapter }) {
                                Icon(Icons.Rounded.EditNote, null, tint = WattpadOrange)
                            }
                            IconButton(onClick = { viewModel.deleteChapter(story.id, chapter.id) }) {
                                Icon(Icons.Rounded.DeleteOutline, null, tint = Color.Red)
                            }
                        }
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(0.3f))
            }
        }
    }

    // Dialog xử lý cả Thêm và Sửa
    if (editingChapter != null) {
        AddEditChapterDialog(
            chapter = editingChapter!!,
            onDismiss = { editingChapter = null },
            onConfirm = { updatedChapter ->
                viewModel.addOrUpdateChapter(story.id, updatedChapter)
                editingChapter = null
            }
        )
    }
}

@Composable
fun AddEditChapterDialog(chapter: Chapter, onDismiss: () -> Unit, onConfirm: (Chapter) -> Unit) {
    var title by remember { mutableStateOf(chapter.title) }
    var content by remember { mutableStateOf(chapter.content) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if(chapter.id.isEmpty()) "Viết chương mới" else "Chỉnh sửa chương") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tiêu đề chương") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Nội dung chương") },
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    minLines = 10
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(chapter.copy(title = title, content = content)) },
                colors = ButtonDefaults.buttonColors(WattpadOrange)
            ) {
                Text("Xuất bản")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}