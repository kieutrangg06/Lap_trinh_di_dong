package com.example.matestudy.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import com.example.matestudy.data.entity.PostEntity
import com.example.matestudy.data.entity.CommentEntity
import com.example.matestudy.data.entity.LikeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Insert
    suspend fun insertPost(post: PostEntity): Long  // Trả id mới

    @Query("SELECT * FROM bai_viet_dien_dan WHERE trang_thai = 'da_duyet' ORDER BY ngay_dang DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM bai_viet_dien_dan WHERE category = :category AND trang_thai = 'da_duyet' ORDER BY ngay_dang DESC")
    fun getPostsByCategory(category: String): Flow<List<PostEntity>>

    // Nổi bật: Sắp xếp theo like count
    @Query("""
        SELECT p.*, COUNT(l.bai_viet_id) as likeCount 
        FROM bai_viet_dien_dan p 
        LEFT JOIN luot_thich_bai_viet l ON p.id = l.bai_viet_id 
        WHERE p.trang_thai = 'da_duyet' 
        GROUP BY p.id 
        ORDER BY likeCount DESC, p.ngay_dang DESC
    """)
    fun getFeaturedPosts(): Flow<List<PostWithLikeCount>>  // Định nghĩa class bên dưới

    @Query("SELECT * FROM bai_viet_dien_dan WHERE id = :id")
    suspend fun getPostById(id: Long): PostEntity?
}

data class PostWithLikeCount(  // Class phụ cho query
    val id: Long,
    val tac_gia_id: Long,
    val tieu_de: String,
    val noi_dung: String,
    val file_dinh_kem: String?,
    val trang_thai: String,
    val ngay_dang: Long?,
    val category: String,
    val likeCount: Int
)

@Dao
interface CommentDao {
    @Insert
    suspend fun insertComment(comment: CommentEntity)

    @Query("SELECT * FROM binh_luan_dien_dan WHERE bai_viet_id = :postId ORDER BY ngay_tao DESC")
    fun getCommentsForPost(postId: Long): Flow<List<CommentEntity>>
}

@Dao
interface LikeDao {
    @Insert
    suspend fun insertLike(like: LikeEntity)

    @Delete
    suspend fun deleteLike(like: LikeEntity)

    @Query("SELECT COUNT(*) FROM luot_thich_bai_viet WHERE bai_viet_id = :postId")
    fun getLikeCount(postId: Long): Flow<Int>

    @Query("SELECT * FROM luot_thich_bai_viet WHERE bai_viet_id = :postId AND sinh_vien_id = :userId")
    suspend fun getLikeByUser(postId: Long, userId: Long): LikeEntity?
}