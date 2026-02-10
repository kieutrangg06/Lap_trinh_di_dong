package com.example.matestudy.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.matestudy.data.dao.UserDao
import com.example.matestudy.data.dao.PostDao  // Mới
import com.example.matestudy.data.dao.CommentDao  // Mới
import com.example.matestudy.data.dao.LikeDao  // Mới
import com.example.matestudy.data.entity.UserEntity
import com.example.matestudy.data.entity.PostEntity  // Mới
import com.example.matestudy.data.entity.CommentEntity  // Mới
import com.example.matestudy.data.entity.LikeEntity  // Mới
import android.content.Context
import androidx.room.Room

@Database(
    entities = [UserEntity::class, PostEntity::class, CommentEntity::class, LikeEntity::class],
    version = 2,  // Tăng version vì thêm bảng mới
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao  // Mới
    abstract fun commentDao(): CommentDao  // Mới
    abstract fun likeDao(): LikeDao  // Mới

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
                    .fallbackToDestructiveMigration()  // Chỉ dev, sau thay migration
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}