package com.nikoniche.booki.personalData.local_database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PersonalBookEntity::class, UserBookEntity::class],
    version = 4,
    exportSchema = false
)
abstract class BookiDatabase : RoomDatabase() {
    abstract fun personalBookDao(): PersonalBookDao
    abstract fun userBookDao(): UserBookDao
}