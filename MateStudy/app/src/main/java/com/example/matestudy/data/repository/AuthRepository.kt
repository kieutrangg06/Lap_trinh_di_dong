package com.example.matestudy.data.repository

import com.example.matestudy.data.entity.UserEntity
import com.example.matestudy.data.remote.FirestoreDataSource
import kotlinx.coroutines.flow.Flow

class AuthRepository(private val firestore: FirestoreDataSource) {

    suspend fun saveUser(user: UserEntity) {
        firestore.saveUser(user)
    }

    suspend fun updateUser(user: UserEntity) {
        firestore.updateUser(user)
    }

    suspend fun clearUserData() {
        firestore.deleteAllUsers()
    }

    suspend fun loginToSession(user: UserEntity) {
        firestore.setCurrentSession(user)
    }

    // Đăng xuất thì chỉ xóa session
    suspend fun logout() {
        firestore.clearSession()
    }

    fun getCurrentUserFlow(): Flow<UserEntity?> = firestore.getCurrentUserFlow()

    // Tìm user trong DB tổng (không thay đổi)
    suspend fun findUserByUsernameOrEmail(identifier: String): UserEntity? {
        return firestore.findByUsername(identifier) ?: firestore.findByEmail(identifier)
    }
}