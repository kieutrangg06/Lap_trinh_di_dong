package com.example.matestudy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.times
import androidx.navigation.NavHostController
import com.example.matestudy.data.Event
import com.example.matestudy.ui.theme.*
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
    var viewMode by remember { mutableStateOf("month") } // month | week

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
                Icon(Icons.Default.Add, contentDescription = "Thêm")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            MonthHeader(
                currentMonth = currentMonth,
                onPreviousMonth = {
                    if (viewMode == "month") viewModel.changeMonth(-1)
                    else viewModel.changeWeek(-1)
                },
                onNextMonth = {
                    if (viewMode == "month") viewModel.changeMonth(1)
                    else viewModel.changeWeek(1)
                }
            )

            ViewModeSelector(
                selected = viewMode,
                onSelect = { viewMode = it }
            )

            WeekDaysRow(daysOfWeek)

            if (viewMode == "month") {
                CalendarGrid(
                    currentMonth = currentMonth,
                    events = events,
                    onSelectEvent = { viewModel.selectEvent(it) }
                )
            } else {
                WeeklyCalendarGrid(
                    currentDate = currentMonth,
                    events = events,
                    onSelectEvent = { viewModel.selectEvent(it) }
                )
            }
        }
    }

    AddOptionsBottomSheet(
        show = showAddOptions,
        onDismiss = { showAddOptions = false },
        onChooseClass = {
            navController.navigate("choose_class")
            showAddOptions = false
        },
        onChoosePersonalEvent = {
            navController.navigate("add_personal_event")
            showAddOptions = false
        }
    )

    EventDetailDialog(
        event = selectedEvent,
        onDismiss = { viewModel.clearSelectedEvent() },
        onEdit = {
            selectedEvent?.let {
                viewModel.setEventToEdit(it) // lưu event cần sửa
                navController.navigate("edit_event")
            }
        },
        onDelete = {
            coroutineScope.launch {
                selectedEvent?.let { viewModel.deleteEvent(it) }
                viewModel.clearSelectedEvent()
            }
        }
    )
}

