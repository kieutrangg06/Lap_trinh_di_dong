package com.nikoniche.booki

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Story(
    var id: String = "",
    var title: String = "",
    var authorId: String = "test_user_123",
    var authorName: String = "Tác giả Test",
    var description: String = "",
    var coverUrl: String = "",
    var genres: String = "Lãng mạn",
    var status: String = "Đang viết", // Đang viết, Hoàn thành, Tạm ngưng
    var viewCount: Long = 0,
    var voteCount: Long = 0,
    var chapterCount: Int = 0,
    var createdAt: Long = System.currentTimeMillis()
)

@IgnoreExtraProperties
data class Chapter(
    var id: String = "",
    var storyId: String = "",
    var title: String = "",
    var content: String = "",
    var order: Int = 1,
    var isPublished: Boolean = true,
    var createdAt: Long = System.currentTimeMillis()
)