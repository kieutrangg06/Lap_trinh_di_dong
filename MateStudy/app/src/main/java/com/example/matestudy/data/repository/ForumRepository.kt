package com.example.matestudy.data.repository

import com.example.matestudy.data.Post
import com.example.matestudy.data.entity.*
import com.example.matestudy.data.remote.FirestoreDataSource
import com.example.matestudy.data.toPost
import kotlinx.coroutines.flow.Flow
import com.example.matestudy.data.Comment
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import com.example.matestudy.data.toComment

class ForumRepository(private val firestore: FirestoreDataSource) {

    fun getAllPosts(userId: Long): Flow<List<Post>> = firestore.getAllPosts().map { posts ->
        posts.map { postEntity ->
            val likeCount = firestore.getLikeCount(postEntity.id).firstOrNull() ?: 0
            val isLiked = firestore.getLikeByUser(postEntity.id, userId)!= null
            postEntity.toPost(likeCount, isLiked)
        }
    }

    fun getPostsByCategory(category: String, userId: Long): Flow<List<Post>> =
        firestore.getPostsByCategory(category).map { posts ->
            posts.map { postEntity ->
                val likeCount = firestore.getLikeCount(postEntity.id).firstOrNull() ?: 0
                val isLiked = firestore.getLikeByUser(postEntity.id, userId) != null
                postEntity.toPost(likeCount, isLiked)
            }
        }

    fun getFeaturedPosts(userId: Long): Flow<List<Post>> = firestore.getFeaturedPosts().map { featured ->
        featured.map { postWithLike ->
            val isLiked = firestore.getLikeByUser(postWithLike.id, userId) != null
            postWithLike.toPost(isLiked)
        }
    }

    suspend fun createPost(post: PostEntity): Long = firestore.insertPost(post)

    suspend fun getPostById(id: Long): PostEntity? = firestore.getPostById(id)

    fun getCommentsForPost(postId: Long): Flow<List<Comment>> =
        firestore.getCommentsForPost(postId).map { list ->
            list.map { it.toComment() }
        }

    suspend fun addComment(comment: CommentEntity) {
        firestore.insertComment(comment)
    }

    fun getLikeCount(postId: Long): Flow<Int> = firestore.getLikeCount(postId)

    suspend fun toggleLike(postId: Long, userId: Long): Boolean {
        val existing = firestore.getLikeByUser(postId, userId)
        return if (existing != null) {
            firestore.deleteLike(existing)
            false // unliked
        } else {
            firestore.insertLike(LikeEntity(postId, userId))
            true // liked
        }
    }

    // ForumRepository.kt
    suspend fun isPostLiked(postId: Long, userId: Long): Boolean {
        if (userId == 0L) return false
        return firestore.getLikeByUser(postId, userId) != null
    }
}