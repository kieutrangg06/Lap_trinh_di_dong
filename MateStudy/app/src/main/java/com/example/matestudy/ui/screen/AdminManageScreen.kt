package com.example.matestudy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManageScreen(viewModel: AdminViewModel) {
    val hocKyList by viewModel.hocKyList.collectAsState(emptyList())
    val monHocList by viewModel.filteredMonHocList.collectAsState(emptyList())
    val selectedHocKy by viewModel.selectedHocKy.collectAsState(null)
    val searchQuery by viewModel.searchQuery.collectAsState("")

    // Trạng thái điều hướng màn hình (Thay thế Dialog)
    var editingHocKy by remember { mutableStateOf<HocKyEntity?>(null) }
    var editingMonHoc by remember { mutableStateOf<MonHocEntity?>(null) }
    var isAddingHocKy by remember { mutableStateOf(false) }
    var isAddingMonHoc by remember { mutableStateOf(false) }

    when {
        // 1. MÀN HÌNH FORM HỌC KỲ (THÊM/SỬA)
        isAddingHocKy || editingHocKy != null -> {
            HocKyFormScreen(
                hocKy = editingHocKy,
                onSave = { entity ->
                    viewModel.saveHocKy(entity)
                    isAddingHocKy = false
                    editingHocKy = null
                },
                onCancel = { isAddingHocKy = false; editingHocKy = null }
            )
        }

        // 2. MÀN HÌNH FORM MÔN HỌC (THÊM/SỬA)
        isAddingMonHoc || editingMonHoc != null -> {
            MonHocFormScreen(
                monHoc = editingMonHoc,
                hocKyId = selectedHocKy?.id ?: 0L,
                onSave = { entity ->
                    viewModel.saveMonHoc(entity)
                    isAddingMonHoc = false
                    editingMonHoc = null
                },
                onCancel = { isAddingMonHoc = false; editingMonHoc = null }
            )
        }

        // 3. MÀN HÌNH DANH SÁCH CHÍNH
        else -> {
            Scaffold(
                containerColor = BackgroundGradientEnd,
                topBar = {
                    Column(Modifier.background(Color.White)) {
                        CenterAlignedTopAppBar(
                            title = { Text("QUẢN TRỊ HỆ THỐNG", fontWeight = FontWeight.Black, fontSize = 18.sp) },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                        )
                        // Thanh tìm kiếm + Dropdown lọc HK
                        Row(
                            Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = viewModel::onSearchQueryChange,
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Tìm tên môn...") },
                                leadingIcon = { Icon(Icons.Default.Search, null) },
                                shape = CircleShape,
                                singleLine = true
                            )
                            Spacer(Modifier.width(8.dp))
                            HocKyFilterDropdown(
                                hocKyList = hocKyList,
                                selectedHocKy = selectedHocKy,
                                onSelected = viewModel::selectHocKy
                            )
                        }
                    }
                },
                floatingActionButton = {
                    AdminFabGroup(
                        showAddMonHoc = selectedHocKy != null,
                        onAddHocKy = { isAddingHocKy = true },
                        onAddMonHoc = { isAddingMonHoc = true }
                    )
                }
            ) { padding ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { SectionHeader("Môn học", Icons.Default.Book, SecondaryBlue) }

                    if (selectedHocKy == null) {
                        item { InfoState("Chọn một học kỳ từ dropdown để xem môn học.") }
                    } else if (monHocList.isEmpty()) {
                        item { EmptyState("Không có môn học nào.") }
                    } else {
                        items(monHocList) { mon ->
                            MonHocItem(
                                monHoc = mon,
                                onEdit = { editingMonHoc = mon },
                                onDelete = { viewModel.deleteMonHoc(mon.id) }
                            )
                        }
                    }

                    item { Spacer(Modifier.height(16.dp)) }
                    item { SectionHeader("Danh sách Học kỳ", Icons.Default.DateRange, PrimaryPink) }

                    items(hocKyList) { hk ->
                        HocKyItem(
                            hocKy = hk,
                            isSelected = selectedHocKy?.id == hk.id,
                            onClick = { viewModel.selectHocKy(hk) },
                            onEdit = { editingHocKy = hk },
                            onDelete = { viewModel.deleteHocKy(hk.id) }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

// --- DROPDOWN LỌC HỌC KỲ ---
@Composable
fun HocKyFilterDropdown(
    hocKyList: List<HocKyEntity>,
    selectedHocKy: HocKyEntity?,
    onSelected: (HocKyEntity?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(
            onClick = { expanded = true },
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            Text(selectedHocKy?.ten_hoc_ky ?: "Tất cả HK", fontSize = 12.sp, maxLines = 1)
            Icon(Icons.Default.ArrowDropDown, null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Tất cả HK") },
                onClick = { onSelected(null); expanded = false }
            )
            hocKyList.forEach { hk ->
                DropdownMenuItem(
                    text = { Text(hk.ten_hoc_ky) },
                    onClick = { onSelected(hk); expanded = false }
                )
            }
        }
    }
}

// --- FORM MÔN HỌC (SỬ DỤNG CALENDAR & CLOCK) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonHocFormScreen(monHoc: MonHocEntity?, hocKyId: Long, onSave: (MonHocEntity) -> Unit, onCancel: () -> Unit) {
    var tenMon by remember { mutableStateOf(monHoc?.tenMon ?: "") }
    var tenGv by remember { mutableStateOf(monHoc?.tenGv ?: "") }
    var diaDiem by remember { mutableStateOf(monHoc?.diaDiem ?: "") }
    var thu by remember { mutableStateOf(monHoc?.thu ?: "") }
    var gioBd by remember { mutableStateOf(monHoc?.gioBatDau ?: "") }
    var gioKt by remember { mutableStateOf(monHoc?.gioKetThuc ?: "") }
    var ngayBd by remember { mutableStateOf(monHoc?.ngayBatDau ?: "") }
    var ngayKt by remember { mutableStateOf(monHoc?.ngayKetThuc ?: "") }

    var showDatePicker by remember { mutableStateOf(false) }
    var isNgayKetThuc by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var isGioKetThuc by remember { mutableStateOf(false) }

    // Logic DatePicker (Lịch)
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it))
                        if (isNgayKetThuc) ngayKt = date else ngayBd = date
                    }
                    showDatePicker = false
                }) { Text("CHỌN") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    // Logic TimePicker (Đồng hồ)
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(is24Hour = true)
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val time = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                    if (isGioKetThuc) gioKt = time else gioBd = time
                    showTimePicker = false
                }) { Text("CHỌN") }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (monHoc == null) "Thêm môn học" else "Sửa môn học") },
                navigationIcon = { IconButton(onClick = onCancel) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AdminTextField("Tên môn học", tenMon) { tenMon = it }
            AdminTextField("Tên giảng viên", tenGv) { tenGv = it }
            AdminTextField("Địa điểm", diaDiem) { diaDiem = it }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(0.3f)) { AdminTextField("Thứ", thu) { thu = it } }
                Box(Modifier.weight(0.7f)) {
                    OutlinedTextField(
                        value = gioBd, onValueChange = {}, label = { Text("Bắt đầu") },
                        modifier = Modifier.clickable { isGioKetThuc = false; showTimePicker = true },
                        enabled = false, readOnly = true, colors = TextFieldDefaults.colors(disabledTextColor = Color.Black)
                    )
                }
            }

            OutlinedTextField(
                value = gioKt, onValueChange = {}, label = { Text("Kết thúc") },
                modifier = Modifier.fillMaxWidth().clickable { isGioKetThuc = true; showTimePicker = true },
                enabled = false, readOnly = true, colors = TextFieldDefaults.colors(disabledTextColor = Color.Black)
            )

            AdminDateField("Ngày bắt đầu (Lịch)", ngayBd) { isNgayKetThuc = false; showDatePicker = true }
            AdminDateField("Ngày kết thúc (Lịch)", ngayKt) { isNgayKetThuc = true; showDatePicker = true }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    onSave(MonHocEntity(
                        id = monHoc?.id ?: 0L, tenMon = tenMon, tenGv = tenGv,
                        diaDiem = diaDiem, thu = thu, gioBatDau = gioBd,
                        gioKetThuc = gioKt, ngayBatDau = ngayBd, ngayKetThuc = ngayKt,
                        hocKyId = hocKyId
                    ))
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) { Text("LƯU DỮ LIỆU") }
        }
    }
}

