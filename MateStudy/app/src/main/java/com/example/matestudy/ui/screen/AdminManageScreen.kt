package com.example.matestudy.ui.screen

import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import com.example.matestudy.data.entity.HocKyEntity
import com.example.matestudy.data.entity.MonHocEntity
import com.example.matestudy.ui.theme.*
import com.example.matestudy.ui.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManageScreen(viewModel: AdminViewModel) {
    val hocKyList by viewModel.hocKyList.collectAsState(emptyList())
    val monHocList by viewModel.monHocList.collectAsState(emptyList())
    val selectedHocKy by viewModel.selectedHocKy.collectAsState(null)

    var showAddHocKyDialog by remember { mutableStateOf(false) }
    var showAddMonHocDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundGradientEnd,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("QUẢN TRỊ HỆ THỐNG", fontWeight = FontWeight.Black, fontSize = 18.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { /* Refresh logic */ }) {
                        Icon(Icons.Default.Refresh, null, tint = PrimaryPink)
                    }
                }
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                // Nút thêm Môn học (Chỉ hiện khi đã chọn Học kỳ)
                if (selectedHocKy != null) {
                    ExtendedFloatingActionButton(
                        onClick = { showAddMonHocDialog = true },
                        containerColor = SecondaryBlue,
                        contentColor = Color.White,
                        icon = { Icon(Icons.Default.LibraryAdd, null) },
                        text = { Text("Môn học") },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                // Nút thêm Học kỳ
                ExtendedFloatingActionButton(
                    onClick = { showAddHocKyDialog = true },
                    containerColor = PrimaryPink,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.NoteAdd, null) },
                    text = { Text("Học kỳ") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // PHẦN HỌC KỲ
            item {
                SectionHeader(title = "Danh sách Học kỳ", icon = Icons.Default.DateRange)
            }

            if (hocKyList.isEmpty()) {
                item { EmptyState("Chưa có học kỳ nào được tạo.") }
            }

            items(hocKyList) { hk ->
                HocKyItem(
                    hocKy = hk,
                    isSelected = selectedHocKy?.id == hk.id,
                    onClick = { viewModel.selectHocKy(hk) },
                    onDelete = { viewModel.deleteHocKy(hk) }
                )
            }

            // PHẦN MÔN HỌC
            item {
                Spacer(Modifier.height(8.dp))
                SectionHeader(
                    title = if (selectedHocKy != null) "Môn học: ${selectedHocKy?.ten_hoc_ky}" else "Danh sách Môn học",
                    icon = Icons.Default.Book,
                    color = SecondaryBlue
                )
            }

            if (selectedHocKy == null) {
                item { InfoState("Vui lòng chọn một học kỳ phía trên để xem môn học.") }
            } else if (monHocList.isEmpty()) {
                item { EmptyState("Học kỳ này chưa có môn học nào.") }
            } else {
                items(monHocList) { mon ->
                    MonHocItem(
                        monHoc = mon,
                        onDelete = { viewModel.deleteMonHoc(mon) }
                    )
                }
            }

            item { Spacer(Modifier.height(100.dp)) }
        }
    }

    // Dialog thêm học kỳ
    if (showAddHocKyDialog) {
        var tenHocKy by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddHocKyDialog = false },
            shape = MaterialTheme.shapes.large,
            title = { Text("Thêm học kỳ mới", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = tenHocKy,
                    onValueChange = { tenHocKy = it },
                    label = { Text("Tên học kỳ (VD: 2024-HK1)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (tenHocKy.isNotBlank()) {
                        viewModel.addHocKy(HocKyEntity(ten_hoc_ky = tenHocKy.trim()))
                        showAddHocKyDialog = false
                    }
                }) { Text("XÁC NHẬN") }
            },
            dismissButton = { TextButton(onClick = { showAddHocKyDialog = false }) { Text("HỦY") } }
        )
    }

    // Dialog thêm môn học
    if (showAddMonHocDialog && selectedHocKy != null) {
        AddMonHocDialog(
            hocKyId = selectedHocKy!!.id,
            onDismiss = { showAddMonHocDialog = false },
            onAdd = { mon -> viewModel.addMonHoc(mon) }
        )
    }
}

@Composable
fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color = PrimaryPink) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
    }
}

@Composable
fun HocKyItem(hocKy: HocKyEntity, isSelected: Boolean, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFEBF2) else Color.White
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, PrimaryPink) else null,
        elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isSelected) PrimaryPink else Color.Gray
            )
            Spacer(Modifier.width(16.dp))
            Text(
                hocKy.ten_hoc_ky,
                modifier = Modifier.weight(1f),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) PrimaryPink else TextPrimary
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteSweep, null, tint = Color.LightGray)
            }
        }
    }
}

@Composable
fun MonHocItem(monHoc: MonHocEntity, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(40.dp), CircleShape, PrimaryLight) {
                Icon(Icons.Default.Class, null, Modifier.padding(10.dp), PrimaryPink)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(monHoc.tenMon, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Giảng viên: ${monHoc.tenGv}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text(
                    "Thứ ${monHoc.thu}: ${monHoc.gioBatDau} - ${monHoc.gioKetThuc}",
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryBlue,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteOutline, null, tint = ErrorRed)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMonHocDialog(hocKyId: Long, onDismiss: () -> Unit, onAdd: (MonHocEntity) -> Unit) {
    var tenMon by remember { mutableStateOf("") }
    var tenGv by remember { mutableStateOf("") }
    var diaDiem by remember { mutableStateOf("") }
    var thu by remember { mutableStateOf("") }
    var gioBd by remember { mutableStateOf("") }
    var gioKt by remember { mutableStateOf("") }
    var ngayBd by remember { mutableStateOf("") }
    var ngayKt by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.large,
        title = { Text("Thông tin môn học", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminTextField("Tên môn học", tenMon) { tenMon = it }
                AdminTextField("Tên giảng viên", tenGv) { tenGv = it }
                AdminTextField("Địa điểm", diaDiem) { diaDiem = it }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.weight(0.4f)) { AdminTextField("Thứ", thu) { thu = it } }
                    Box(Modifier.weight(0.6f)) { AdminTextField("Bắt đầu", gioBd) { gioBd = it } }
                }
                AdminTextField("Kết thúc", gioKt) { gioKt = it }
                AdminTextField("Ngày bắt đầu (yyyy-MM-dd)", ngayBd) { ngayBd = it }
                AdminTextField("Ngày kết thúc (yyyy-MM-dd)", ngayKt) { ngayKt = it }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (tenMon.isNotBlank() && tenGv.isNotBlank()) {
                    onAdd(MonHocEntity(
                        tenMon = tenMon.trim(), tenGv = tenGv.trim(),
                        diaDiem = diaDiem.takeIf { it.isNotBlank() },
                        thu = thu.takeIf { it.isNotBlank() },
                        gioBatDau = gioBd.takeIf { it.isNotBlank() },
                        gioKetThuc = gioKt.takeIf { it.isNotBlank() },
                        ngayBatDau = ngayBd, ngayKetThuc = ngayKt,
                        hocKyId = hocKyId
                    ))
                    onDismiss()
                }
            }) { Text("LƯU MÔN HỌC") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("HỦY") } }
    )
}

@Composable
fun AdminTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        singleLine = true
    )
}

@Composable
fun EmptyState(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        color = Color.Gray,
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
fun InfoState(text: String) {
    Surface(
        color = Color(0xFFE3F2FD),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, null, tint = SecondaryBlue)
            Spacer(Modifier.width(12.dp))
            Text(text, style = MaterialTheme.typography.bodySmall, color = SecondaryBlue)
        }
    }
}