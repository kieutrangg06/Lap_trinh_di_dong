package com.example.matestudy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matestudy.data.Post
import com.example.matestudy.data.repository.ForumRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminForumViewModel(private val repository: ForumRepository) : ViewModel() {

    private val _allPosts = repository.getAllPostsForAdmin()
    private val _filterStatus = MutableStateFlow("Tất cả")
    val filterStatus = _filterStatus.asStateFlow()
    private val _searchQuery = MutableStateFlow("")

    // ────────────────────────────────────────────────
    // 1. XỬ LÝ LOGIC LỌC VÀ TÌM KIẾM (FLOW)
    // ────────────────────────────────────────────────

    val filteredPosts: StateFlow<List<Post>> = combine(
        _allPosts, _filterStatus, _searchQuery
    ) { posts, status, query ->
        var result = posts

        if (status != "Tất cả") {
            result = result.filter {
                when (status) {
                    "Chờ duyệt" -> it.trangThai == "cho_duyet"
                    "Đã duyệt" -> it.trangThai == "da_duyet"
                    "Bị ẩn" -> it.trangThai == "bi_an"
                    else -> false
                }
            }
        }

        if (query.isNotBlank()) {
            val lowerQuery = query.lowercase()
            result = result.filter {
                (it.tieuDe?.lowercase()?.contains(lowerQuery) == true) ||
                        (it.noiDung?.lowercase()?.contains(lowerQuery) == true)
            }
        }

        result
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ────────────────────────────────────────────────
    // 2. CÁC HÀM CẬP NHẬT TRẠNG THÁI UI
    // ────────────────────────────────────────────────

    fun setFilter(status: String) {
        _filterStatus.value = status
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // ────────────────────────────────────────────────
    // 3. CÁC THAO TÁC DỮ LIỆU (REPOSITORY)
    // ────────────────────────────────────────────────

    fun approvePost(postId: Long) = viewModelScope.launch {
        repository.approvePostWithNotify(postId)
    }

    fun hidePost(postId: Long) = viewModelScope.launch {
        repository.hidePost(postId)
    }

    fun deletePost(postId: Long) = viewModelScope.launch {
        repository.deletePost(postId)
    }
}