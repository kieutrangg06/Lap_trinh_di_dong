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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ForumViewModel(
    private val forumRepository: ForumRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // ────────────────────────────────────────────────
    // 1. STATE & USER CONTEXT
    // ────────────────────────────────────────────────

    private val currentUserId: StateFlow<Long> = authRepository.getCurrentUserFlow()
        .map { it?.id ?: 0L }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // ────────────────────────────────────────────────
    // 2. DANH SÁCH BÀI VIẾT (CATEGORY & SEARCH)
    // ────────────────────────────────────────────────

    private val _currentCategory = MutableStateFlow("all")
    val currentCategory: StateFlow<String> = _currentCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val posts: StateFlow<List<Post>> = combine(_currentCategory, currentUserId) { cat, userId ->
        cat to userId
    }.flatMapLatest { (cat, userId) ->
        when (cat) {
            "all" -> forumRepository.getAllPosts(userId)
            "featured" -> forumRepository.getFeaturedPosts(userId)
            else -> forumRepository.getPostsByCategory(cat, userId)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredPosts: StateFlow<List<Post>> = combine(posts, _searchQuery) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.tieuDe.contains(query, ignoreCase = true) ||
                    it.noiDung.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setCategory(category: String) {
        _currentCategory.value = category
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    // ────────────────────────────────────────────────
    // 3. CHI TIẾT BÀI VIẾT & BÌNH LUẬN
    // ────────────────────────────────────────────────

    private val _selectedPostId = MutableStateFlow<Long?>(null)
    private val _selectedPost = MutableStateFlow<Post?>(null)
    val selectedPost: StateFlow<Post?> = _selectedPost.asStateFlow()

    val comments: StateFlow<List<Comment>> = _selectedPostId.flatMapLatest { id ->
        if (id != null) forumRepository.getCommentsForPost(id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadPostDetail(postId: Long) {
        _selectedPostId.value = postId
        viewModelScope.launch {
            try {
                val userId = currentUserId.value
                val post = forumRepository.getPostWithAuthor(postId, userId)
                _selectedPost.value = post
            } catch (e: Exception) {
                _error.value = "Lỗi tải bài viết: ${e.message}"
            }
        }
    }

    // ────────────────────────────────────────────────
    // 4. THAO TÁC DỮ LIỆU (POST, COMMENT, LIKE)
    // ────────────────────────────────────────────────

    fun createPost(tieuDe: String, noiDung: String, category: String, imageUrl: String?) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserFlow().firstOrNull()?.id ?: 0L
            if (userId == 0L) return@launch

            val newPost = PostEntity(
                tac_gia_id = userId,
                tieu_de = tieuDe.trim(),
                noi_dung = noiDung.trim(),
                file_dinh_kem = imageUrl,
                category = category,
                trang_thai = "cho_duyet",
                ngay_dang = System.currentTimeMillis()
            )
            forumRepository.createPost(newPost)
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
}