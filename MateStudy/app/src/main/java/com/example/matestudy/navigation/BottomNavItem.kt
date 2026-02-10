package com.example.matestudy.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Group

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "Diễn đàn", Icons.Default.Forum)
    object Schedule : BottomNavItem("schedule", "Thời gian biểu", Icons.Default.CalendarMonth)
    object Rating : BottomNavItem("rating", "Đánh giá", Icons.Default.Star)
    object Group : BottomNavItem("group", "Nhóm học", Icons.Default.Group)
    object Notification : BottomNavItem("notification", "Thông báo", Icons.Default.Notifications)
    object Profile : BottomNavItem("profile", "Cá nhân", Icons.Default.Person)
}