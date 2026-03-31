package com.nikoniche.booki.personalData

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikoniche.booki.Book
import com.nikoniche.booki.PersonalBook
import com.nikoniche.booki.Status
import com.nikoniche.booki.personalData.local_database.DataGraph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PersonalRecordsViewModel : ViewModel() {
    private val repo = DataGraph.firebaseRepository

    private val _books: MutableState<List<PersonalBook>> = mutableStateOf(emptyList())
    val books: State<List<PersonalBook>> = _books

    val viewedPersonalBook: MutableState<PersonalBook?> = mutableStateOf(null)

    init {
        refreshBooks()
    }

    /**
     * Tải lại danh sách sách từ Firebase
     */
    fun refreshBooks() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fetchedBooks = repo.getPersonalBooks()
                withContext(Dispatchers.Main) {
                    _books.value = fetchedBooks

                    // Cập nhật lại đối tượng đang xem nếu nó tồn tại trong danh sách mới load về
                    viewedPersonalBook.value?.let { current ->
                        viewedPersonalBook.value = fetchedBooks.find { it.id == current.id }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Thêm sách mới vào Personal Books trên Firebase
     */
    fun addBook(personalBook: PersonalBook) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addPersonalBook(personalBook)
            refreshBooks() // Gọi load lại để UI cập nhật ngay lập tức
        }
    }

    /**
     * Cập nhật thông tin sách (trạng thái, số trang đã đọc, ghi chú...)
     */
    fun updateBook(personalBook: PersonalBook) {
        viewModelScope.launch(Dispatchers.IO) {
            // Firebase dùng setValue tại ID cũ sẽ tự động ghi đè (update)
            repo.addPersonalBook(personalBook)
            refreshBooks()
        }
    }

    /**
     * Xóa sách khỏi Personal Books
     */
    fun removeBook(personalBook: PersonalBook) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deletePersonalBook(personalBook)
            refreshBooks()
        }
    }

    /**
     * Tìm kiếm sách trong danh sách cá nhân dựa trên object Book
     */
    fun setViewedBookByBook(book: Book) {
        // SỬA TẠI ĐÂY: Dùng displayISBN thay cho getISBN()
        val found = _books.value.find { it.book.displayISBN == book.displayISBN }
        viewedPersonalBook.value = found
    }

    /**
     * Lọc sách theo trạng thái (Reading, Finished, v.v.)
     */
    fun getBooks(status: Status? = null): List<PersonalBook> {
        return if (status == null) _books.value
        else _books.value.filter { it.status == status }
    }
}