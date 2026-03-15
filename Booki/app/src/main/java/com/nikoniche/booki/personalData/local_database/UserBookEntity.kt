package com.nikoniche.booki.personalData.local_database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="books_created_by_user-table")
data class UserBookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long=0L,
    @ColumnInfo("bookString")
    val bookString: String,
)