package com.example.matestudy.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.matestudy.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM nguoi_dung WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM nguoi_dung LIMIT 1") // giả sử chỉ có 1 user đăng nhập cùng lúc
    fun getCurrentUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM nguoi_dung WHERE tenDangNhap = :username LIMIT 1")
    suspend fun findByUsername(username: String): UserEntity?

    @Query("SELECT * FROM nguoi_dung WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("DELETE FROM nguoi_dung")
    suspend fun deleteAll()
}