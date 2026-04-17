package com.example.matestudy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.matestudy.ui.viewmodel.ScheduleViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.matestudy.data.Event
import com.example.matestudy.ui.theme.PrimaryPink
import com.example.matestudy.ui.theme.TextPrimary
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    viewModel: ScheduleViewModel,
    eventToEdit: Event? = null,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var tieuDe by remember { mutableStateOf(eventToEdit?.title ?: "") }
    var diaDiem by remember { mutableStateOf(eventToEdit?.location ?: "") }
    var lapLai by remember { mutableStateOf(eventToEdit?.repeat ?: "khong") }
    var selectedColor by remember { mutableStateOf(eventToEdit?.color ?: "#3788d8") }

    var selectedNgayBd by remember { mutableStateOf(eventToEdit?.date ?: LocalDate.now()) }
    var selectedNgayKt by remember { mutableStateOf(eventToEdit?.date ?: LocalDate.now()) }
    var selectedGioBd by remember { mutableStateOf(eventToEdit?.startTime ?: LocalTime.of(8, 0)) }
    var selectedGioKt by remember { mutableStateOf(eventToEdit?.endTime ?: LocalTime.of(10, 0)) }

    val colors = listOf("#3788d8", "#e67c73", "#f4511e", "#f6bf26", "#33b679", "#0b8043", "#8e24aa")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (eventToEdit == null) "Thêm sự kiện" else "Sửa sự kiện", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.Close, null) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // ────────────────────────────────────────────────
            // 1. THÔNG TIN CHI TIẾT
            // ────────────────────────────────────────────────
            Text("Chi tiết sự kiện", style = MaterialTheme.typography.titleMedium, color = PrimaryPink)

            OutlinedTextField(
                value = tieuDe, onValueChange = { tieuDe = it },
                label = { Text("Tiêu đề sự kiện") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                leadingIcon = { Icon(Icons.Default.Edit, null) }
            )

            OutlinedTextField(
                value = diaDiem, onValueChange = { diaDiem = it },
                label = { Text("Địa điểm") },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                leadingIcon = { Icon(Icons.Default.LocationOn, null) }
            )

            // ────────────────────────────────────────────────
            // 2. CHỌN MÀU SẮC
            // ────────────────────────────────────────────────
            Spacer(Modifier.height(20.dp))
            Text("Màu sắc hiển thị", style = MaterialTheme.typography.titleMedium, color = PrimaryPink)
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                colors.forEach { colorStr ->
                    val colorObj = Color(android.graphics.Color.parseColor(colorStr))
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(colorObj)
                            .clickable { selectedColor = colorStr }
                            .then(
                                if (selectedColor == colorStr)
                                    Modifier.border(2.dp, Color.Black, CircleShape)
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedColor == colorStr) {
                            Icon(Icons.Default.Add, null, tint = Color.White)
                        }
                    }
                }
            }

            // ────────────────────────────────────────────────
            // 3. THỜI GIAN
            // ────────────────────────────────────────────────
            Spacer(Modifier.height(16.dp))
            Text("Thời gian", style = MaterialTheme.typography.titleMedium, color = PrimaryPink)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                DatePickerField("Từ ngày", selectedNgayBd, Modifier.weight(1f)) { selectedNgayBd = it }
                DatePickerField("Đến ngày", selectedNgayKt, Modifier.weight(1f)) { selectedNgayKt = it }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                TimePickerField("Bắt đầu", selectedGioBd, Modifier.weight(1f)) { selectedGioBd = it }
                TimePickerField("Kết thúc", selectedGioKt, Modifier.weight(1f)) { selectedGioKt = it }
            }

            // ────────────────────────────────────────────────
            // 4. CHẾ ĐỘ LẶP LẠI
            // ────────────────────────────────────────────────
            Spacer(Modifier.height(24.dp))
            Text("Lặp lại", style = MaterialTheme.typography.titleMedium, color = PrimaryPink)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RepeatOption("Không", lapLai == "khong") { lapLai = "khong" }
                RepeatOption("Hàng ngày", lapLai == "hang_ngay") { lapLai = "hang_ngay" }
                RepeatOption("Hàng tuần", lapLai == "hang_tuan") { lapLai = "hang_tuan" }
            }

            // ────────────────────────────────────────────────
            // 5. NÚT XỬ LÝ (LƯU/CẬP NHẬT)
            // ────────────────────────────────────────────────
            Spacer(Modifier.height(40.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        val thu = if(selectedNgayBd.dayOfWeek.value == 7) "CN" else (selectedNgayBd.dayOfWeek.value + 1).toString()

                        if (eventToEdit == null) {
                            viewModel.addEvent(
                                tieuDe, diaDiem.ifEmpty { null }, thu, lapLai,
                                selectedGioBd.format(timeFormatter), selectedGioKt.format(timeFormatter),
                                selectedNgayBd.format(dateFormatter), selectedNgayKt.format(dateFormatter), selectedColor
                            )
                        } else {
                            viewModel.updateEvent(
                                eventToEdit.id, eventToEdit.originalId, tieuDe, diaDiem.ifEmpty { null }, thu, lapLai,
                                selectedGioBd.format(timeFormatter), selectedGioKt.format(timeFormatter),
                                selectedNgayBd.format(dateFormatter), selectedNgayKt.format(dateFormatter), selectedColor
                            )
                        }
                        onSuccess()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = tieuDe.isNotBlank()
            ) {
                Text(
                    text = if (eventToEdit == null) "LƯU SỰ KIỆN" else "CẬP NHẬT THAY ĐỔI",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

// ────────────────────────────────────────────────
// 6. COMPONENTS HỖ TRỢ
// ────────────────────────────────────────────────

@Composable
fun DatePickerField(label: String, date: LocalDate, modifier: Modifier, onDateSelected: (LocalDate) -> Unit) {
    val context = LocalContext.current
    OutlinedTextField(
        value = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        modifier = modifier.clickable {
            val picker = android.app.DatePickerDialog(context, { _, y, m, d ->
                onDateSelected(LocalDate.of(y, m + 1, d))
            }, date.year, date.monthValue - 1, date.dayOfMonth)
            picker.show()
        },
        enabled = false,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = TextPrimary,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Composable
fun TimePickerField(label: String, time: LocalTime, modifier: Modifier, onTimeSelected: (LocalTime) -> Unit) {
    val context = LocalContext.current
    OutlinedTextField(
        value = time.format(DateTimeFormatter.ofPattern("HH:mm")),
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        modifier = modifier.clickable {
            android.app.TimePickerDialog(context, { _, h, m ->
                onTimeSelected(LocalTime.of(h, m))
            }, time.hour, time.minute, true).show()
        },
        enabled = false,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = TextPrimary,
            disabledBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
fun RepeatOption(text: String, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(text) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = PrimaryPink,
            selectedLabelColor = Color.White
        )
    )
}