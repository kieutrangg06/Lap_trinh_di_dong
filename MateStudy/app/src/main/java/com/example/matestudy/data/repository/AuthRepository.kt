package com.example.matestudy.data.repository

import com.example.matestudy.data.entity.UserEntity
import com.example.matestudy.data.remote.FirestoreDataSource
import kotlinx.coroutines.flow.Flow

class AuthRepository(private val firestore: FirestoreDataSource) {

    // ────────────────────────────────────────────────
    // 1. Trạng thái người dùng hiện tại (thường dùng nhất)
    // ────────────────────────────────────────────────

    fun getCurrentUserFlow(): Flow<UserEntity?> = firestore.getCurrentUserFlow()

    // ────────────────────────────────────────────────
    // 2. Đăng nhập / Thiết lập phiên
    // ────────────────────────────────────────────────

    suspend fun loginToSession(user: UserEntity) {
        firestore.setCurrentSession(user)
    }

    // ────────────────────────────────────────────────
    // 3. Đăng xuất
    // ────────────────────────────────────────────────

    suspend fun logout() {
        firestore.clearSession()
    }

    // ────────────────────────────────────────────────
    // 4. Tạo / Cập nhật thông tin người dùng
    // ────────────────────────────────────────────────

    suspend fun saveUser(user: UserEntity) {
        firestore.saveUser(user)
    }

    suspend fun updateUser(user: UserEntity) {
        firestore.updateUser(user)
    }

    // ────────────────────────────────────────────────
    // 5. Tìm kiếm người dùng (dùng khi đăng ký / đăng nhập)
    // ────────────────────────────────────────────────

    suspend fun findUserByUsernameOrEmail(identifier: String): UserEntity? {
        return firestore.findByUsername(identifier) ?: firestore.findByEmail(identifier)
    }

    fun getAllUsersForAdmin(): Flow<List<UserEntity>> = firestore.getAllUsersForAdmin()

    suspend fun toggleUserLock(userId: Long, currentStatus: String) {
        val newStatus = if (currentStatus == "hoat_dong") "bi_khoa" else "hoat_dong"
        firestore.updateUserStatus(userId, newStatus)
    }

    suspend fun deleteUser(userId: Long) {
        firestore.deleteUser(userId)
    }
}