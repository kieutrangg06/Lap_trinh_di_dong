package com.example.matestudy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.matestudy.ui.theme.BackgroundGradientEnd
import com.example.matestudy.ui.theme.ErrorRed
import com.example.matestudy.ui.theme.PrimaryPink
import com.example.matestudy.ui.theme.TextPrimary
import com.example.matestudy.ui.theme.TextSecondary
import com.example.matestudy.ui.viewmodel.ScheduleViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel,
    navController: NavHostController
) {
    val currentMonth by viewModel.currentMonth.collectAsState()
    val events by viewModel.events.collectAsState()
    val selectedEvent by viewModel.selectedEvent.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var showAddOptions by remember { mutableStateOf(false) }

    val daysOfWeek = listOf("CN", "T2", "T3", "T4", "T5", "T6", "T7")

    Scaffold(
        containerColor = BackgroundGradientEnd,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddOptions = true },
                containerColor = PrimaryPink,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, "Thêm")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Header Tháng hiện đại
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.changeMonth(-1) }) {
                        Icon(Icons.Default.ChevronLeft, null, tint = PrimaryPink)
                    }
                    Text(
                        text = "${currentMonth.month.getDisplayName(java.time.format.TextStyle.FULL,
                            Locale("vi")
                        )} ${currentMonth.year}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = { viewModel.changeMonth(1) }) {
                        Icon(Icons.Default.ChevronRight, null, tint = PrimaryPink)
                    }
                }
            }

            // Thứ trong tuần
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (day == "CN") ErrorRed else TextSecondary
                    )
                }
            }

            // ... (Các phần khai báo phía trên giữ nguyên)

// Grid Lịch
            val firstOfMonth = currentMonth.withDayOfMonth(1)
            val firstDayOfWeek = (firstOfMonth.dayOfWeek.value % 7) // 0 cho CN, 1 cho T2...
            val daysInMonth = currentMonth.lengthOfMonth()

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)
            ) {
                items(42) { index ->
                    val day = index - firstDayOfWeek + 1
                    val date = if (day in 1..daysInMonth) firstOfMonth.plusDays((day - 1).toLong()) else null
                    val isToday = date == LocalDate.now()

                    // ScheduleScreen.kt

// ... (Phần code cũ)

                    Box(
                        modifier = Modifier
                            .aspectRatio(0.7f) // Tăng thêm chiều cao để chứa được khoảng 3-4 sự kiện
                            .padding(1.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isToday) Color(0xFFFFEBF2) else if (date != null) Color.White else Color.Transparent)
                            .then(if (date != null) Modifier.clickable { /* logic */ } else Modifier)
                    ) {
                        if (date != null) {
                            Text(
                                text = day.toString(),
                                modifier = Modifier.align(Alignment.TopCenter).padding(2.dp),
                                fontSize = 11.sp,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                color = if (isToday) PrimaryPink else TextPrimary
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 18.dp), // Đẩy xuống dưới số ngày
                                verticalArrangement = Arrangement.spacedBy(1.dp)
                            ) {
                                events.filter { it.date == date }
                                    .take(3) // Giới hạn hiển thị 3 sự kiện để không bị tràn ô
                                    .forEach { event ->
                                        val eventColor = try {
                                            Color(android.graphics.Color.parseColor(event.color))
                                        } catch (e: Exception) {
                                            PrimaryPink
                                        }

                                        // Thanh sự kiện kiểu Google Calendar
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(16.dp)
                                                .background(eventColor.copy(alpha = 0.2f)) // Nền cực nhạt
                                                .clickable { viewModel.selectEvent(event) }
                                                .padding(horizontal = 2.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                // Vạch màu đậm bên trái
                                                Box(
                                                    modifier = Modifier
                                                        .width(2.5.dp)
                                                        .fillMaxHeight()
                                                        .background(eventColor)
                                                )
                                                Spacer(Modifier.width(2.dp))
                                                Text(
                                                    text = event.title,
                                                    fontSize = 8.sp,
                                                    maxLines = 1,
                                                    color = eventColor, // Chữ cùng màu với vạch đậm
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }
                                    }

                                // Nếu nhiều hơn 3 sự kiện thì hiện dấu ba chấm
                                if (events.count { it.date == date } > 3) {
                                    Text(
                                        "...",
                                        fontSize = 8.sp,
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet Options
    if (showAddOptions) {
        ModalBottomSheet(onDismissRequest = { showAddOptions = false }, containerColor = Color.White) {
            Column(modifier = Modifier.padding(24.dp).padding(bottom = 32.dp)) {
                Text("Thêm vào lịch", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(16.dp))
                OptionItem(Icons.Default.School, "Thêm lớp học", "Đăng ký môn học vào thời khóa biểu") {
                    navController.navigate("choose_class"); showAddOptions = false
                }
                OptionItem(Icons.Default.Event, "Sự kiện cá nhân", "Ghi chú lịch thi, họp nhóm, deadline...") {
                    navController.navigate("add_personal_event"); showAddOptions = false
                }
            }
        }
    }

    // Dialog Chi tiết
    // Trong ScheduleScreen.kt, phần Dialog Chi tiết
    selectedEvent?.let { event ->
        AlertDialog(
            onDismissRequest = { viewModel.clearSelectedEvent() },
            title = { Text(event.title, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (event.teacher != null) DetailRow(Icons.Default.Person, event.teacher!!)
                    DetailRow(Icons.Default.LocationOn, event.location ?: "Trực tuyến")
                    DetailRow(Icons.Default.AccessTime, "${event.startTime} - ${event.endTime}")
                    DetailRow(Icons.Default.CalendarToday, event.date.toString())
                }
            },
            confirmButton = {
                // Nút Sửa mới
                Button(onClick = {
                    val eventJson = /* Bạn có thể truyền qua Bundle hoặc đơn giản là lưu vào biến tạm ở ViewModel */
                        viewModel.clearSelectedEvent()
                    navController.navigate("edit_event")
                }) {
                    Text("Sửa")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        viewModel.deleteEvent(event)
                        viewModel.clearSelectedEvent()
                    }
                }) {
                    Text("Xóa", color = ErrorRed)
                }
            }
        )
    }
}

@Composable
fun DetailRow(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Icon(icon, null, tint = PrimaryPink, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
    }
}

@Composable
fun OptionItem(icon: ImageVector, title: String, desc: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        color = BackgroundGradientEnd
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = PrimaryPink, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                Text(desc, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}