// AddEventScreen.kt (sửa)
package com.example.matestudy.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.matestudy.ui.viewmodel.ScheduleViewModel
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    viewModel: ScheduleViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var tieuDe by remember { mutableStateOf("") }
    var diaDiem by remember { mutableStateOf("") }
    var lapLai by remember { mutableStateOf("khong") } // sẽ thay bằng DropdownMenu sau
    var gioBd by remember { mutableStateOf("") }
    var gioKt by remember { mutableStateOf("") }
    var ngayBd by remember { mutableStateOf("") }
    var ngayKt by remember { mutableStateOf("") }

    // TODO: Thay bằng Material3 DatePicker + TimePicker sau
    // Hiện tại dùng TextField tạm thời

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = tieuDe,
            onValueChange = { tieuDe = it },
            label = { Text("Tiêu đề *") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = diaDiem,
            onValueChange = { diaDiem = it },
            label = { Text("Địa điểm") },
            modifier = Modifier.fillMaxWidth()
        )

        // Dropdown lặp lại (ví dụ đơn giản)
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = when (lapLai) {
                    "khong" -> "Không lặp lại"
                    "hang_ngay" -> "Hàng ngày"
                    "hang_tuan" -> "Hàng tuần"
                    else -> "Không lặp lại"
                },
                onValueChange = {},
                readOnly = true,
                label = { Text("Lặp lại") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                listOf("khong" to "Không lặp lại", "hang_ngay" to "Hàng ngày", "hang_tuan" to "Hàng tuần").forEach { (value, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            lapLai = value
                            expanded = false
                        }
                    )
                }
            }
        }

        // Thời gian & ngày (tạm TextField, sau thay bằng picker)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = gioBd,
                onValueChange = { gioBd = it },
                label = { Text("Giờ bắt đầu (HH:mm)") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = gioKt,
                onValueChange = { gioKt = it },
                label = { Text("Giờ kết thúc (HH:mm)") },
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = ngayBd,
                onValueChange = { ngayBd = it },
                label = { Text("Ngày bắt đầu (yyyy-MM-dd)") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = ngayKt,
                onValueChange = { ngayKt = it },
                label = { Text("Ngày kết thúc (yyyy-MM-dd)") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("Hủy")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                coroutineScope.launch {
                    viewModel.addEvent(
                        tieuDe = tieuDe.trim(),
                        diaDiem = diaDiem.trim().ifEmpty { null },
                        thu = null,
                        lapLai = lapLai,
                        gioBd = gioBd.trim().ifEmpty { null },
                        gioKt = gioKt.trim().ifEmpty { null },
                        ngayBd = ngayBd.trim(),
                        ngayKt = ngayKt.trim()
                    )
                    onSuccess()
                }
            }) {
                Text("Lưu")
            }
        }
    }
}