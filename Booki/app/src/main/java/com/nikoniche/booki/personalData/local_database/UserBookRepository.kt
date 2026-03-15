package com.nikoniche.booki.personalData.local_database

import kotlinx.coroutines.flow.Flow
import com.nikoniche.booki.Book
import com.google.gson.Gson
import kotlinx.coroutines.flow.first

class UserBookRepository(
    private val userBookDao: UserBookDao
) {
    private fun convertBookToUserBookEntity(book: com.nikoniche.booki.Book): UserBookEntity {
        return if (book.id == -1L) {
            UserBookEntity(
                bookString = Gson().toJson(book),
            )
        } else {
            UserBookEntity(
                id=book.id,
                bookString = Gson().toJson(book),
            )
        }
    }
    suspend fun addUserBook(book: com.nikoniche.booki.Book){
        userBookDao.addUserBook(convertBookToUserBookEntity(book))
        println("saving ${book.title} w ${book.coverUrl}")
    }

    suspend fun getUserBooks(): List<com.nikoniche.booki.Book> {
        val userBookEntities = userBookDao.getAllUserBooks().first()
        return userBookEntities.map {
            entity ->
            val newBook = Gson().fromJson(entity.bookString, com.nikoniche.booki.Book::class.java)
            newBook.id = entity.id
            println("loaded ${newBook.title} w ${newBook.coverUrl}")
            newBook
        }
    }

    fun getUserBookEntityById(id: Long): Flow<UserBookEntity> {
        return userBookDao.getUserBookById(id)
    }

    suspend fun updateUserBook(book: com.nikoniche.booki.Book) {
        userBookDao.updateUserBook(convertBookToUserBookEntity(book))
    }

    suspend fun deleteUserBook(book: com.nikoniche.booki.Book) {
        userBookDao.deleteUserBook(convertBookToUserBookEntity(book))
    }
}