// --- FORM HỌC KỲ ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HocKyFormScreen(hocKy: HocKyEntity?, onSave: (HocKyEntity) -> Unit, onCancel: () -> Unit) {
    var tenHocKy by remember { mutableStateOf(hocKy?.ten_hoc_ky ?: "") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (hocKy == null) "Thêm học kỳ" else "Sửa học kỳ") },
                navigationIcon = { IconButton(onClick = onCancel) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(20.dp)) {
            AdminTextField("Tên học kỳ", tenHocKy) { tenHocKy = it }
            Spacer(Modifier.height(24.dp))
            Button(onClick = { if (tenHocKy.isNotBlank()) onSave(hocKy?.copy(ten_hoc_ky = tenHocKy) ?: HocKyEntity(ten_hoc_ky = tenHocKy)) }, modifier = Modifier.fillMaxWidth()) {
                Text("XÁC NHẬN")
            }
        }
    }
}

// --- CÁC THÀNH PHẦN UI HỖ TRỢ ---

@Composable
fun AdminDateField(label: String, value: String, onClick: () -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = {}, label = { Text(label) },
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        enabled = false, readOnly = true,
        trailingIcon = { Icon(Icons.Default.CalendarMonth, null) },
        colors = TextFieldDefaults.colors(disabledTextColor = Color.Black)
    )
}

