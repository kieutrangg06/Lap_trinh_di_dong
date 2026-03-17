package com.example.matestudy.data.remote

import com.example.matestudy.data.dao.PostWithLikeCount
import com.example.matestudy.data.entity.*
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import java.util.*

class FirestoreDataSource {

    private val db: FirebaseFirestore = Firebase.firestore

    init {
        try {
            db.firestoreSettings = firestoreSettings {
                isPersistenceEnabled = true
            }
        } catch (_: Exception) {
            // đã bật hoặc lỗi khác
        }
    }

    // USER
    suspend fun saveUser(user: UserEntity) {
        db.collection("users").document(user.id.toString()).set(user).await()
    }

    suspend fun updateUser(user: UserEntity) {
        db.collection("users").document(user.id.toString()).set(user).await()
    }

    suspend fun getUserById(id: Long): UserEntity? {
        return db.collection("users").document(id.toString()).get().await()
            .toObject(UserEntity::class.java)
    }

    fun getCurrentUserFlow(): Flow<UserEntity?> = callbackFlow {
        val listener = db.collection("users")
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val user = snapshot?.documents?.firstOrNull()?.toObject(UserEntity::class.java)
                trySend(user)
            }
        awaitClose { listener.remove() }
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

    suspend fun deleteAllUsers() {
        val batch = db.batch()
        db.collection("users").get().await().documents.forEach { doc ->
            batch.delete(doc.reference)
        }
        batch.commit().await()
    }

    // POST / FORUM
    suspend fun insertPost(post: PostEntity): Long {
        val ref = db.collection("bai_viet_dien_dan").document()
        val generatedId = ref.id.hashCode().toLong().coerceAtLeast(1L)
        ref.set(post.copy(id = generatedId)).await()
        return generatedId
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
                val posts = snap?.toObjects(PostEntity::class.java) ?: emptyList()

                // Tính like count client-side (không realtime hoàn hảo)
                val featured = posts.map { post ->
                    val likeCount = runCatching {
                        db.collection("luot_thich_bai_viet")
                            .whereEqualTo("bai_viet_id", post.id)
                            .get()
                            .result
                            ?.size() ?: 0
                    }.getOrDefault(0)

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
                }.sortedWith(
                    compareByDescending<PostWithLikeCount> { it.likeCount }
                        .thenByDescending { it.ngay_dang }
                )

                trySend(featured)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getPostById(id: Long): PostEntity? {
        return db.collection("bai_viet_dien_dan").document(id.toString()).get().await()
            .toObject(PostEntity::class.java)
    }

    suspend fun insertComment(comment: CommentEntity) {
        db.collection("binh_luan_dien_dan").add(comment).await()
    }

    fun getCommentsForPost(postId: Long): Flow<List<CommentEntity>> = callbackFlow {
        val listener = db.collection("binh_luan_dien_dan")
            .whereEqualTo("bai_viet_id", postId)
            .orderBy("ngay_tao", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, e ->
                if (e != null) close(e) else trySend(snap?.toObjects(CommentEntity::class.java) ?: emptyList())
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

    fun getLikeCount(postId: Long): Flow<Int> = callbackFlow {
        val listener = db.collection("luot_thich_bai_viet")
            .whereEqualTo("bai_viet_id", postId)
            .addSnapshotListener { snap, e ->
                if (e != null) close(e) else trySend(snap?.size() ?: 0)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getLikeByUser(postId: Long, userId: Long): LikeEntity? {
        val docId = "${postId}_$userId"
        return db.collection("luot_thich_bai_viet").document(docId).get().await()
            .toObject(LikeEntity::class.java)
    }

    // REVIEW
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

    // SCHEDULE
    suspend fun insertHocKy(hocKy: HocKyEntity) {
        val ref = db.collection("hoc_ky").document()
        val newId = ref.id.hashCode().toLong().coerceAtLeast(1L)
        ref.set(hocKy.copy(id = newId)).await()
    }

    fun getAllHocKy(): Flow<List<HocKyEntity>> = callbackFlow {
        val listener = db.collection("hoc_ky")
            .addSnapshotListener { snap, e ->
                if (e != null) close(e) else trySend(snap?.toObjects(HocKyEntity::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    suspend fun insertMonHoc(monHoc: MonHocEntity): Long {
        val ref = db.collection("mon_hoc").document()
        val newId = ref.id.hashCode().toLong().coerceAtLeast(1L)
        ref.set(monHoc.copy(id = newId)).await()
        return newId
    }

    fun getMonHocByHocKy(hocKyId: Long): Flow<List<MonHocEntity>> = callbackFlow {
        val listener = db.collection("mon_hoc")
            .whereEqualTo("hoc_ky_id", hocKyId)
            .addSnapshotListener { snap, e ->
                if (e != null) close(e) else trySend(snap?.toObjects(MonHocEntity::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    suspend fun getMonHocById(id: Long): MonHocEntity? {
        return db.collection("mon_hoc").document(id.toString()).get().await()
            .toObject(MonHocEntity::class.java)
    }

    suspend fun insertLich(lich: LichCaNhanEntity): Long {
        val ref = db.collection("lich_ca_nhan").document()
        val newId = ref.id.hashCode().toLong().coerceAtLeast(1L)
        ref.set(lich.copy(id = newId)).await()
        return newId
    }

    fun getLichBySinhVien(sinhVienId: Long): Flow<List<LichCaNhanEntity>> = callbackFlow {
        val listener = db.collection("lich_ca_nhan")
            .whereEqualTo("sinh_vien_id", sinhVienId)
            .addSnapshotListener { snap, e ->
                if (e != null) close(e) else trySend(snap?.toObjects(LichCaNhanEntity::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    suspend fun getLichById(id: Long): LichCaNhanEntity? {
        return db.collection("lich_ca_nhan").document(id.toString()).get().await()
            .toObject(LichCaNhanEntity::class.java)
    }

    suspend fun updateLich(lich: LichCaNhanEntity) {
        db.collection("lich_ca_nhan").document(lich.id.toString()).set(lich).await()
    }

    suspend fun deleteLich(lich: LichCaNhanEntity) {
        db.collection("lich_ca_nhan").document(lich.id.toString()).delete().await()
    }

    suspend fun insertSk(sk: SkCaNhanEntity): Long {
        val ref = db.collection("sk_ca_nhan").document()
        val newId = ref.id.hashCode().toLong().coerceAtLeast(1L)
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

    // ------------------ THÔNG BÁO ------------------

    suspend fun insertThongBao(thongBao: ThongBaoEntity) {
        val ref = db.collection("thong_bao").document()
        val generatedId = ref.id.hashCode().toLong().coerceAtLeast(1L)
        ref.set(thongBao.copy(id = generatedId)).await()
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

    suspend fun markAsRead(thongBaoId: Long) {
        db.collection("thong_bao").document(thongBaoId.toString())
            .update("daDoc", true)
            .await()
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