package com.nikoniche.booki.personalData.local_database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="personal_books-table")
data class PersonalBookEntity(
    @PrimaryKey(autoGenerate=true)
    val id: Long=0L,

    @ColumnInfo("bookString")
    val bookString: String,
    @ColumnInfo("statusId")
    val statusId: Int,
    @ColumnInfo("pageProgress")
    val pageProgress: Int,
    @ColumnInfo("review")
    val review: String,
    @ColumnInfo("rating")
    val rating: Int,
    @ColumnInfo("bookNotes")
    val bookNotes: String,
)