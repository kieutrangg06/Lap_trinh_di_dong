package com.example.matestudy.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matestudy.ui.viewmodel.ReviewViewModel
import com.example.matestudy.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingScreen(viewModel: ReviewViewModel) {
    // SỬA LỖI: Sử dụng collectAsState cho TẤT CẢ các luồng dữ liệu từ ViewModel
    val monHocList by viewModel.monHocList.collectAsState(initial = emptyList())
    val selectedMonHoc by viewModel.selectedMonHoc.collectAsState()
    val currentRating by viewModel.rating.collectAsState()
    val currentContent by viewModel.content.collectAsState()
    val errorMsg by viewModel.error.collectAsState()

    val showAddDialog = remember { mutableStateOf(false) }
    val selectedMonHocId = remember { mutableStateOf<Long?>(null) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog.value = true },
                containerColor = PrimaryPink,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Viết đánh giá")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Đánh giá môn học",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
                )
                Text(
                    "Chia sẻ trải nghiệm về giảng viên & môn học",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(8.dp))
            }

            items(monHocList) { monHoc ->
                val average by viewModel.getAverageRating(monHoc.id).collectAsState(initial = 0.0)
                val count by viewModel.getReviewCount(monHoc.id).collectAsState(initial = 0)

                Card(
                    onClick = {
                        selectedMonHocId.value = if (selectedMonHocId.value == monHoc.id) null else monHoc.id
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(Modifier.size(40.dp), CircleShape, PrimaryLight) {
                                Icon(Icons.Default.Book, null, Modifier.padding(10.dp), PrimaryPink)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(monHoc.tenMon ?: "Môn học", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("GV: ${monHoc.tenGv ?: "N/A"}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, null, Modifier.size(18.dp), Color(0xFFFFB100))
                                    Text(String.format(" %.1f", average), fontWeight = FontWeight.Black, fontSize = 16.sp)
                                }
                                Text("$count lượt", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                            }
                        }

                        if (selectedMonHocId.value == monHoc.id) {
                            val reviews by viewModel.getReviewsByMonHoc(monHoc.id).collectAsState(initial = emptyList())
                            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))

                            if (reviews.isEmpty()) {
                                Text(
                                    "Chưa có nhận xét chi tiết",
                                    modifier = Modifier.padding(8.dp),
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            } else {
                                reviews.forEach { review ->
                                    Column(Modifier.padding(bottom = 12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            repeat(5) { i ->
                                                Icon(
                                                    Icons.Default.Star,
                                                    null,
                                                    Modifier.size(14.dp),
                                                    if (i < (review.diem_sao ?: 0)) Color(0xFFFFB100) else Color.LightGray
                                                )
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            Text("Sinh viên ẩn danh", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                        }
                                        Text(review.noi_dung, modifier = Modifier.padding(top = 4.dp), style = MaterialTheme.typography.bodyMedium)
                                        Text(
                                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(review.ngay_dang),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.LightGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    // Dialog thêm đánh giá mới
    if (showAddDialog.value) {
        AlertDialog(
            onDismissRequest = { showAddDialog.value = false },
            shape = MaterialTheme.shapes.large,
            containerColor = Color.White,
            title = { Text("Đánh giá mới", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Môn học", style = MaterialTheme.typography.labelMedium)

                    var expanded by remember { mutableStateOf(false) }

                    Box(modifier = Modifier.padding(vertical = 8.dp)) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = true },
                            border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedMonHoc?.tenMon ?: "Nhấn để chọn môn...",
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(Icons.Default.ArrowDropDown, null)
                            }
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.7f)
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

                    Text("Mức độ hài lòng", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        (1..5).forEach { star ->
                            IconButton(onClick = { viewModel.setRating(star) }) {
                                Icon(
                                    Icons.Default.Star,
                                    null,
                                    tint = if (star <= currentRating) Color(0xFFFFB100) else Color.LightGray,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = currentContent,
                        onValueChange = { viewModel.setContent(it) },
                        label = { Text("Cảm nhận của bạn...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = MaterialTheme.shapes.medium
                    )

                    errorMsg?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.submitReview { showAddDialog.value = false } },
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink)
                ) {
                    Text("Gửi đánh giá")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog.value = false }) {
                    Text("Hủy", color = TextSecondary)
                }
            }
        )
    }
}