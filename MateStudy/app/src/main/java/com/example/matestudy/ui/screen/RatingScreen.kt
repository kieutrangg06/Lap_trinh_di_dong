package com.example.matestudy.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.matestudy.data.entity.MonHocEntity
import com.example.matestudy.ui.theme.*
import com.example.matestudy.ui.viewmodel.ReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingScreen(
    viewModel: ReviewViewModel,
    onNavigateToAddReview: () -> Unit
) {
    val monHocList by viewModel.monHocList.collectAsState(initial = emptyList())
    val hocKyList by viewModel.hocKyList.collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var selectedHocKyFilterId by remember { mutableStateOf<Long?>(null) }
    val expandedStates = remember { mutableStateMapOf<Long, Boolean>() }

    val filteredMonHocList = remember(monHocList, searchQuery, selectedHocKyFilterId) {
        monHocList
            .filter { monHoc ->
                val matchSearch = searchQuery.isBlank() ||
                        monHoc.tenMon?.contains(searchQuery, ignoreCase = true) == true ||
                        monHoc.tenGv?.contains(searchQuery, ignoreCase = true) == true
                val matchHocKy = selectedHocKyFilterId == null || monHoc.hocKyId == selectedHocKyFilterId
                matchSearch && matchHocKy
            }
            .sortedBy { it.tenMon }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddReview,
                containerColor = PrimaryPink,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Viết đánh giá")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ────────────────────────────────────────────────
            // 1. HEADER
            // ────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Đánh giá môn học",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
                )
                Text(
                    text = "Chia sẻ trải nghiệm về giảng viên & môn học",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // ────────────────────────────────────────────────
            // 2. SEARCH & FILTER
            // ────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Tìm môn học hoặc giảng viên...") },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryPink)
                    )

                    var filterExpanded by remember { mutableStateOf(false) }
                    Box {
                        Card(
                            modifier = Modifier
                                .width(170.dp)
                                .clickable { filterExpanded = true },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = hocKyList.find { it.id == selectedHocKyFilterId }?.ten_hoc_ky ?: "Tất cả học kỳ",
                                    maxLines = 1,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(Icons.Default.ArrowDropDown, null)
                            }
                        }

                        DropdownMenu(
                            expanded = filterExpanded,
                            onDismissRequest = { filterExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Tất cả học kỳ") },
                                onClick = {
                                    selectedHocKyFilterId = null
                                    filterExpanded = false
                                }
                            )
                            hocKyList.forEach { hocKy ->
                                DropdownMenuItem(
                                    text = { Text(hocKy.ten_hoc_ky) },
                                    onClick = {
                                        selectedHocKyFilterId = hocKy.id
                                        filterExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ────────────────────────────────────────────────
            // 3. DANH SÁCH MÔN HỌC & REVIEWS
            // ────────────────────────────────────────────────
            items(filteredMonHocList) { monHoc ->
                val isExpanded = expandedStates[monHoc.id] ?: false
                val average by viewModel.getAverageRating(monHoc.id).collectAsState(initial = 0.0)
                val count by viewModel.getReviewCount(monHoc.id).collectAsState(initial = 0)
                val reviews by viewModel.getReviewsByMonHoc(monHoc.id).collectAsState(initial = emptyList())

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedStates[monHoc.id] = !isExpanded }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape,
                                color = PrimaryLight
                            ) {
                                Icon(Icons.Default.Book, null, Modifier.padding(10.dp), PrimaryPink)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(monHoc.tenMon ?: "Môn học", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(
                                    "GV: ${monHoc.tenGv ?: "N/A"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, null, Modifier.size(18.dp), Color(0xFFFFB100))
                                    Text(String.format(" %.1f", average), fontWeight = FontWeight.Black, fontSize = 16.sp)
                                }
                                Text("$count lượt", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                            }
                            Icon(
                                if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
                                if (reviews.isEmpty()) {
                                    Text(
                                        "Chưa có đánh giá nào cho môn này.",
                                        color = Color.Gray,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                } else {
                                    reviews.forEach { review ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text("⭐".repeat(review.diem_sao ?: 0))
                                                    Spacer(Modifier.weight(1f))
                                                    Text(
                                                        review.ngay_dang.toString().take(10),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color.Gray
                                                    )
                                                }
                                                Spacer(Modifier.height(6.dp))
                                                Text(review.noi_dung)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (filteredMonHocList.isEmpty() && monHocList.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Không tìm thấy môn học nào phù hợp", color = Color.Gray)
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}