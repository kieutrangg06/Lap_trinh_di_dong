package com.example.matestudy.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    // ── User items ────────────────────────────────────────
    object Home        : BottomNavItem("home",        "Diễn đàn",       Icons.Default.Forum)
    object Schedule    : BottomNavItem("schedule",    "Thời gian biểu", Icons.Default.CalendarMonth)
    object Rating      : BottomNavItem("rating",      "Đánh giá",       Icons.Default.Star)
    object Notification: BottomNavItem("notification","Thông báo",     Icons.Default.Notifications)
    object Profile     : BottomNavItem("profile",     "Cá nhân",        Icons.Default.Person)

    // ── Admin items ────────────────────────────────────────
    object AdminForum     : BottomNavItem("admin_forum",     "Quản lý diễn đàn",     Icons.Default.Forum)
    object AdminStudyData : BottomNavItem("admin_study_data","Quản lý dữ liệu học tập", Icons.Default.Storage) // hoặc Icons.Default.Folder
    object AdminRating    : BottomNavItem("admin_rating",    "Quản lý đánh giá",     Icons.Default.Star)
    object AdminUsers     : BottomNavItem("admin_users",     "Quản lý người dùng",   Icons.Default.People)
}