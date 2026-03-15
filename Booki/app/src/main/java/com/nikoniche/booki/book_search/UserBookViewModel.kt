package com.nikoniche.booki.book_search

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikoniche.booki.Book
import com.nikoniche.booki.personalData.local_database.Graph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserBookViewModel: ViewModel() {
    private val userBookRepository = Graph.userBookRepository

    private val _userBooks: MutableState<List<Book>> = mutableStateOf(emptyList())
    val userBooks: State<List<Book>> = _userBooks

    private val _userBookToEdit: MutableState<Book?> = mutableStateOf(null)
    val userBookToEdit: State<Book?> = _userBookToEdit

    private fun fetchSavedBooks() {
        viewModelScope.launch(Dispatchers.IO) {
            val fetchedBooks = userBookRepository.getUserBooks()

            withContext(Dispatchers.Main) {
                _userBooks.value = fetchedBooks

                // Check if userBookToEdit still exists
                val foundEditBook = fetchedBooks.any { it == _userBookToEdit.value }
                if (!foundEditBook) {
                    _userBookToEdit.value = null
                }
            }
        }
    }

    init {
        fetchSavedBooks()
    }

    fun addBook(book: Book) {
        viewModelScope.launch(Dispatchers.IO) {
            userBookRepository.addUserBook(book)
            fetchSavedBooks()
        }
    }

    fun updateBook(book: Book) {
        viewModelScope.launch(Dispatchers.IO) {
            userBookRepository.updateUserBook(book)
            fetchSavedBooks()
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch(Dispatchers.IO) {
            userBookRepository.deleteUserBook(book)
            fetchSavedBooks()
        }
    }

    fun triggerBookEdit(userBook: Book) {
        _userBookToEdit.value = userBook
    }
}