package com.example.matestudy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.matestudy.data.entity.ReviewEntity
import com.example.matestudy.ui.theme.*
import com.example.matestudy.ui.viewmodel.AdminReviewViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRatingScreen(viewModel: AdminReviewViewModel) {
    val reviews by viewModel.filteredReviews.collectAsState()
    val currentFilter by viewModel.filterStatus.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("QUẢN LÝ ĐÁNH GIÁ", fontWeight = FontWeight.Black, fontSize = 16.sp) },
                actions = {
                    Box {
                        TextButton(onClick = { expanded = true }) {
                            Text(currentFilter, color = PrimaryPink)
                            Icon(Icons.Default.FilterList, null, tint = PrimaryPink)
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            listOf("Tất cả", "Chờ duyệt", "Đã duyệt", "Bị ẩn").forEach {
                                DropdownMenuItem(text = { Text(it) }, onClick = {
                                    viewModel.setFilter(it)
                                    expanded = false
                                })
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Header Table
            item {
                Row(
                    Modifier.fillMaxWidth().background(Color(0xFFF5F5F5)).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("MÔN HỌC / NỘI DUNG", Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Text("SINH VIÊN", Modifier.weight(0.8f), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Text("TRẠNG THÁI", Modifier.weight(0.7f), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Text("HÀNH ĐỘNG", Modifier.weight(0.6f), fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }

            items(reviews) { review ->
                AdminReviewRow(
                    review = review,
                    onApprove = { viewModel.approveReview(review.ngay_dang) },
                    onHide = { viewModel.hideReview(review.ngay_dang) },
                    onDelete = { viewModel.deleteReview(review.ngay_dang) }
                )
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
            }
        }
    }
}

@Composable
fun AdminReviewRow(review: ReviewEntity, onApprove: () -> Unit, onHide: () -> Unit, onDelete: () -> Unit) {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    
    Row(
        Modifier.fillMaxWidth().padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cột Nội dung
        Column(Modifier.weight(1.5f)) {
            Text("ID Môn: ${review.mon_hoc_id}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(review.noi_dung, fontSize = 13.sp, color = Color.DarkGray, maxLines = 2)
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { index ->
                    Icon(
                        Icons.Default.Star, null, 
                        Modifier.size(12.dp), 
                        tint = if (index < (review.diem_sao ?: 0)) Color(0xFFFFC107) else Color.LightGray
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(sdf.format(Date(review.ngay_dang)), fontSize = 10.sp, color = Color.Gray)
            }
        }

        // Cột Sinh viên
        Text("sv${review.sinh_vien_id}", Modifier.weight(0.8f), fontSize = 12.sp)

        // Cột Trạng thái
        Box(Modifier.weight(0.7f)) {
            val (label, color) = when(review.trang_thai) {
                "da_duyet" -> "Đã duyệt" to Color(0xFF4CAF50)
                "cho_duyet" -> "Chờ duyệt" to Color(0xFFFF9800)
                else -> "Bị ẩn" to Color.Gray
            }
            Surface(color = color, shape = RoundedCornerShape(4.dp)) {
                Text(label, color = Color.White, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
            }
        }

        // Cột Hành động
        Column(Modifier.weight(0.6f), horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = if (review.trang_thai == "da_duyet") onHide else onApprove, Modifier.size(28.dp)) {
                Icon(
                    if (review.trang_thai == "da_duyet") Icons.Default.VisibilityOff else Icons.Default.CheckCircle,
                    null, tint = if (review.trang_thai == "da_duyet") Color(0xFFFFB300) else Color(0xFF4CAF50)
                )
            }
            IconButton(onClick = onDelete, Modifier.size(28.dp)) {
                Icon(Icons.Default.Delete, null, tint = Color(0xFFE53935))
            }
        }
    }
}