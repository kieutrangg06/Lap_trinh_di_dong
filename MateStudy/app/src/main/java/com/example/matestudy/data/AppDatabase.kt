package com.example.matestudy.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.matestudy.data.dao.UserDao
import com.example.matestudy.data.entity.UserEntity
import android.content.Context
import androidx.room.Room

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "matestudy_db"
                )
                    .fallbackToDestructiveMigration() // chỉ dev, sau này thay bằng migration
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}