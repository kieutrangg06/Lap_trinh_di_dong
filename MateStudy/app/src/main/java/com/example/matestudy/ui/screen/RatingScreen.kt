package com.example.matestudy.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.matestudy.ui.viewmodel.ReviewViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.text.style.TextAlign
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingScreen(viewModel: ReviewViewModel) {
    val monHocList by viewModel.monHocList.collectAsState(initial = emptyList())
    val showAddDialog = remember { mutableStateOf(false) }
    val selectedMonHocId = remember { mutableStateOf<Long?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Đánh giá môn học và giảng viên", style = MaterialTheme.typography.titleLarge)

        // Nút thêm đánh giá mới
        Button(onClick = { showAddDialog.value = true }, modifier = Modifier.align(Alignment.End)) {
            Icon(Icons.Default.Add, contentDescription = null)
            Text("Thêm đánh giá mới")
        }

        // Danh sách môn học
        Text("Danh sách môn học", modifier = Modifier.padding(top = 16.dp))
        LazyColumn {
            items(monHocList) { monHoc ->
                val average by viewModel.getAverageRating(monHoc.id).collectAsState(initial = 0.0)
                val count by viewModel.getReviewCount(monHoc.id).collectAsState(initial = 0)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedMonHocId.value = monHoc.id },
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = monHoc.tenMon ?: "Không tên",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "GV: ${monHoc.tenGv ?: "Không có"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Row {
                                Icon(Icons.Default.Star, contentDescription = null)
                                Text(String.format("%.1f", average ?: 0.0))
                            }
                            Text("$count đánh giá", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }

        // Nếu chọn môn → hiển thị danh sách đánh giá chi tiết
        selectedMonHocId.value?.let { monId ->
            val reviews by viewModel.getReviewsByMonHoc(monId).collectAsState(initial = emptyList())
            Spacer(modifier = Modifier.height(16.dp))
            Text("Danh sách đánh giá", style = MaterialTheme.typography.titleMedium)

            if (reviews.isEmpty()) {
                Text(
                    "Chưa có đánh giá",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn {
                    items(reviews) { review ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Sinh viên ID: ${review.sinh_vien_id}") // sửa: sinh_vien_id
                                Row {
                                    repeat(review.diem_sao ?: 0) { // sửa: diem_sao
                                        Icon(Icons.Default.Star, contentDescription = null)
                                    }
                                }
                                Text(review.noi_dung) // sửa: noi_dung
                                Text(
                                    text = "Ngày: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(review.ngay_dang)}", // sửa: ngay_dang
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog thêm đánh giá mới
    if (showAddDialog.value) {
        AlertDialog(
            onDismissRequest = { showAddDialog.value = false },
            title = { Text("Thêm đánh giá mới") },
            text = {
                Column {
                    // Chọn môn học
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(
                            readOnly = true,
                            value = viewModel.selectedMonHoc.value?.tenMon ?: "Chọn môn học",
                            onValueChange = {},
                            label = { Text("Môn học") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            monHocList.forEach { monHoc ->
                                DropdownMenuItem(
                                    text = { Text(monHoc.tenMon ?: "") },
                                    onClick = {
                                        viewModel.setSelectedMonHoc(monHoc)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Đánh giá sao
                    Row(modifier = Modifier.padding(top = 8.dp)) {
                        (1..5).forEach { star ->
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = if (star <= viewModel.rating.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.clickable { viewModel.setRating(star) }
                            )
                        }
                    }

                    // Nội dung
                    TextField(
                        value = viewModel.content.value,
                        onValueChange = { viewModel.setContent(it) },
                        label = { Text("Nội dung") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )

                    viewModel.error.value?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.submitReview { showAddDialog.value = false }
                }) {
                    Text("Gửi đánh giá")
                }
            },
            dismissButton = {
                Button(onClick = { showAddDialog.value = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}