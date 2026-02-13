package com.example.matestudy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.matestudy.data.dao.*
import com.example.matestudy.data.entity.*

@Database(
    entities = [UserEntity::class, PostEntity::class, CommentEntity::class, LikeEntity::class,
        HocKyEntity::class, MonHocEntity::class, LichCaNhanEntity::class, SkCaNhanEntity::class,
        ReviewEntity::class], // Thêm ReviewEntity
    version = 4, // Tăng từ 3 lên 4
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun likeDao(): LikeDao
    abstract fun hocKyDao(): HocKyDao
    abstract fun monHocDao(): MonHocDao
    abstract fun lichCaNhanDao(): LichCaNhanDao
    abstract fun skCaNhanDao(): SkCaNhanDao
    abstract fun reviewDao(): ReviewDao

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