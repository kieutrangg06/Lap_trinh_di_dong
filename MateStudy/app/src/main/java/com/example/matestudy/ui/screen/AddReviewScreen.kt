package com.example.matestudy.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matestudy.data.entity.HocKyEntity
import com.example.matestudy.data.entity.MonHocEntity
import com.example.matestudy.ui.theme.*
import com.example.matestudy.ui.viewmodel.ReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewScreen(
    viewModel: ReviewViewModel,
    onBack: () -> Unit,
    onReviewSubmitted: () -> Unit
) {
    val hocKyList by viewModel.hocKyList.collectAsState(initial = emptyList())
    val monHocList by viewModel.monHocList.collectAsState(initial = emptyList())
    val errorMsg by viewModel.error.collectAsState()

    var selectedHocKy by remember { mutableStateOf<HocKyEntity?>(null) }
    var selectedMonHoc by remember { mutableStateOf<MonHocEntity?>(null) }
    var rating by remember { mutableStateOf(0) }
    var content by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Viết đánh giá mới", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // ────────────────────────────────────────────────
            // 1. CHỌN HỌC KỲ
            // ────────────────────────────────────────────────
            Text("Học kỳ", style = MaterialTheme.typography.labelMedium)
            var hocKyExpanded by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { hocKyExpanded = true },
                border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedHocKy?.ten_hoc_ky ?: "Chọn học kỳ",
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Default.ArrowDropDown, null)
                }
            }

            DropdownMenu(
                expanded = hocKyExpanded,
                onDismissRequest = { hocKyExpanded = false }
            ) {
                hocKyList.forEach { hocKy ->
                    DropdownMenuItem(
                        text = { Text(hocKy.ten_hoc_ky) },
                        onClick = {
                            selectedHocKy = hocKy
                            selectedMonHoc = null
                            hocKyExpanded = false
                        }
                    )
                }
            }

            // ────────────────────────────────────────────────
            // 2. CHỌN MÔN HỌC
            // ────────────────────────────────────────────────
            if (selectedHocKy != null) {
                Text("Môn học", style = MaterialTheme.typography.labelMedium)
                var monHocExpanded by remember { mutableStateOf(false) }

                val monHocCuaHocKy = remember(selectedHocKy, monHocList) {
                    monHocList.filter { it.hocKyId == selectedHocKy?.id }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { monHocExpanded = true },
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedMonHoc?.let { "${it.tenMon} - ${it.tenGv}" } ?: "Chọn môn học",
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                }

                DropdownMenu(
                    expanded = monHocExpanded,
                    onDismissRequest = { monHocExpanded = false }
                ) {
                    if (monHocCuaHocKy.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Không có môn học nào trong học kỳ này") },
                            onClick = {},
                            enabled = false
                        )
                    } else {
                        monHocCuaHocKy.forEach { monHoc ->
                            DropdownMenuItem(
                                text = { Text("${monHoc.tenMon} - ${monHoc.tenGv}") },
                                onClick = {
                                    selectedMonHoc = monHoc
                                    monHocExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // ────────────────────────────────────────────────
            // 3. ĐÁNH GIÁ SAO (RATING)
            // ────────────────────────────────────────────────
            Text("Mức độ hài lòng", style = MaterialTheme.typography.labelMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                (1..5).forEach { star ->
                    IconButton(onClick = { rating = star }) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = if (star <= rating) Color(0xFFFFB100) else Color.LightGray,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            // ────────────────────────────────────────────────
            // 4. NỘI DUNG ĐÁNH GIÁ
            // ────────────────────────────────────────────────
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Cảm nhận của bạn về môn học và giảng viên...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryPink)
            )

            errorMsg?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.weight(1f))

            // ────────────────────────────────────────────────
            // 5. NÚT GỬI ĐÁNH GIÁ
            // ────────────────────────────────────────────────
            Button(
                onClick = {
                    viewModel.submitReviewNew(
                        monHoc = selectedMonHoc,
                        rating = rating,
                        content = content
                    ) {
                        selectedHocKy = null
                        selectedMonHoc = null
                        rating = 0
                        content = ""
                        onReviewSubmitted()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
                enabled = selectedMonHoc != null && rating > 0 && content.isNotBlank()
            ) {
                Text("Gửi đánh giá", fontSize = 16.sp)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}