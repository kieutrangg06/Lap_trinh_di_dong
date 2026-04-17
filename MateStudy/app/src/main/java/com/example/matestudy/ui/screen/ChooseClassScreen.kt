package com.example.matestudy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import com.example.matestudy.ui.viewmodel.ScheduleViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseClassScreen(viewModel: ScheduleViewModel, onBack: () -> Unit) {
    val hocKyList by viewModel.scheduleRepository.getAllHocKy().collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var selectedHocKy by remember { mutableStateOf<HocKyEntity?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(hocKyList) {
        if (selectedHocKy == null && hocKyList.isNotEmpty()) {
            selectedHocKy = hocKyList.first()
        }
    }

    val monHocList by produceState<List<MonHocEntity>>(initialValue = emptyList(), key1 = selectedHocKy) {
        selectedHocKy?.let { hk ->
            viewModel.scheduleRepository.getMonHocByHocKy(hk.id).collect { value = it }
        }
    }

    val filteredMonHocList = remember(monHocList, searchQuery) {
        if (searchQuery.isEmpty()) monHocList
        else monHocList.filter {
            it.tenMon.contains(searchQuery, ignoreCase = true) ||
                    it.tenGv.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thêm môn học", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            // ────────────────────────────────────────────────
            // 1. THANH TÌM KIẾM & DROPDOWN HỌC KỲ
            // ────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Tìm tên môn, giảng viên...", fontSize = 14.sp) },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.width(130.dp)
                ) {
                    OutlinedTextField(
                        value = selectedHocKy?.ten_hoc_ky ?: "Học kỳ",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
                        shape = MaterialTheme.shapes.medium
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        hocKyList.forEach { hk ->
                            DropdownMenuItem(
                                text = { Text(hk.ten_hoc_ky) },
                                onClick = {
                                    selectedHocKy = hk
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Divider(thickness = 0.5.dp, color = Color.LightGray)

            // ────────────────────────────────────────────────
            // 2. DANH SÁCH MÔN HỌC
            // ────────────────────────────────────────────────
            if (filteredMonHocList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không tìm thấy môn học phù hợp", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredMonHocList) { monHoc ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.addClass(monHoc.id)
                                    onBack()
                                }
                            },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = monHoc.tenMon,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(text = "GV: ${monHoc.tenGv}", fontSize = 14.sp)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Thứ: ${monHoc.thu}", fontSize = 13.sp, color = Color.Gray)
                                    Text(
                                        text = "${monHoc.gioBatDau} - ${monHoc.gioKetThuc}",
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}