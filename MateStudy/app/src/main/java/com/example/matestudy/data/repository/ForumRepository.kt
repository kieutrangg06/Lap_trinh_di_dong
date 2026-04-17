package com.example.matestudy.data.repository

import com.example.matestudy.data.Comment
import com.example.matestudy.data.Post
import com.example.matestudy.data.entity.*
import com.example.matestudy.data.remote.FirestoreDataSource
import com.example.matestudy.data.toPost
import com.example.matestudy.data.toComment
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.firstOrNull

class ForumRepository(
    private val firestore: FirestoreDataSource,
    private val thongBaoRepo: ThongBaoRepository
) {

    // ────────────────────────────────────────────────
    // 1. Lấy bài viết + Tác giả + Avatar
    // ────────────────────────────────────────────────

    suspend fun getPostWithAuthor(postId: Long, currentUserId: Long): Post? {
        val entity = firestore.getPostById(postId) ?: return null
        val likeCount = firestore.getLikeCount(postId).firstOrNull() ?: 0
        val isLiked = firestore.getLikeByUser(postId, currentUserId) != null
        val author = firestore.getUserById(entity.tac_gia_id)

        return entity.toPost(
            likeCount = likeCount,
            isLiked = isLiked,
            tacGiaTen = author?.tenDangNhap ?: "Sinh viên ${entity.tac_gia_id}",
            tacGiaAvatar = author?.anhDaiDien
        )
    }

    fun getAllPosts(userId: Long): Flow<List<Post>> =
        firestore.getAllPosts().mapLatest { posts ->
            posts.map { postEntity ->
                val likeCount = firestore.getLikeCount(postEntity.id).firstOrNull() ?: 0
                val isLiked = firestore.getLikeByUser(postEntity.id, userId) != null
                val author = firestore.getUserById(postEntity.tac_gia_id)

                postEntity.toPost(
                    likeCount = likeCount,
                    isLiked = isLiked,
                    tacGiaTen = author?.tenDangNhap ?: "Sinh viên ${postEntity.tac_gia_id}",
                    tacGiaAvatar = author?.anhDaiDien
                )
            }
        }

    fun getPostsByCategory(category: String, userId: Long): Flow<List<Post>> =
        firestore.getPostsByCategory(category).mapLatest { posts ->
            posts.map { postEntity ->
                val likeCount = firestore.getLikeCount(postEntity.id).firstOrNull() ?: 0
                val isLiked = firestore.getLikeByUser(postEntity.id, userId) != null
                val author = firestore.getUserById(postEntity.tac_gia_id)

                postEntity.toPost(
                    likeCount = likeCount,
                    isLiked = isLiked,
                    tacGiaTen = author?.tenDangNhap ?: "Sinh viên ${postEntity.tac_gia_id}",
                    tacGiaAvatar = author?.anhDaiDien
                )
            }
        }

    fun getFeaturedPosts(userId: Long): Flow<List<Post>> =
        firestore.getFeaturedPosts().mapLatest { featured ->
            featured.map { postWithLike ->
                val isLiked = firestore.getLikeByUser(postWithLike.id, userId) != null
                val author = firestore.getUserById(postWithLike.tac_gia_id)

                postWithLike.toPost(
                    isLiked = isLiked,
                    tacGiaTen = author?.tenDangNhap ?: "Sinh viên ${postWithLike.tac_gia_id}",
                    tacGiaAvatar = author?.anhDaiDien
                )
            }
        }

    // ────────────────────────────────────────────────
    // 2. Bình luận + Avatar của người bình luận
    // ────────────────────────────────────────────────

    fun getCommentsForPost(postId: Long): Flow<List<Comment>> =
        firestore.getCommentsForPost(postId).mapLatest { list ->
            list.map { commentEntity ->
                val author = firestore.getUserById(commentEntity.tac_gia_id)

                commentEntity.toComment(
                    tacGiaTen = author?.tenDangNhap ?: "Sinh viên ${commentEntity.tac_gia_id}",
                    tacGiaAvatar = author?.anhDaiDien
                )
            }
        }

    // ────────────────────────────────────────────────
    // 3. Dành cho Admin
    // ────────────────────────────────────────────────

    fun getAllPostsForAdmin(): Flow<List<Post>> =
        firestore.getAllPostsForAdmin().mapLatest { entities ->
            entities.map { entity ->
                val author = firestore.getUserById(entity.tac_gia_id)

                entity.toPost(
                    likeCount = 0,
                    isLiked = false,
                    tacGiaTen = author?.tenDangNhap ?: "Sinh viên ${entity.tac_gia_id}",
                    tacGiaAvatar = author?.anhDaiDien
                )
            }
        }

    // ────────────────────────────────────────────────
    // 4. Các hàm khác
    // ────────────────────────────────────────────────

    suspend fun createPost(post: PostEntity): Long = firestore.insertPost(post)

    suspend fun addComment(comment: CommentEntity) {
        firestore.insertComment(comment)
        val post = firestore.getPostById(comment.bai_viet_id)
        if (post != null && post.tac_gia_id != comment.tac_gia_id) {
            thongBaoRepo.createThongBao(
                ThongBaoEntity(
                    sinhVienId = post.tac_gia_id,
                    tieuDe = "Bình luận mới",
                    noiDung = "Ai đó đã bình luận vào bài viết '${post.tieu_de}' của bạn.",
                    loai = "bai_viet",
                    idLienQuan = post.id,
                    loaiLienQuan = "comment"
                )
            )
        }
    }

    suspend fun toggleLike(postId: Long, userId: Long): Boolean {
        val existing = firestore.getLikeByUser(postId, userId)
        val post = firestore.getPostById(postId)

        return if (existing != null) {
            firestore.deleteLike(existing)
            false
        } else {
            firestore.insertLike(LikeEntity(postId, userId))
            if (post != null && post.tac_gia_id != userId) {
                thongBaoRepo.createThongBao(
                    ThongBaoEntity(
                        sinhVienId = post.tac_gia_id,
                        tieuDe = "Lượt thích mới",
                        noiDung = "Ai đó đã thích bài viết '${post.tieu_de}' của bạn.",
                        loai = "bai_viet",
                        idLienQuan = post.id,
                        loaiLienQuan = "like"
                    )
                )
            }
            true
        }
    }

    suspend fun approvePostWithNotify(postId: Long) {
        firestore.updatePostStatus(postId, "da_duyet")
        val post = firestore.getPostById(postId)
        post?.let {
            thongBaoRepo.createThongBao(
                ThongBaoEntity(
                    sinhVienId = it.tac_gia_id,
                    tieuDe = "Bài viết đã được duyệt",
                    noiDung = "Chúc mừng! Bài viết '${it.tieu_de}' của bạn đã công khai.",
                    loai = "bai_viet",
                    idLienQuan = it.id
                )
            )
        }
    }

    suspend fun hidePost(postId: Long) {
        firestore.updatePostStatus(postId, "bi_an")
    }

    suspend fun deletePost(postId: Long) {
        firestore.deletePost(postId)
    }
}