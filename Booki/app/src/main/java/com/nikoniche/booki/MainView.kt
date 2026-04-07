package com.nikoniche.booki

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nikoniche.booki.writer.ChapterManagementView
import com.nikoniche.booki.writer.WriterDashboardView
import com.nikoniche.booki.writer.WriterViewModel

@Composable
fun MainView() {
    var selectedTab by remember { mutableIntStateOf(2) } // Mặc định mở Tab Studio để bạn test
    val writerViewModel: WriterViewModel = viewModel()

    // State để quản lý việc điều hướng sâu vào bên trong Tab Studio
    // Nếu selectedStory != null nghĩa là đang ở màn hình Quản lý chương
    var selectedStory by remember { mutableStateOf<Story?>(null) }

    Scaffold(
        bottomBar = {
            // Chỉ hiện BottomBar khi không ở màn hình quản lý chương (cho chuyên nghiệp)
            if (selectedStory == null) {
                NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        label = { Text("Trang chủ") },
                        icon = { Icon(Icons.Rounded.Home, null) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        label = { Text("Thư viện") },
                        icon = { Icon(Icons.Rounded.AutoStories, null) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        label = { Text("Studio") },
                        icon = { Icon(Icons.Rounded.Create, null) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFF6D00),
                            selectedTextColor = Color(0xFFFF6D00)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> Text("Màn hình Trang chủ")
                1 -> Text("Màn hình Thư viện")
                2 -> {
                    // LOGIC ĐIỀU HƯỚNG TRONG STUDIO
                    if (selectedStory == null) {
                        // Màn hình 1: Danh sách truyện
                        WriterDashboardView(
                            viewModel = writerViewModel,
                            onManageChapters = { story ->
                                selectedStory = story // Khi nhấn "Quản lý chương", gán story để chuyển màn hình
                            }
                        )
                    } else {
                        // Màn hình 2: Quản lý chương (ĐƯỢC GỌI Ở ĐÂY)
                        ChapterManagementView(
                            story = selectedStory!!,
                            viewModel = writerViewModel,
                            onBack = { selectedStory = null } // Nhấn back thì gán null để quay về danh sách
                        )
                    }
                }
            }
        }
    }
}