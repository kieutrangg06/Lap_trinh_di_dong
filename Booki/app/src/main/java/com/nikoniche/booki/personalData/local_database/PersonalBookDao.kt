package com.nikoniche.booki.personalData.local_database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PersonalBookDao {
    // personal books
    @Insert(onConflict=OnConflictStrategy.ABORT)
    abstract suspend fun addPersonalBook(personalBookEntity: PersonalBookEntity)
    @Query("Select * from `personal_books-table`")
    abstract fun getAllPersonalBooks(): Flow<List<PersonalBookEntity>>
    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun updatePersonalBook(personalBookEntity: PersonalBookEntity)
    @Delete
    abstract suspend fun deletePersonalBook(personalBookEntity: PersonalBookEntity)
    @Query("Select * from `personal_books-table` where id=:id")
    abstract fun getPersonalBookById(id: Long): Flow<PersonalBookEntity>
}