package com.nikoniche.booki.personalData.local_database

import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.room.Room

object Graph {
    lateinit var database: BookiDatabase

    val personalBookRepository by lazy {
        PersonalBookRepository(personalBookDao = database.personalBookDao())
    }
    val userBookRepository by lazy {
        UserBookRepository(userBookDao = database.userBookDao())
    }

    fun provide(context: Context) {
        try {
            database = Room.databaseBuilder(
                context = context,
                klass = BookiDatabase::class.java,
                name = "personal_books.db",
            )
//                .fallbackToDestructiveMigration() // delete before production -> resetting DB on schema change
                .build()
            println("database provided")
        } catch (e: SQLiteException) {
            println("database failed")
        }

    }
}