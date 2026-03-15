package com.example.matestudy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matestudy.data.Post
import com.example.matestudy.data.Comment
import com.example.matestudy.data.entity.CommentEntity
import com.example.matestudy.data.entity.PostEntity
import com.example.matestudy.data.repository.AuthRepository
import com.example.matestudy.data.repository.ForumRepository
import com.example.matestudy.data.toPost
import com.example.matestudy.data.toComment
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ForumViewModel(
    private val forumRepository: ForumRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _currentCategory = MutableStateFlow("all")
    val currentCategory: StateFlow<String> = _currentCategory.asStateFlow()

    private val currentUserId: StateFlow<Long> = authRepository.getCurrentUserFlow()
        .map { it?.id ?: 0L }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val posts: StateFlow<List<Post>> = _currentCategory.flatMapLatest { cat ->
        val userId = currentUserId.value
        when (cat) {
            "all" -> forumRepository.getAllPosts(userId)
            "featured" -> forumRepository.getFeaturedPosts(userId)
            else -> forumRepository.getPostsByCategory(cat, userId)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredPosts: StateFlow<List<Post>> = combine(posts, _searchQuery) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.tieuDe.contains(query, ignoreCase = true) ||
                    it.noiDung.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedPost = MutableStateFlow<Post?>(null)
    val selectedPost: StateFlow<Post?> = _selectedPost.asStateFlow()

    val comments: StateFlow<List<Comment>> = _selectedPost.flatMapLatest { post ->
        if (post != null) forumRepository.getCommentsForPost(post.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun setCategory(category: String) {
        _currentCategory.value = category
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun loadPostDetail(postId: Long) {
        viewModelScope.launch {
            val postEntity = forumRepository.getPostById(postId)
            if (postEntity != null) {
                val likeCount = forumRepository.getLikeCount(postId).firstOrNull() ?: 0
                val isLiked = forumRepository.toggleLike(postId, currentUserId.value) // chỉ load, không toggle
                _selectedPost.value = postEntity.toPost(likeCount, isLiked)
            } else {
                _error.value = "Không tìm thấy bài viết"
            }
        }
    }

    fun createPost(tieuDe: String, noiDung: String, category: String, filePath: String?) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserFlow().firstOrNull()?.id ?: 0L
            if (userId == 0L) {
                _error.value = "Vui lòng đăng nhập để đăng bài"
                return@launch
            }
            if (tieuDe.isBlank() || noiDung.isBlank()) {
                _error.value = "Tiêu đề và nội dung không được để trống"
                return@launch
            }

            val newPost = PostEntity(
                tac_gia_id = userId,
                tieu_de = tieuDe.trim(),
                noi_dung = noiDung.trim(),
                file_dinh_kem = filePath,
                category = category,
                trang_thai = "da_duyet",
                ngay_dang = System.currentTimeMillis()
            )

            forumRepository.createPost(newPost)
            _error.value = null
        }
    }

    fun addComment(postId: Long, noiDung: String) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserFlow().firstOrNull()?.id ?: 0L
            if (userId == 0L) {
                _error.value = "Vui lòng đăng nhập để bình luận"
                return@launch
            }

            val newComment = CommentEntity(
                bai_viet_id = postId,
                tac_gia_id = userId,
                noi_dung = noiDung.trim(),
                ngay_tao = System.currentTimeMillis()
            )

            forumRepository.addComment(newComment)
        }
    }

    fun toggleLike(postId: Long) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserFlow().firstOrNull()?.id ?: 0L
            if (userId == 0L) {
                _error.value = "Vui lòng đăng nhập để thích"
                return@launch
            }
            forumRepository.toggleLike(postId, userId)
        }
    }

    fun clearError() {
        _error.value = null
    }
}