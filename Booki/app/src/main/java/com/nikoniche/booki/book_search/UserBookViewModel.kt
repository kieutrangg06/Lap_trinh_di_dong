package com.nikoniche.booki.book_search

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikoniche.booki.Book
import com.nikoniche.booki.personalData.local_database.DataGraph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserBookViewModel : ViewModel() {
    private val repo = DataGraph.firebaseRepository

    private val _userBooks: MutableState<List<Book>> = mutableStateOf(emptyList())
    val userBooks: State<List<Book>> = _userBooks

    private val _userBookToEdit: MutableState<Book?> = mutableStateOf(null)
    val userBookToEdit: State<Book?> = _userBookToEdit

    init {
        fetchSavedBooks()
    }

    fun fetchSavedBooks() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fetchedBooks = repo.getUserBooks()
                withContext(Dispatchers.Main) {
                    _userBooks.value = fetchedBooks

                    // Kiểm tra xem sách đang edit còn tồn tại không
                    if (_userBookToEdit.value != null) {
                        val exists = fetchedBooks.any { it.id == _userBookToEdit.value?.id }
                        if (!exists) _userBookToEdit.value = null
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addBook(book: Book) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addUserBook(book)
            fetchSavedBooks()
        }
    }

    fun updateBook(book: Book) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addUserBook(book) // Firebase ghi đè lên ID cũ để update
            fetchSavedBooks()
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch(Dispatchers.IO) {
            // Lưu ý: Cần thêm hàm deleteUserBook vào FirebaseRepository nếu chưa có
            // db.child(userId).child("user_created_books").child(book.id.toString()).removeValue()
            fetchSavedBooks()
        }
    }

    fun triggerBookEdit(userBook: Book) {
        _userBookToEdit.value = userBook
    }
}