@Composable
private fun ViewModeSelector(
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        listOf("month" to "Tháng", "week" to "Tuần").forEach { (mode, label) ->
            Surface(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clickable { onSelect(mode) },
                shape = RoundedCornerShape(20.dp),
                color = if (selected == mode) PrimaryPink else Color.White,
                shadowElevation = 2.dp
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    color = if (selected == mode) Color.White else PrimaryPink,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun WeeklyCalendarGrid(
    currentDate: LocalDate,
    events: List<Event>,
    onSelectEvent: (Event) -> Unit
) {
    val startOfWeek = currentDate.minusDays(currentDate.dayOfWeek.value % 7L)

    val startHour = 0
    val endHour = 23

    val hourHeight = 80.dp
    val dayWidth = 100.dp
    val headerHeight = 40.dp

    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(horizontalScroll)
    ) {

        // Cột timeline giờ
        Column(
            modifier = Modifier
                .width(50.dp)
                .verticalScroll(verticalScroll)
        ) {
            Spacer(modifier = Modifier.height(headerHeight))

            for (hour in startHour..endHour) {
                Box(
                    modifier = Modifier.height(hourHeight),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = String.format("%02dh", hour),
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // 7 ngày
        Row(
            modifier = Modifier
                .verticalScroll(verticalScroll)
        ) {
            repeat(7) { index ->
                val date = startOfWeek.plusDays(index.toLong())
                val isToday = date == LocalDate.now()

                Box(
                    modifier = Modifier
                        .width(dayWidth)
                        .height(headerHeight + hourHeight * 24)
                        .background(
                            if (isToday) Color(0x12FF69B4)
                            else Color.White
                        )
                ) {

                    // header ngày
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(headerHeight),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = when (index) {
                                        0 -> "CN"
                                        1 -> "T2"
                                        2 -> "T3"
                                        3 -> "T4"
                                        4 -> "T5"
                                        5 -> "T6"
                                        else -> "T7"
                                    },
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )

                                Text(
                                    text = date.dayOfMonth.toString(),
                                    fontWeight = FontWeight.Bold,
                                    color = if (isToday) PrimaryPink else TextPrimary
                                )
                            }
                        }

                        // grid line từng giờ
                        for (hour in startHour..endHour) {
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = Color.LightGray.copy(alpha = 0.4f)
                            )
                            Spacer(modifier = Modifier.height(hourHeight))
                        }
                    }

                    // events
                    events.filter { it.date == date }
                        .forEach { event ->

                            val start = event.startTime ?: return@forEach
                            val end = event.endTime ?: return@forEach

                            val startMinutes = start.hour * 60 + start.minute
                            val endMinutes = end.hour * 60 + end.minute
                            val duration = endMinutes - startMinutes

                            val topOffset =
                                headerHeight + (startMinutes / 60f) * hourHeight

                            val eventHeight =
                                (duration / 60f) * hourHeight

                            val eventColor = try {
                                Color(android.graphics.Color.parseColor(event.color))
                            } catch (e: Exception) {
                                PrimaryPink
                            }

                            Box(
                                modifier = Modifier
                                    .offset(y = topOffset)
                                    .padding(horizontal = 4.dp)
                                    .fillMaxWidth()
                                    .height(eventHeight)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(eventColor.copy(alpha = 0.9f))
                                    .clickable { onSelectEvent(event) }
                                    .padding(6.dp)
                            ) {
                                Column {
                                    Text(
                                        text = event.title,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        maxLines = 2
                                    )

                                    Text(
                                        text = "${start} - ${end}",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 9.sp
                                    )

                                    event.location?.let {
                                        Text(
                                            text = it,
                                            color = Color.White.copy(alpha = 0.85f),
                                            fontSize = 9.sp,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                }
            }
        }
    }
}
// ────────────────────────────────────────────────
// 1. COMPONENT HEADER & WEEKDAYS
// ────────────────────────────────────────────────

@Composable
private fun MonthHeader(
    currentMonth: LocalDate,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(Icons.Default.ChevronLeft, null, tint = PrimaryPink)
            }

            Text(
                text = "${currentMonth.month.getDisplayName(java.time.format.TextStyle.FULL, Locale("vi"))} ${currentMonth.year}",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            IconButton(onClick = onNextMonth) {
                Icon(Icons.Default.ChevronRight, null, tint = PrimaryPink)
            }
        }
    }
}

@Composable
private fun WeekDaysRow(daysOfWeek: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
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
}

// ────────────────────────────────────────────────
// 2. COMPONENT CALENDAR GRID
// ────────────────────────────────────────────────

@Composable
private fun CalendarGrid(
    currentMonth: LocalDate,
    events: List<com.example.matestudy.data.Event>,
    onSelectEvent: (com.example.matestudy.data.Event) -> Unit
) {
    val firstOfMonth = currentMonth.withDayOfMonth(1)
    val firstDayOfWeek = (firstOfMonth.dayOfWeek.value % 7)
    val daysInMonth = currentMonth.lengthOfMonth()

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        items(42) { index ->
            val day = index - firstDayOfWeek + 1
            val date = if (day in 1..daysInMonth) firstOfMonth.plusDays((day - 1).toLong()) else null
            val isToday = date == LocalDate.now()

            Box(
                modifier = Modifier
                    .aspectRatio(0.7f)
                    .padding(1.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        when {
                            isToday -> Color(0xFFFFEBF2)
                            date != null -> Color.White
                            else -> Color.Transparent
                        }
                    )
                    .then(if (date != null) Modifier.clickable { } else Modifier)
            ) {
                if (date != null) {
                    Text(
                        text = day.toString(),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(2.dp),
                        fontSize = 11.sp,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                        color = if (isToday) PrimaryPink else TextPrimary
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        events.filter { it.date == date }
                            .take(3)
                            .forEach { event ->
                                val eventColor = try {
                                    Color(android.graphics.Color.parseColor(event.color))
                                } catch (e: Exception) {
                                    PrimaryPink
                                }

                                EventItem(
                                    event = event,
                                    color = eventColor,
                                    onClick = { onSelectEvent(event) }
                                )
                            }

                        if (events.count { it.date == date } > 3) {
                            Text(
                                text = "...",
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

@Composable
private fun EventItem(
    event: com.example.matestudy.data.Event,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(16.dp)
            .background(color.copy(alpha = 0.2f))
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(2.5.dp)
                    .fillMaxHeight()
                    .background(color)
            )
            Spacer(Modifier.width(2.dp))
            Text(
                text = event.title,
                fontSize = 8.sp,
                maxLines = 1,
                color = color,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ────────────────────────────────────────────────
// 3. DIALOGS & BOTTOM SHEETS
// ────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddOptionsBottomSheet(
    show: Boolean,
    onDismiss: () -> Unit,
    onChooseClass: () -> Unit,
    onChoosePersonalEvent: () -> Unit
) {
    if (show) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    "Thêm vào lịch",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(16.dp))

                OptionItem(
                    icon = Icons.Default.School,
                    title = "Thêm lớp học",
                    desc = "Đăng ký môn học vào thời khóa biểu",
                    onClick = onChooseClass
                )

                OptionItem(
                    icon = Icons.Default.Event,
                    title = "Sự kiện cá nhân",
                    desc = "Ghi chú lịch thi, họp nhóm, deadline...",
                    onClick = onChoosePersonalEvent
                )
            }
        }
    }
}

@Composable
private fun EventDetailDialog(
    event: com.example.matestudy.data.Event?,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    event?.let {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(it.title, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (it.teacher != null) DetailRow(Icons.Default.Person, it.teacher!!)
                    DetailRow(Icons.Default.LocationOn, it.location ?: "Trực tuyến")
                    DetailRow(Icons.Default.AccessTime, "${it.startTime} - ${it.endTime}")
                    DetailRow(Icons.Default.CalendarToday, it.date.toString())
                }
            },
            confirmButton = {
                if (it.loai == "su_kien_rieng") {
                    Button(onClick = onEdit) { Text("Sửa") }
                }
            },
            dismissButton = {
                TextButton(onClick = onDelete) { Text("Xóa", color = Color.Red) }
            }
        )
    }
}

// ────────────────────────────────────────────────
// 4. CÁC COMPONENT HỖ TRỢ KHÁC
// ────────────────────────────────────────────────

@Composable
private fun DetailRow(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Icon(icon, null, tint = PrimaryPink, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
    }
}

@Composable
private fun OptionItem(
    icon: ImageVector,
    title: String,
    desc: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        color = BackgroundGradientEnd
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = PrimaryPink, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                Text(desc, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}