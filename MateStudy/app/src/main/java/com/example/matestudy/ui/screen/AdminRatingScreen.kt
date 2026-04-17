package com.example.matestudy.ui.screen

import androidx.compose.animation.AnimatedVisibility
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
import com.example.matestudy.data.entity.ReviewWithUser
import com.example.matestudy.ui.theme.*
import com.example.matestudy.ui.viewmodel.AdminReviewViewModel
import com.example.matestudy.ui.viewmodel.MonHocWithReviews
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRatingScreen(viewModel: AdminReviewViewModel) {
    val monHocGroups by viewModel.filteredMonHocWithReviews.collectAsState()
    val currentFilter by viewModel.filterStatus.collectAsState()
    var expandedFilter by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("QUẢN LÝ ĐÁNH GIÁ", fontWeight = FontWeight.Black, fontSize = 16.sp) },
                actions = {
                    Box {
                        TextButton(onClick = { expandedFilter = true }) {
                            Text(currentFilter, color = PrimaryPink)
                            Icon(Icons.Default.FilterList, null, tint = PrimaryPink)
                        }
                        DropdownMenu(
                            expanded = expandedFilter,
                            onDismissRequest = { expandedFilter = false }
                        ) {
                            listOf("Tất cả", "Chờ duyệt", "Đã duyệt", "Bị ẩn").forEach {
                                DropdownMenuItem(
                                    text = { Text(it) },
                                    onClick = {
                                        viewModel.setFilter(it)
                                        expandedFilter = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // ────────────────────────────────────────────────
            // 1. THANH TÌM KIẾM
            // ────────────────────────────────────────────────
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.setSearchQuery(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                placeholder = { Text("Tìm theo tên môn học...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // ────────────────────────────────────────────────
            // 2. DANH SÁCH NHÓM ĐÁNH GIÁ THEO MÔN
            // ────────────────────────────────────────────────
            if (monHocGroups.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Không có đánh giá nào khớp với bộ lọc.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(monHocGroups) { group ->
                        MonHocReviewCard(group, viewModel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonHocReviewCard(
    group: MonHocWithReviews,
    viewModel: AdminReviewViewModel
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = { expanded = !expanded }
    ) {
        Column {
            // ────────────────────────────────────────────────
            // 3. HEADER MÔN HỌC (TÓM TẮT ĐIỂM SỐ)
            // ────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = group.tenMonHoc,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "ID: ${group.monHocId}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (group.averageRating != null) "%.1f".format(group.averageRating) else "—",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryPink
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
                    }
                    Text(
                        text = "${group.reviewCount} đánh giá",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Icon(
                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // ────────────────────────────────────────────────
            // 4. DANH SÁCH CHI TIẾT ĐÁNH GIÁ (KHI MỞ RỘNG)
            // ────────────────────────────────────────────────
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    if (group.reviews.isEmpty()) {
                        Text(
                            text = "Không có đánh giá nào.",
                            modifier = Modifier.padding(16.dp),
                            color = Color.Gray
                        )
                    } else {
                        group.reviews.forEach { reviewWithUser ->
                            ReviewDetailCard(
                                reviewWithUser = reviewWithUser,
                                onApprove = { viewModel.approveReview(reviewWithUser.review.ngay_dang) },
                                onHide = { viewModel.hideReview(reviewWithUser.review.ngay_dang) },
                                onDelete = { viewModel.deleteReview(reviewWithUser.review.ngay_dang) }
                            )
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewDetailCard(
    reviewWithUser: ReviewWithUser,
    onApprove: () -> Unit,
    onHide: () -> Unit,
    onDelete: () -> Unit
) {
    val review = reviewWithUser.review
    val tenDangNhap = reviewWithUser.tenDangNhap
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ────────────────────────────────────────────────
            // 5. THÔNG TIN SINH VIÊN & NỘI DUNG ĐÁNH GIÁ
            // ────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sinh viên: @$tenDangNhap",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                Text(
                    text = sdf.format(Date(review.ngay_dang)),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { index ->
                    Icon(
                        Icons.Default.Star,
                        null,
                        modifier = Modifier.size(18.dp),
                        tint = if (index < (review.diem_sao ?: 0)) Color(0xFFFFC107) else Color.LightGray
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${review.diem_sao ?: 0}/5",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = review.noi_dung.ifBlank { "(Không có nội dung)" },
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(12.dp))

            // ────────────────────────────────────────────────
            // 6. TRẠNG THÁI & CÁC NÚT THAO TÁC
            // ────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val (label, color) = when (review.trang_thai) {
                    "da_duyet" -> "Đã duyệt" to Color(0xFF4CAF50)
                    "cho_duyet" -> "Chờ duyệt" to Color(0xFFFF9800)
                    else -> "Bị ẩn" to Color.Gray
                }

                Surface(
                    color = color,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = label,
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Row {
                    IconButton(onClick = if (review.trang_thai == "da_duyet") onHide else onApprove) {
                        Icon(
                            if (review.trang_thai == "da_duyet") Icons.Default.VisibilityOff else Icons.Default.CheckCircle,
                            null,
                            tint = if (review.trang_thai == "da_duyet") Color(0xFFFFB300) else Color(0xFF4CAF50)
                        )
                    }

                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, null, tint = Color(0xFFE53935))
                    }
                }
            }
        }
    }
}