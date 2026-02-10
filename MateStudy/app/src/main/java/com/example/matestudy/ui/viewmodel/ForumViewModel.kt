//package com.example.matestudy.ui.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.matestudy.data.Post
//import com.example.matestudy.data.Comment
//import com.example.matestudy.data.entity.PostEntity
//import com.example.matestudy.data.entity.CommentEntity
//import kotlinx.coroutines.flow.SharingStarted
//import com.example.matestudy.data.repository.ForumRepository
//import com.example.matestudy.data.repository.AuthRepository
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.flatMapLatest
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.flow.stateIn
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.flow.combine
//import kotlinx.coroutines.flow.flowOf
//import kotlinx.coroutines.flow.firstOrNull
//import com.example.matestudy.data.toPost
//
//class ForumViewModel(
//    private val forumRepository: ForumRepository,
//    private val authRepository: AuthRepository
//) : ViewModel() {
//
//    private val _currentCategory = MutableStateFlow("all")
//    val currentCategory: StateFlow<String> = _currentCategory.asStateFlow()
//
//    private val currentUserId: StateFlow<Long> = authRepository.getCurrentUserFlow()
//        .map { it?.id ?: 0L }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)
//
//    val posts: StateFlow<List<Post>> = _currentCategory.flatMapLatest { cat ->
//        val userId = currentUserId.value
//        when (cat) {
//            "all" -> forumRepository.getAllPosts(userId)
//            "featured" -> forumRepository.getFeaturedPosts(userId)
//            else -> forumRepository.getPostsByCategory(cat, userId)
//        }
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
//
//    // Trạng thái cho tìm kiếm (filter local)
//    private val _searchQuery = MutableStateFlow("")
//    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
//
//    val filteredPosts: StateFlow<List<Post>> = combine(posts, _searchQuery) { list, query ->
//        if (query.isBlank()) list else list.filter { it.tieuDe.contains(query, ignoreCase = true) || it.noiDung.contains(query, ignoreCase = true) }
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
//
//    // Cho màn chi tiết post
//    private val _selectedPost = MutableStateFlow<Post?>(null)
//    val selectedPost: StateFlow<Post?> = _selectedPost.asStateFlow()
//
//    val comments: StateFlow<List<Comment>> = _selectedPost.flatMapLatest { post ->
//        if (post != null) forumRepository.getCommentsForPost(post.id) else flowOf(emptyList())
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
//
//    private val _error = MutableStateFlow<String?>(null)
//    val error: StateFlow<String?> = _error.asStateFlow()
//
//    fun setCategory(category: String) {
//        _currentCategory.value = category
//    }
//
//    fun onSearchQueryChange(query: String) {
//        _searchQuery.value = query
//    }
//
//    fun loadPostDetail(postId: Long) {
//        viewModelScope.launch {
//            val postEntity = forumRepository.getPostById(postId)
//            if (postEntity != null) {
//                val likeCount = forumRepository.getLikeCount(postId).firstOrNull() ?: 0
//                val isLiked = forumRepository.toggleLike(postId, currentUserId.value)  // Không, chỉ load
//                _selectedPost.value = postEntity.toPost(likeCount, isLiked)
//            } else {
//                _error.value = "Không tìm thấy bài viết"
//            }
//        }
//    }
//
//    fun createPost(tieuDe: String, noiDung: String, category: String, filePath: String?) {
//        viewModelScope.launch {
//            if (tieuDe.isBlank() || noiDung.isBlank()) {
//                _error.value = "Tiêu đề và nội dung không được để trống"
//                return@launch
//            }
//            val userId = currentUserId.value
//            if (userId == 0L) {
//                _error.value = "Vui lòng đăng nhập để đăng bài"
//                return@launch
//            }
//            val newPost = PostEntity(
//                tac_gia_id = userId,
//                tieu_de = tieuDe,
//                noi_dung = noiDung,
//                file_dinh_kem = filePath,
//                category = category,
//                trang_thai = "da_duyet"  // Giả định duyệt tự động cho demo
//            )
//            forumRepository.createPost(newPost)
//            _error.value = null  // Success
//        }
//    }
//
//    fun addComment(postId: Long, noiDung: String) {
//        viewModelScope.launch {
//            if (noiDung.isBlank()) return@launch
//            val userId = currentUserId.value
//            if (userId == 0L) {
//                _error.value = "Vui lòng đăng nhập để bình luận"
//                return@launch
//            }
//            val newComment = CommentEntity(
//                bai_viet_id = postId,
//                tac_gia_id = userId,
//                noi_dung = noiDung
//            )
//            forumRepository.addComment(newComment)
//        }
//    }
//
//    fun toggleLike(postId: Long) {
//        viewModelScope.launch {
//            val userId = currentUserId.value
//            if (userId == 0L) {
//                _error.value = "Vui lòng đăng nhập để thích"
//                return@launch
//            }
//            forumRepository.toggleLike(postId, userId)
//            // Flow sẽ tự update posts
//        }
//    }
//
//    fun clearError() {
//        _error.value = null
//    }
//}