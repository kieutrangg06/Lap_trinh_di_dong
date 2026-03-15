package com.nikoniche.booki.personalData.local_database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserBookDao {
    // user books
    @Insert(onConflict= OnConflictStrategy.ABORT)
    abstract suspend fun addUserBook(userBookEntity: UserBookEntity)
    @Query("Select * from `books_created_by_user-table`")
    abstract fun getAllUserBooks(): Flow<List<UserBookEntity>>
    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun updateUserBook(userBookEntity: UserBookEntity)
    @Delete
    abstract suspend fun deleteUserBook(userBookEntity: UserBookEntity)
    @Query("Select * from `books_created_by_user-table` where id=:id")
    abstract fun getUserBookById(id: Long): Flow<UserBookEntity>
}