@Composable
fun HocKyItem(hocKy: HocKyEntity, isSelected: Boolean, onClick: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFFFEBF2) else Color.White),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, PrimaryPink) else null
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked, null, tint = if (isSelected) PrimaryPink else Color.Gray)
            Text(hocKy.ten_hoc_ky, Modifier.weight(1f).padding(start = 12.dp), fontWeight = FontWeight.Bold)
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = Color.Gray) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.DeleteSweep, null, tint = Color.LightGray) }
        }
    }
}

@Composable
fun MonHocItem(monHoc: MonHocEntity, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(40.dp), CircleShape, PrimaryLight) { Icon(Icons.Default.Class, null, Modifier.padding(10.dp), PrimaryPink) }
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text(monHoc.tenMon, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("GV: ${monHoc.tenGv} | Thứ ${monHoc.thu}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text("${monHoc.gioBatDau} - ${monHoc.gioKetThuc}", style = MaterialTheme.typography.labelSmall, color = SecondaryBlue, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = Color.Gray) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.DeleteOutline, null, tint = ErrorRed) }
        }
    }
}

@Composable
fun AdminFabGroup(showAddMonHoc: Boolean, onAddHocKy: () -> Unit, onAddMonHoc: () -> Unit) {
    Column(horizontalAlignment = Alignment.End) {
        if (showAddMonHoc) {
            ExtendedFloatingActionButton(onClick = onAddMonHoc, containerColor = SecondaryBlue, contentColor = Color.White, icon = { Icon(Icons.Default.LibraryAdd, null) }, text = { Text("Môn học") }, modifier = Modifier.padding(bottom = 12.dp))
        }
        ExtendedFloatingActionButton(onClick = onAddHocKy, containerColor = PrimaryPink, contentColor = Color.White, icon = { Icon(Icons.Default.NoteAdd, null) }, text = { Text("Học kỳ") })
    }
}

@Composable
fun AdminTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(label) }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, singleLine = true)
}

@Composable fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color = PrimaryPink) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
    }
}

@Composable fun EmptyState(t: String) { Text(t, Modifier.fillMaxWidth().padding(20.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = Color.Gray, style = MaterialTheme.typography.bodySmall) }
@Composable fun InfoState(t: String) {
    Surface(color = Color(0xFFE3F2FD), shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, null, tint = SecondaryBlue)
            Spacer(Modifier.width(12.dp))
            Text(t, style = MaterialTheme.typography.bodySmall, color = SecondaryBlue)
        }
    }
}
