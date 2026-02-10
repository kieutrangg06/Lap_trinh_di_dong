//package com.example.matestudy.data.repository
//
//import com.example.matestudy.data.AppDatabase
//import com.example.matestudy.data.Post
//import com.example.matestudy.data.Comment
//import com.example.matestudy.data.entity.PostEntity
//import com.example.matestudy.data.entity.CommentEntity
//import com.example.matestudy.data.entity.LikeEntity
//import com.example.matestudy.data.toPost
//import com.example.matestudy.data.toComment
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.firstOrNull
//import kotlinx.coroutines.flow.map
//
//class ForumRepository(private val db: AppDatabase) {
//    private val postDao = db.postDao()
//    private val commentDao = db.commentDao()
//    private val likeDao = db.likeDao()
//
//    fun getAllPosts(userId: Long): Flow<List<Post>> = postDao.getAllPosts().map { posts ->
//        posts.map { postEntity ->
//            val likeCount = likeDao.getLikeCount(postEntity.id).firstOrNull() ?: 0  // Sync chờ
//            val isLiked = likeDao.getLikeByUser(postEntity.id, userId) != null
//            postEntity.toPost(likeCount, isLiked)
//        }
//    }
//
//    fun getPostsByCategory(category: String, userId: Long): Flow<List<Post>> = postDao.getPostsByCategory(category).map { posts ->
//        posts.map { postEntity ->
//            val likeCount = likeDao.getLikeCount(postEntity.id).firstOrNull() ?: 0
//            val isLiked = likeDao.getLikeByUser(postEntity.id, userId) != null
//            postEntity.toPost(likeCount, isLiked)
//        }
//    }
//
//    fun getFeaturedPosts(userId: Long): Flow<List<Post>> = postDao.getFeaturedPosts().map { posts ->
//        posts.map { postWithLike ->
//            val isLiked = likeDao.getLikeByUser(postWithLike.id, userId) != null
//            postWithLike.toPost(isLiked)
//        }
//    }
//
//    suspend fun createPost(post: PostEntity): Long = postDao.insertPost(post)
//
//    suspend fun getPostById(id: Long): PostEntity? = postDao.getPostById(id)
//
//    fun getCommentsForPost(postId: Long): Flow<List<Comment>> = commentDao.getCommentsForPost(postId).map { it.map { entity -> entity.toComment() } }
//
//    suspend fun addComment(comment: CommentEntity) = commentDao.insertComment(comment)
//
//    fun getLikeCount(postId: Long): Flow<Int> = likeDao.getLikeCount(postId)
//
//    suspend fun toggleLike(postId: Long, userId: Long): Boolean {
//        val existingLike = likeDao.getLikeByUser(postId, userId)
//        return if (existingLike != null) {
//            likeDao.deleteLike(existingLike)
//            false  // Unliked
//        } else {
//            likeDao.insertLike(LikeEntity(postId, userId))
//            true  // Liked
//        }
//    }
//}