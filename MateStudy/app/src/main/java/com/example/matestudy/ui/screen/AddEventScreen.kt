// AddEventScreen.kt (sửa)
package com.example.matestudy.ui.screen

import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.matestudy.ui.viewmodel.ScheduleViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.example.matestudy.ui.theme.PrimaryPink
import com.example.matestudy.ui.theme.TextPrimary
import com.example.matestudy.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// AddEventScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(viewModel: ScheduleViewModel, onBack: () -> Unit, onSuccess: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var tieuDe by remember { mutableStateOf("") }
    var diaDiem by remember { mutableStateOf("") }
    var lapLai by remember { mutableStateOf("khong") }

    // State cho Date và Time
    var selectedNgayBd by remember { mutableStateOf(LocalDate.now()) }
    var selectedNgayKt by remember { mutableStateOf(LocalDate.now()) }
    var selectedGioBd by remember { mutableStateOf(LocalTime.of(8, 0)) }
    var selectedGioKt by remember { mutableStateOf(LocalTime.of(10, 0)) }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thêm sự kiện cá nhân", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.Close, null) } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
            Text("Chi tiết sự kiện", style = MaterialTheme.typography.titleMedium, color = PrimaryPink)

            OutlinedTextField(
                value = tieuDe, onValueChange = { tieuDe = it },
                label = { Text("Tiêu đề sự kiện") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Edit, null) }
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = diaDiem, onValueChange = { diaDiem = it },
                label = { Text("Địa điểm (Phòng học, Zoom...)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.LocationOn, null) }
            )

            Spacer(Modifier.height(24.dp))
            Text("Thời gian", style = MaterialTheme.typography.titleMedium, color = PrimaryPink)

            // Chọn Ngày
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DatePickerField("Từ ngày", selectedNgayBd, Modifier.weight(1f)) { selectedNgayBd = it }
                DatePickerField("Đến ngày", selectedNgayKt, Modifier.weight(1f)) { selectedNgayKt = it }
            }

            Spacer(Modifier.height(8.dp))

            // Chọn Giờ
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TimePickerField("Bắt đầu", selectedGioBd, Modifier.weight(1f)) { selectedGioBd = it }
                TimePickerField("Kết thúc", selectedGioKt, Modifier.weight(1f)) { selectedGioKt = it }
            }

            Spacer(Modifier.height(24.dp))
            Text("Lặp lại", style = MaterialTheme.typography.titleMedium, color = PrimaryPink)

            // Lựa chọn lặp lại
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                RepeatOption("Không", lapLai == "khong") { lapLai = "khong" }
                RepeatOption("Ngày", lapLai == "hang_ngay") { lapLai = "hang_ngay" }
                RepeatOption("Tuần", lapLai == "hang_tuan") { lapLai = "hang_tuan" }
            }

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        // Tính toán 'thu' dựa trên ngày bắt đầu
                        val thu = when(selectedNgayBd.dayOfWeek.value) {
                            7 -> "CN"
                            else -> (selectedNgayBd.dayOfWeek.value + 1).toString()
                        }

                        viewModel.addEvent(
                            tieuDe = tieuDe.trim(),
                            diaDiem = diaDiem.trim().ifEmpty { null },
                            thu = thu, // Gán thứ tự động
                            lapLai = lapLai,
                            gioBd = selectedGioBd.format(timeFormatter),
                            gioKt = selectedGioKt.format(timeFormatter),
                            ngayBd = selectedNgayBd.format(dateFormatter),
                            ngayKt = selectedNgayKt.format(dateFormatter)
                        )
                        onSuccess()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = tieuDe.isNotBlank()
            ) {
                Text("LƯU SỰ KIỆN", fontWeight = FontWeight.Bold)
            }
        }
    }
}

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
        enabled = false, // Vô hiệu hóa gõ phím để buộc click
        colors = OutlinedTextFieldDefaults.colors(disabledTextColor = TextPrimary, disabledBorderColor = MaterialTheme.colorScheme.outline, disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant)
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
        colors = OutlinedTextFieldDefaults.colors(disabledTextColor = TextPrimary, disabledBorderColor = MaterialTheme.colorScheme.outline)
    )
}

@Composable
fun RepeatOption(text: String, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(text) },
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryPink, selectedLabelColor = Color.White)
    )
}

@Composable
private fun AddEventDetailRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = TextSecondary
        )
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}