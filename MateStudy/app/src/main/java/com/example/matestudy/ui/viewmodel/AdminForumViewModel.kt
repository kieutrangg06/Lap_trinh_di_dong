package com.example.matestudy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matestudy.data.Post
import com.example.matestudy.data.repository.ForumRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminForumViewModel(private val repository: ForumRepository) : ViewModel() {

    private val _allPosts = repository.getAllPostsForAdmin()
    
    // Trạng thái lọc: "Tất cả", "cho_duyet", "da_duyet", "bi_an"
    private val _filterStatus = MutableStateFlow("Tất cả")
    val filterStatus = _filterStatus.asStateFlow()

    val filteredPosts: StateFlow<List<Post>> = combine(_allPosts, _filterStatus) { posts, status ->
        if (status == "Tất cả") posts 
        else posts.filter { 
            when(status) {
                "Chờ duyệt" -> it.trangThai == "cho_duyet"
                "Đã duyệt" -> it.trangThai == "da_duyet"
                "Bị ẩn" -> it.trangThai == "bi_an"
                else -> true
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(status: String) { _filterStatus.value = status }

    fun deletePost(postId: Long) = viewModelScope.launch { repository.deletePost(postId) }

    fun approvePost(postId: Long) = viewModelScope.launch {
        repository.approvePostWithNotify(postId)
    }

    fun hidePost(postId: Long) = viewModelScope.launch {
        repository.hidePost(postId)
        // Bạn có thể thêm notify "Bài viết bị ẩn do vi phạm" tương tự tại đây
    }
}