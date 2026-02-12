// ChooseClassScreen.kt (sửa)
package com.example.matestudy.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.matestudy.ui.viewmodel.ScheduleViewModel
import androidx.compose.ui.Alignment

@Composable
fun ChooseClassScreen(
    viewModel: ScheduleViewModel,
    onBack: () -> Unit,
    onSelect: (Long) -> Unit
) {
    // TODO: Lấy hoc_ky_id hiện tại động (từ ViewModel hoặc SharedPreferences)
    // Hiện tại hardcode 1 để test, sau thay bằng logic thật
    val hocKyId = 1L // Thay bằng giá trị động

    // Lấy danh sách môn học từ repository qua flow
    val monHocList by viewModel.scheduleRepository
        .getMonHocByHocKy(hocKyId)
        .collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Chọn lớp học",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (monHocList.isEmpty()) {
            Text("Không có lớp học nào trong học kỳ này", modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn {
                items(monHocList) { mon ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onSelect(mon.id) },
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = mon.ten_mon,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "GV: ${mon.ten_gv}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${mon.gio_bat_dau ?: "--"} - ${mon.gio_ket_thuc ?: "--"}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = mon.dia_diem ?: "Chưa có địa điểm",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onBack,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Hủy")
        }
    }
}