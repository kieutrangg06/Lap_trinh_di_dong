package com.example.matestudy.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matestudy.data.entity.ReviewEntity
import com.example.matestudy.ui.theme.PrimaryPink
import com.example.matestudy.ui.theme.TextSecondary
import com.example.matestudy.ui.viewmodel.ReviewViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDetailScreen(
    viewModel: ReviewViewModel,
    monHocId: Long,
    onBack: () -> Unit
) {
    val monHocList by viewModel.monHocList.collectAsState()
    val monHoc = monHocList.find { it.id == monHocId }
    val reviews by viewModel.getReviewsByMonHoc(monHocId).collectAsState(initial = emptyList())
    val formatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết đánh giá") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (monHoc != null) {
                Text(
                    text = monHoc.tenMon ?: "Môn học",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPink
                )
                Text(
                    text = "Giảng viên: ${monHoc.tenGv}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                if (reviews.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Chưa có đánh giá nào được duyệt.")
                    }
                } else {
                    reviews.forEach { review ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    repeat(5) { index ->
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = null,
                                            tint = if (index < (review.diem_sao ?: 0)) Color(0xFFFFB100) else Color.LightGray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(Modifier.weight(1f))
                                    Text(
                                        text = formatter.format(Date(review.ngay_dang)),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(text = review.noi_dung, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryPink)
                }
            }
        }
    }
}