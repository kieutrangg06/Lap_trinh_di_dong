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

    fun getCurrentUserFlow(): Flow<UserEntity?> = firestore.getCurrentUserFlow()

    suspend fun findUserByUsernameOrEmail(identifier: String): UserEntity? {
        return firestore.findByUsername(identifier) ?: firestore.findByEmail(identifier)
    }

    suspend fun clearUserData() {
        firestore.deleteAllUsers()
    }
}