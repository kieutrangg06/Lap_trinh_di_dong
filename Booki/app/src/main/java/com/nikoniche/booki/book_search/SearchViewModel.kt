package com.nikoniche.booki.book_search

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikoniche.booki.Book
import com.nikoniche.booki.book_search.apis.openLibrary.OpenLibrary
import com.nikoniche.booki.personalData.local_database.DataGraph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel : ViewModel() {
    data class Search(
        val searching: Boolean = true,
        val result: List<Book> = emptyList(), // Sử dụng List thay cho MutableList để quản lý State tốt hơn
        val error: String = "",
    )

    private val firebaseRepository = DataGraph.firebaseRepository

    private val _search = mutableStateOf(Search())
    val search: State<Search> = _search

    private fun extendResultsList(listToAdd: List<Book>) {
        // Tạo một danh sách mới kết hợp kết quả hiện tại và kết quả mới
        val currentList = _search.value.result.toMutableList()
        currentList.addAll(listToAdd)

        _search.value = _search.value.copy(
            result = currentList.toList()
        )
    }

    fun fetchSearchResults(query: String) {
        // Reset trạng thái về ban đầu khi bắt đầu tìm kiếm mới
        _search.value = Search(searching = true, result = emptyList(), error = "")

        val queryAsIsbnString = query.replace("-", "").replace(" ", "").trim()
        var searchByISBN = false

        if (queryAsIsbnString.length == 10 || queryAsIsbnString.length == 13) {
            if (queryAsIsbnString.toLongOrNull() != null) {
                searchByISBN = true
            }
        }

        if (searchByISBN) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    // 1. Lấy dữ liệu từ OpenLibrary API
                    val apiResults = OpenLibrary.getBookByISBN(queryAsIsbnString)
                    withContext(Dispatchers.Main) {
                        extendResultsList(apiResults)
                    }

                    // 2. Lấy dữ liệu từ Firebase cá nhân
                    val userBooks = firebaseRepository.getUserBooks()

                    // SỬA TẠI ĐÂY: Dùng displayISBN thay vì getISBN()
                    val matchingUserBooks = userBooks.filter { it.displayISBN == queryAsIsbnString }

                    withContext(Dispatchers.Main) {
                        extendResultsList(matchingUserBooks)
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _search.value = _search.value.copy(
                            error = "Error: ${e.message ?: "Unknown error"}"
                        )
                    }
                } finally {
                    withContext(Dispatchers.Main) {
                        _search.value = _search.value.copy(searching = false)
                    }
                }
            }
        } else {
            _search.value = _search.value.copy(searching = false)
            println("Not searching by ISBN")
        }
    }

    fun fetchUserBook(userBook: Book) {
        _search.value = Search(
            result = listOf(userBook),
            searching = false
        )
    }
}