package com.example.matestudy.data.remote

import com.example.matestudy.data.dao.PostWithLikeCount
import com.example.matestudy.data.entity.*
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirestoreDataSource {

    private val db: FirebaseFirestore = Firebase.firestore
    private val SESSION_ID = "current_session"

    init {
        try {
            db.firestoreSettings = firestoreSettings {
                isPersistenceEnabled = true
            }
        } catch (_: Exception) {
        }
    }

    // ────────────────────────────────────────────────
    // 1. USER - Quản lý người dùng & phiên đăng nhập
    // ────────────────────────────────────────────────

    fun getCurrentUserFlow(): Flow<UserEntity?> = callbackFlow {
        val listener = db.collection("sessions").document(SESSION_ID)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val user = snapshot?.toObject(UserEntity::class.java)
                trySend(user)
            }
        awaitClose { listener.remove() }
    }

    suspend fun setCurrentSession(user: UserEntity) {
        db.collection("sessions").document(SESSION_ID).set(user).await()
    }

    suspend fun clearSession() {
        db.collection("sessions").document(SESSION_ID).delete().await()
    }

    suspend fun findByUsername(username: String): UserEntity? {
        val snap = db.collection("users")
            .whereEqualTo("tenDangNhap", username)
            .limit(1)
            .get().await()
        return snap.documents.firstOrNull()?.toObject(UserEntity::class.java)
    }

    suspend fun findByEmail(email: String): UserEntity? {
        val snap = db.collection("users")
            .whereEqualTo("email", email)
            .limit(1)
            .get().await()
        return snap.documents.firstOrNull()?.toObject(UserEntity::class.java)
    }

    fun getAllUsersForAdmin(): Flow<List<UserEntity>> = callbackFlow {
        val listener = db.collection("users")
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                trySend(snap?.toObjects(UserEntity::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateUserStatus(userId: Long, newStatus: String) {
        db.collection("users").document(userId.toString())
            .update("trangThai", newStatus)
            .await()
    }

    suspend fun deleteUser(userId: Long) {
        db.collection("users").document(userId.toString()).delete().await()
    }

    suspend fun getUserById(userId: Long): UserEntity? {
        return db.collection("users")
            .document(userId.toString())
            .get()
            .await()
            .toObject(UserEntity::class.java)
    }

    fun getUserByIdFlow(userId: Long): Flow<UserEntity?> = callbackFlow {
        val listener = db.collection("users")
            .document(userId.toString())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(UserEntity::class.java))
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveUser(user: UserEntity) {
        db.collection("users")
            .document(user.id.toString())
            .set(user)
            .await()
    }

    suspend fun updateUser(user: UserEntity) {
        if (user.id == 0L) {
            throw IllegalArgumentException("User ID không được là 0 khi update")
        }
        db.collection("users")
            .document(user.id.toString())
            .set(user, SetOptions.merge())
            .await()
    }

    suspend fun updateUserFields(userId: Long, fields: Map<String, Any?>) {
        db.collection("users")
            .document(userId.toString())
            .update(fields)
            .await()
    }

    // ────────────────────────────────────────────────
    // 2. POST & FORUM - Bài viết, bình luận, lượt thích
    // ────────────────────────────────────────────────

    suspend fun insertPost(post: PostEntity): Long {
        val generatedId = System.nanoTime()
        val ref = db.collection("bai_viet_dien_dan").document(generatedId.toString())
        ref.set(post.copy(id = generatedId)).await()
        return generatedId
    }

    suspend fun getPostById(id: Long): PostEntity? {
        return db.collection("bai_viet_dien_dan")
            .whereEqualTo("id", id)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()
            ?.toObject(PostEntity::class.java)
    }

    fun getAllPosts(): Flow<List<PostEntity>> = callbackFlow {
        val listener = db.collection("bai_viet_dien_dan")
            .whereEqualTo("trang_thai", "da_duyet")
            .orderBy("ngay_dang", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                trySend(snap?.toObjects(PostEntity::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    fun getPostsByCategory(category: String): Flow<List<PostEntity>> = callbackFlow {
        val listener = db.collection("bai_viet_dien_dan")
            .whereEqualTo("category", category)
            .whereEqualTo("trang_thai", "da_duyet")
            .orderBy("ngay_dang", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, e ->
                if (e != null) close(e) else trySend(snap?.toObjects(PostEntity::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    fun getFeaturedPosts(): Flow<List<PostWithLikeCount>> = callbackFlow {
        val listener = db.collection("bai_viet_dien_dan")
            .whereEqualTo("trang_thai", "da_duyet")
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }

                val postEntities = snap?.toObjects(PostEntity::class.java) ?: emptyList()

                launch {
                    try {
                        val deferredPosts = postEntities.map { post ->
                            async(Dispatchers.IO) {
                                val likesSnap = db.collection("luot_thich_bai_viet")
                                    .whereEqualTo("bai_viet_id", post.id)
                                    .get()
                                    .await()

                                val likeCount = likesSnap.size()

                                PostWithLikeCount(
                                    id = post.id,
                                    tac_gia_id = post.tac_gia_id,
                                    tieu_de = post.tieu_de,
                                    noi_dung = post.noi_dung,
                                    file_dinh_kem = post.file_dinh_kem,
                                    trang_thai = post.trang_thai,
                                    ngay_dang = post.ngay_dang,
                                    category = post.category,
                                    likeCount = likeCount
                                )
                            }
                        }

                        val featured = deferredPosts.awaitAll()
                        val sortedFeatured = featured.sortedWith(
                            compareByDescending<PostWithLikeCount> { it.likeCount }
                                .thenByDescending { it.ngay_dang }
                        )
                        trySend(sortedFeatured)

                    } catch (ioException: Exception) {
                        trySend(emptyList())
                    }
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun insertComment(comment: CommentEntity) {
        val generatedId = System.nanoTime()
        db.collection("binh_luan_dien_dan").document(generatedId.toString())
            .set(comment.copy(id = generatedId))
            .await()
    }

    fun getCommentsForPost(postId: Long): Flow<List<CommentEntity>> = callbackFlow {
        val listener = db.collection("binh_luan_dien_dan")
            .whereEqualTo("bai_viet_id", postId)
            .orderBy("ngay_tao", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, e ->
                if (e != null) close(e)
                else trySend(snap?.toObjects(CommentEntity::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    suspend fun insertLike(like: LikeEntity) {
        val docId = "${like.bai_viet_id}_${like.sinh_vien_id}"
        db.collection("luot_thich_bai_viet").document(docId).set(like).await()
    }

    suspend fun deleteLike(like: LikeEntity) {
        val docId = "${like.bai_viet_id}_${like.sinh_vien_id}"
        db.collection("luot_thich_bai_viet").document(docId).delete().await()
    }

    suspend fun getLikeByUser(postId: Long, userId: Long): LikeEntity? {
        val docId = "${postId}_$userId"
        return db.collection("luot_thich_bai_viet").document(docId).get().await()
            .toObject(LikeEntity::class.java)
    }

    fun getLikeCount(postId: Long): Flow<Int> = callbackFlow {
        val listener = db.collection("luot_thich_bai_viet")
            .whereEqualTo("bai_viet_id", postId)
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                trySend(snap?.size() ?: 0)
            }
        awaitClose { listener.remove() }
    }

    fun getAllPostsForAdmin(): Flow<List<PostEntity>> = callbackFlow {
        val listener = db.collection("bai_viet_dien_dan")
            .orderBy("ngay_dang", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                trySend(snap?.toObjects(PostEntity::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    suspend fun updatePostStatus(postId: Long, newStatus: String) {
        db.collection("bai_viet_dien_dan")
            .document(postId.toString())
            .update("trang_thai", newStatus)
            .await()
    }

    suspend fun deletePost(postId: Long) {
        db.collection("bai_viet_dien_dan").document(postId.toString()).delete().await()
        val comments = db.collection("binh_luan_dien_dan").whereEqualTo("bai_viet_id", postId).get().await()
        val likes = db.collection("luot_thich_bai_viet").whereEqualTo("bai_viet_id", postId).get().await()

        db.runBatch { batch ->
            comments.forEach { batch.delete(it.reference) }
            likes.forEach { batch.delete(it.reference) }
        }.await()
    }

    // ────────────────────────────────────────────────
    // 3. REVIEW - Đánh giá môn học / giảng viên
    // ────────────────────────────────────────────────

    suspend fun insertReview(review: ReviewEntity) {
        db.collection("danh_gia_mon_giang_vien").add(review).await()
    }

    fun getReviewsByMonHoc(monHocId: Long): Flow<List<ReviewEntity>> = callbackFlow {
        val listener = db.collection("danh_gia_mon_giang_vien")
            .whereEqualTo("mon_hoc_id", monHocId)
            .whereEqualTo("trang_thai", "da_duyet")
            .orderBy("ngay_dang", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, e ->
                if (e != null) close(e) else trySend(snap?.toObjects(ReviewEntity::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    fun getAverageRating(monHocId: Long): Flow<Double?> = callbackFlow {
        val listener = db.collection("danh_gia_mon_giang_vien")
            .whereEqualTo("mon_hoc_id", monHocId)
            .whereEqualTo("trang_thai", "da_duyet")
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val avg = snap?.documents
                    ?.mapNotNull { it.getLong("diem_sao")?.toDouble() }
                    ?.average()
                trySend(if (avg?.isNaN() == true) null else avg)
            }
        awaitClose { listener.remove() }
    }

    fun getReviewCount(monHocId: Long): Flow<Int> = callbackFlow {
        val listener = db.collection("danh_gia_mon_giang_vien")
            .whereEqualTo("mon_hoc_id", monHocId)
            .whereEqualTo("trang_thai", "da_duyet")
            .addSnapshotListener { snap, e ->
                if (e != null) close(e) else trySend(snap?.size() ?: 0)
            }
        awaitClose { listener.remove() }
    }

    fun getAllReviewsForAdmin(): Flow<List<ReviewEntity>> = callbackFlow {
        val listener = db.collection("danh_gia_mon_giang_vien")
            .orderBy("ngay_dang", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                trySend(snap?.toObjects(ReviewEntity::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    fun getAllReviewsForAdminWithUser(): Flow<List<ReviewWithUser>> = callbackFlow {
        val listener = db.collection("danh_gia_mon_giang_vien")
            .orderBy("ngay_dang", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }

                val reviews = snap?.toObjects(ReviewEntity::class.java) ?: emptyList()

                launch {
                    val reviewWithUsers = reviews.mapNotNull { review ->
                        val user = getUserById(review.sinh_vien_id)
                        if (user != null) {
                            ReviewWithUser(
                                review = review,
                                tenDangNhap = user.tenDangNhap ?: "sv${review.sinh_vien_id}"
                            )
                        } else {
                            ReviewWithUser(
                                review = review,
                                tenDangNhap = "sv${review.sinh_vien_id}"
                            )
                        }
                    }
                    trySend(reviewWithUsers)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateReviewStatus(reviewId: String, newStatus: String) {
        val query = db.collection("danh_gia_mon_giang_vien")
            .whereEqualTo("ngay_dang", reviewId.toLongOrNull() ?: 0L)
            .limit(1)
            .get()
            .await()

        query.documents.firstOrNull()?.reference?.update("trang_thai", newStatus)?.await()
    }

    suspend fun deleteReview(ngayDang: Long) {
        val query = db.collection("danh_gia_mon_giang_vien")
            .whereEqualTo("ngay_dang", ngayDang)
            .get()
            .await()
        query.documents.forEach { it.reference.delete().await() }
    }

    // ────────────────────────────────────────────────
    // 4. SCHEDULE - Học kỳ, môn học, lịch cá nhân, sự kiện
    // ────────────────────────────────────────────────

    suspend fun insertHocKy(hocKy: HocKyEntity) {
        val finalId = if (hocKy.id != 0L) hocKy.id else System.nanoTime()
        db.collection("hoc_ky")
            .document(finalId.toString())
            .set(hocKy.copy(id = finalId))
            .await()
    }

    fun getAllHocKy(): Flow<List<HocKyEntity>> = callbackFlow {
        val listener = db.collection("hoc_ky")
            .addSnapshotListener { snap, e ->
                if (e != null) close(e) else trySend(snap?.toObjects(HocKyEntity::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateHocKy(hocKy: HocKyEntity) {
        db.collection("hoc_ky").document(hocKy.id.toString()).set(hocKy).await()
    }

    suspend fun deleteHocKy(hocKyId: Long) {
        db.collection("hoc_ky").document(hocKyId.toString()).delete().await()
        val monHocs = db.collection("mon_hoc").whereEqualTo("hocKyId", hocKyId).get().await()
        db.runBatch { batch ->
            monHocs.forEach { batch.delete(it.reference) }
        }.await()
    }

    suspend fun insertMonHoc(monHoc: MonHocEntity): Long {
        val finalId = if (monHoc.id != 0L) monHoc.id else System.nanoTime()
        db.collection("mon_hoc")
            .document(finalId.toString())
            .set(monHoc.copy(id = finalId))
            .await()
        return finalId
    }

    fun getAllMonHoc(): Flow<List<MonHocEntity>> = callbackFlow {
        val listener = db.collection("mon_hoc")
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val list = snap?.toObjects(MonHocEntity::class.java) ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getMonHocById(id: Long): MonHocEntity? {
        return db.collection("mon_hoc")
            .whereEqualTo("id", id)
            .get()
            .await()
            .documents
            .firstOrNull()
            ?.toObject(MonHocEntity::class.java)
    }

    fun getMonHocByHocKy(hocKyId: Long): Flow<List<MonHocEntity>> = callbackFlow {
        val listener = db.collection("mon_hoc")
            .whereEqualTo("hocKyId", hocKyId)
            .addSnapshotListener { snap, e ->
                if (e != null) close(e) else trySend(snap?.toObjects(MonHocEntity::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateMonHoc(monHoc: MonHocEntity) {
        db.collection("mon_hoc").document(monHoc.id.toString()).set(monHoc).await()
    }

    suspend fun deleteMonHoc(monHocId: Long) {
        db.collection("mon_hoc").document(monHocId.toString()).delete().await()
    }

    suspend fun insertLich(lich: LichCaNhanEntity): Long {
        val newId = System.currentTimeMillis()
        val ref = db.collection("lich_ca_nhan").document(newId.toString())
        ref.set(lich.copy(id = newId)).await()
        return newId
    }

    suspend fun getLichById(id: Long): LichCaNhanEntity? {
        return db.collection("lich_ca_nhan").document(id.toString()).get().await()
            .toObject(LichCaNhanEntity::class.java)
    }

    fun getLichBySinhVien(sinhVienId: Long): Flow<List<LichCaNhanEntity>> = callbackFlow {
        val listener = db.collection("lich_ca_nhan")
            .whereEqualTo("sinhVienId", sinhVienId)
            .addSnapshotListener { snap, e ->
                if (e != null) close(e) else trySend(snap?.toObjects(LichCaNhanEntity::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateLich(lich: LichCaNhanEntity) {
        db.collection("lich_ca_nhan").document(lich.id.toString()).set(lich).await()
    }

    suspend fun deleteLich(lich: LichCaNhanEntity) {
        db.collection("lich_ca_nhan").document(lich.id.toString()).delete().await()
    }

    suspend fun insertSk(sk: SkCaNhanEntity): Long {
        val newId = System.nanoTime()
        val ref = db.collection("sk_ca_nhan").document(newId.toString())
        ref.set(sk.copy(id = newId)).await()
        return newId
    }

    suspend fun getSkById(id: Long): SkCaNhanEntity? {
        return db.collection("sk_ca_nhan").document(id.toString()).get().await()
            .toObject(SkCaNhanEntity::class.java)
    }

    suspend fun updateSk(sk: SkCaNhanEntity) {
        db.collection("sk_ca_nhan").document(sk.id.toString()).set(sk).await()
    }

    suspend fun deleteSk(sk: SkCaNhanEntity) {
        db.collection("sk_ca_nhan").document(sk.id.toString()).delete().await()
    }

    // ────────────────────────────────────────────────
    // 5. THÔNG BÁO - Notification
    // ────────────────────────────────────────────────

    suspend fun insertThongBao(thongBao: ThongBaoEntity) {
        val id = System.nanoTime()
        db.collection("thong_bao")
            .document(id.toString())
            .set(thongBao.copy(id = id))
            .await()
    }

    suspend fun markAsRead(thongBaoId: Long) {
        db.collection("thong_bao")
            .document(thongBaoId.toString())
            .update("daDoc", true)
            .await()
    }

    fun getThongBaoCuaToi(sinhVienId: Long): Flow<List<ThongBaoEntity>> = callbackFlow {
        val listener = db.collection("thong_bao")
            .whereEqualTo("sinhVienId", sinhVienId)
            .orderBy("ngayTao", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                trySend(snap?.toObjects(ThongBaoEntity::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    suspend fun markAllAsRead(sinhVienId: Long) {
        val batch = db.batch()
        db.collection("thong_bao")
            .whereEqualTo("sinhVienId", sinhVienId)
            .whereEqualTo("daDoc", false)
            .get().await().documents.forEach { doc ->
                batch.update(doc.reference, "daDoc", true)
            }
        batch.commit().await()
    }

    suspend fun getUnreadCount(sinhVienId: Long): Int {
        return db.collection("thong_bao")
            .whereEqualTo("sinhVienId", sinhVienId)
            .whereEqualTo("daDoc", false)
            .get().await().size()
    }
}