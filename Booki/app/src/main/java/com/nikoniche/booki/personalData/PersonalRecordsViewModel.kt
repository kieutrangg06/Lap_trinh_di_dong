package com.nikoniche.booki.personalData

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikoniche.booki.Book
import com.nikoniche.booki.PersonalBook
import com.nikoniche.booki.Status
import com.nikoniche.booki.personalData.local_database.Graph
import com.nikoniche.booki.personalData.local_database.PersonalBookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PersonalRecordsViewModel: ViewModel() {
    private val personalBookRepository: PersonalBookRepository = Graph.personalBookRepository

    private val _books: MutableState<List<PersonalBook>> = mutableStateOf(emptyList())
    val books: State<List<PersonalBook>> = _books

    val viewedPersonalBook: MutableState<PersonalBook?> = mutableStateOf(null)

    init {
        refreshBooks()
    }

    private fun refreshBooks() {
        viewModelScope.launch(Dispatchers.IO) {
            val listOfSavedBooks = personalBookRepository.getPersonalBooks()

            withContext(Dispatchers.Main) {
                _books.value = listOfSavedBooks

                // Check if viewedPersonalBook still exists
                val foundViewedBook = listOfSavedBooks.any { it == viewedPersonalBook.value }
                if (!foundViewedBook) {
                    viewedPersonalBook.value = null
                }
            }
        }
    }


    fun addBook(personalBook: PersonalBook) {
        viewModelScope.launch(Dispatchers.IO) {
            personalBookRepository.addPersonalBook(personalBook)
            refreshBooks()
        }
    }
    fun updateBook(personalBook: PersonalBook) {
        viewModelScope.launch(Dispatchers.IO) {
            personalBookRepository.updatePersonalBook(personalBook)
            refreshBooks()
//            viewedPersonalBook.value = personalBook
        }
    }
    fun removeBook(
        personalBook: PersonalBook
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            personalBookRepository.deletePersonalBook(personalBook)
            refreshBooks()
        }
    }

    fun setViewedBookByBook(book: Book) {
        var hasBook = false
        _books.value.forEach {
            personalBook ->
            if (personalBook.book.getISBN() == book.getISBN()) {
                viewedPersonalBook.value = personalBook
                hasBook = true
            }
        }
        if (!hasBook) viewedPersonalBook.value = null
    }

    fun getBooks(status: Status?=null): List<PersonalBook> {
        // null status to allow for getting books of all types
        return if (status == null) _books.value
        else {
            val matchingBooks: MutableList<PersonalBook> = mutableListOf()
            _books.value.forEach {
                    book ->
                if (book.status == status) matchingBooks.add(book)
            }
            matchingBooks
        }
    }
}