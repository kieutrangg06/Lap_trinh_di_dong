package com.nikoniche.booki

import com.google.firebase.firestore.IgnoreExtraProperties

// 1. Dành cho Quản lý tài khoản (2.1)
@IgnoreExtraProperties
data class UserProfile(
    var uid: String = "",
    var username: String = "",
    var email: String = "",
    var avatarUrl: String = "",
    var bio: String = ""
)

// 2. Dành cho Truyện (2.2, 2.3, 2.6)
@IgnoreExtraProperties
data class Story(
    var id: String = "",
    var title: String = "",
    var authorId: String = "",
    var authorName: String = "",
    var description: String = "",
    var coverUrl: String = "",
    var genres: List<String> = emptyList(),
    var status: String = "Ongoing", // Ongoing hoặc Completed
    var viewCount: Long = 0,
    var averageRate: Double = 0.0,
    var totalRates: Int = 0,
    var createdAt: Long = System.currentTimeMillis()
)

// 3. Nội dung chương (2.2, 2.6)
@IgnoreExtraProperties
data class Chapter(
    var id: String = "",
    var storyId: String = "",
    var title: String = "",
    var content: String = "", // Nội dung truyện cực dài nằm ở đây
    var order: Int = 1,        // Số thứ tự chương: 1, 2, 3...
    var createdAt: Long = System.currentTimeMillis()
)

// 4. Thư viện & Tương tác (2.4, 2.5)
@IgnoreExtraProperties
data class LibraryItem(
    var id: String = "", // storyId
    var userId: String = "",
    var lastReadChapterId: String = "", // Lưu vị trí đang đọc (2.2)
    var isFavorite: Boolean = false,
    var lastReadTime: Long = System.currentTimeMillis()
)

// 5. Thông báo (2.7)
@IgnoreExtraProperties
data class AppNotification(
    var id: String = "",
    var receiverId: String = "",
    var message: String = "",
    var type: String = "", // "NEW_CHAPTER" hoặc "NEW_RATE"
    var timestamp: Long = System.currentTimeMillis(),
    var isRead: Boolean = false
)