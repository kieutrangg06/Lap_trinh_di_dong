package com.example.matestudy.data.repository

import com.example.matestudy.data.AppDatabase
import com.example.matestudy.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

class AuthRepository(private val db: AppDatabase) {

    private val userDao = db.userDao()

    suspend fun saveUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    fun getCurrentUserFlow(): Flow<UserEntity?> = userDao.getCurrentUserFlow()

    suspend fun findUserByUsernameOrEmail(identifier: String): UserEntity? {
        return userDao.findByUsername(identifier) ?: userDao.findByEmail(identifier)
    }

    suspend fun clearUserData() {
        userDao.deleteAll()
    }
}