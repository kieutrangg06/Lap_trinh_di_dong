package com.nikoniche.booki.personalData.local_database

import com.nikoniche.booki.PersonalBook
import kotlinx.coroutines.flow.Flow
import com.nikoniche.booki.Book
import com.nikoniche.booki.Status
import com.google.gson.Gson
import kotlinx.coroutines.flow.first

class PersonalBookRepository(
    private val personalBookDao: PersonalBookDao
) {
    private fun convertPersonalBookToEntity(personalBook: com.nikoniche.booki.PersonalBook): PersonalBookEntity {
        var personalBookEntity = PersonalBookEntity(
            bookString=personalBook.book.textify(),
            statusId=personalBook.status.id,
            pageProgress = personalBook.readPages,
            rating=personalBook.rating,
            review=personalBook.review,
            bookNotes=personalBook.bookNotes,
        )

        // mozna vyresilo to, ze neuklada spravne data
        // protoze pri vytvoreni nove book, se podle me zapsalo defaultni id
        // protoze se tim zabranilo aby se automaticky vytvorilo primaryKey
        if (personalBook.id != -1L) {
            personalBookEntity = personalBookEntity.copy(
                id=personalBook.id
            )
        }

        return personalBookEntity
    }

    suspend fun addPersonalBook(personalBook: com.nikoniche.booki.PersonalBook){
        personalBookDao.addPersonalBook(convertPersonalBookToEntity(personalBook))
    }

    suspend fun getPersonalBooks(): List<com.nikoniche.booki.PersonalBook> {
        val personalBookEntities = personalBookDao.getAllPersonalBooks().first()
        return personalBookEntities.map { entity ->
            com.nikoniche.booki.PersonalBook(
                id = entity.id,
                book = Gson().fromJson(entity.bookString, com.nikoniche.booki.Book::class.java),
                status = when (entity.statusId) {
                    0 -> com.nikoniche.booki.Status.PlanToRead
                    1 -> com.nikoniche.booki.Status.Reading
                    2 -> com.nikoniche.booki.Status.Finished
                    3 -> com.nikoniche.booki.Status.Dropped
                    else -> com.nikoniche.booki.Status.PlanToRead
                },
                readPages = entity.pageProgress,
                rating = entity.rating,
                review = entity.review,
                bookNotes = entity.bookNotes
            )
        }
    }

    fun getPersonalBookEntityById(id: Long): Flow<PersonalBookEntity> {
        return personalBookDao.getPersonalBookById(id)
    }

    suspend fun updatePersonalBook(personalBook: com.nikoniche.booki.PersonalBook) {
        personalBookDao.updatePersonalBook(convertPersonalBookToEntity(personalBook))
    }

    suspend fun deletePersonalBook(personalBook: com.nikoniche.booki.PersonalBook) {
        personalBookDao.deletePersonalBook(convertPersonalBookToEntity(personalBook))
    }
}