package com.example.matestudy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Class
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.matestudy.ui.theme.BackgroundGradientEnd
import com.example.matestudy.ui.theme.PrimaryLight
import com.example.matestudy.ui.theme.PrimaryPink
import com.example.matestudy.ui.theme.TextSecondary
import com.example.matestudy.ui.viewmodel.ScheduleViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseClassScreen(viewModel: ScheduleViewModel, onBack: () -> Unit) {
    val hocKyId = 1L
    val monHocList by viewModel.scheduleRepository.getMonHocByHocKy(hocKyId).collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chọn lớp học", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(BackgroundGradientEnd)) {
            if (monHocList.isEmpty()) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("Không có lớp học nào", color = TextSecondary)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(monHocList) { mon ->
                        Card(
                            onClick = { coroutineScope.launch { viewModel.addClass(mon.id) }; onBack() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Surface(Modifier.size(48.dp), CircleShape, PrimaryLight) {
                                    Icon(Icons.Default.Class, null, Modifier.padding(12.dp), PrimaryPink)
                                }
                                Spacer(Modifier.width(16.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(mon.tenMon, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("GV: ${mon.tenGv}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("${mon.gioBatDau} - ${mon.gioKetThuc}", fontWeight = FontWeight.Bold, color = PrimaryPink, fontSize = 12.sp)
                                    Text(mon.diaDiem ?: "N/A", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}