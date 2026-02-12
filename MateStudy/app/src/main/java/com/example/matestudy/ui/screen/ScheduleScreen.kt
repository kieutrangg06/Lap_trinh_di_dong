package com.example.matestudy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matestudy.ui.viewmodel.ScheduleViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel,
    onNavigateToAddEvent: () -> Unit,
    onNavigateToChooseClass: () -> Unit
) {
    val currentMonth by viewModel.currentMonth.collectAsState()
    val events by viewModel.events.collectAsState()
    val selectedEvent by viewModel.selectedEvent.collectAsState()

    val coroutineScope = rememberCoroutineScope()  // Để gọi suspend function

    val daysOfWeek = listOf("CN", "Th 2", "Th 3", "Th 4", "Th 5", "Th 6", "Th 7")

    Box(modifier = Modifier.fillMaxSize()) {  // Dùng Box để align FAB dễ dàng

        Column(modifier = Modifier.fillMaxSize()) {
            // Header tháng
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.changeMonth(-1) }) {
                    Text("<", fontSize = 24.sp)
                }
                Text(
                    "${currentMonth.month} ${currentMonth.year}",
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = { viewModel.changeMonth(1) }) {
                    Text(">", fontSize = 24.sp)
                }
            }

            // Days of week
            Row(modifier = Modifier.fillMaxWidth()) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            // Calendar grid
            val firstOfMonth = currentMonth.withDayOfMonth(1)
            val firstDayOfWeek = (firstOfMonth.dayOfWeek.value % 7)  // Sunday = 0, Monday = 1,...
            val daysInMonth = currentMonth.lengthOfMonth()
            val totalCells = 42

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {
                items(totalCells) { index ->
                    val day = index - firstDayOfWeek + 1
                    val date = if (day in 1..daysInMonth) {
                        firstOfMonth.plusDays((day - 1).toLong())
                    } else null

                    val dayEvents = date?.let { d ->
                        events.filter { it.date == d }
                    } ?: emptyList()

                    Box(
                        modifier = Modifier
                            .height(100.dp)  // Giảm chiều cao để vừa màn hình
                            .padding(2.dp)
                            .background(if (date != null) Color(0xFFF5F5F5) else Color.Transparent)
                            .clickable { /* Có thể thêm chọn ngày nếu cần */ }
                    ) {
                        if (date != null) {
                            Text(
                                text = day.toString(),
                                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp),
                                fontSize = 12.sp
                            )

                            Column(
                                modifier = Modifier
                                    .padding(top = 20.dp, start = 4.dp, end = 4.dp)
                                    .fillMaxWidth()
                            ) {
                                dayEvents.take(3).forEach { event ->
                                    val bgColor = try {
                                        Color(android.graphics.Color.parseColor(event.color))
                                    } catch (e: Exception) {
                                        Color(0xFF3788D8)  // fallback nếu color sai format
                                    }

                                    Text(
                                        text = event.title.take(12) + if (event.title.length > 12) "..." else "",
                                        modifier = Modifier
                                            .background(bgColor, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                            .fillMaxWidth()
                                            .clickable { viewModel.selectEvent(event) },
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        maxLines = 1
                                    )
                                }
                                if (dayEvents.size > 3) {
                                    Text("...", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { /* Show bottom sheet chọn loại */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Thêm")
        }

        // BottomSheet chọn loại thêm
        var showAddOptions by remember { mutableStateOf(false) }
        if (showAddOptions) {
            ModalBottomSheet(onDismissRequest = { showAddOptions = false }) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Thêm mới",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    TextButton(
                        onClick = {
                            onNavigateToChooseClass()
                            showAddOptions = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Thêm lớp học")
                    }
                    TextButton(
                        onClick = {
                            onNavigateToAddEvent()
                            showAddOptions = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Thêm sự kiện cá nhân")
                    }
                }
            }
        }

        // Popup chi tiết sự kiện
        selectedEvent?.let { event ->
            AlertDialog(
                onDismissRequest = { viewModel.clearSelectedEvent() },
                title = { Text("Chi tiết sự kiện") },
                text = {
                    Column {
                        Text("Tiêu đề: ${event.title}")
                        if (event.teacher != null) Text("Giảng viên: ${event.teacher}")
                        Text("Địa điểm: ${event.location ?: "Không có"}")
                        Text(
                            "Thời gian: ${event.startTime ?: "--"} - ${event.endTime ?: "--"}"
                        )
                        Text("Ngày: ${event.date}")
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearSelectedEvent() }) {
                        Text("Đóng")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            viewModel.deleteEvent(event)
                            viewModel.clearSelectedEvent()
                        }
                    }) {
                        Text("Xóa", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    }
}