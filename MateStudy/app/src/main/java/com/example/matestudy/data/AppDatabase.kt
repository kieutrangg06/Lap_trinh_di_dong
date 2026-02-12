package com.example.matestudy.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.matestudy.data.dao.*
import com.example.matestudy.data.entity.*
import android.content.Context
import androidx.room.Room

@Database(
    entities = [UserEntity::class, PostEntity::class, CommentEntity::class, LikeEntity::class,
        HocKyEntity::class, MonHocEntity::class, LichCaNhanEntity::class, SkCaNhanEntity::class],
    version = 3, // Tăng từ 2 lên 3
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun likeDao(): LikeDao
    abstract fun hocKyDao(): HocKyDao // Mới
    abstract fun monHocDao(): MonHocDao // Mới
    abstract fun lichCaNhanDao(): LichCaNhanDao // Mới
    abstract fun skCaNhanDao(): SkCaNhanDao // Mới

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "matestudy_db"
                ).fallbackToDestructiveMigration() // Dev only
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}