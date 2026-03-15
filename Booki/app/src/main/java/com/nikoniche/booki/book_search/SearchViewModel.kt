package com.nikoniche.booki.book_search

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikoniche.booki.Book
import com.nikoniche.booki.book_search.apis.openLibrary.OpenLibrary
import com.nikoniche.booki.personalData.local_database.Graph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel: ViewModel() {
    data class Search(
        var searching: Boolean = true,
        var result: MutableList<Book> = mutableListOf(),
        var error: String="",
    )

    private val userBookRepository = Graph.userBookRepository

    private val _search = mutableStateOf(Search())
    val search: State<Search> = _search

    private fun extendResultsList(listToAdd: List<Book>) {
        val extendedList = _search.value.result
        extendedList.addAll(listToAdd)
        _search.value = _search.value.copy(
            result=extendedList
        )
    }

    fun fetchSearchResults(query: String) {
        // notify the UI that the search is ongoing
        _search.value = Search()

        // determining whether or not the query was an ISBN
        val queryAsIsbnString = query.replace("-", "").replace(" ", "").trim()
        var searchByISBN = false
        if (queryAsIsbnString.length == 10 || queryAsIsbnString.length == 13) {
            if (queryAsIsbnString.toLongOrNull() != null) {
                searchByISBN = true
            }
        }

        if (searchByISBN) {
            // search by isbn
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    extendResultsList(
                        OpenLibrary.getBookByISBN(queryAsIsbnString)
                    )

                    // disabled search from google books since they would charge
//                    extendResultsList(
//                        GoogleBooks.getBookByISBN(queryAsIsbnString)
//                    )

                    // search user books
                    val userBooks = userBookRepository.getUserBooks()

                    val matchingUserBooks: MutableList<Book> = mutableListOf()
                    userBooks.forEach {
                        if (it.getISBN() == queryAsIsbnString) {
                            matchingUserBooks.add(it)
                        }
                    }
                    extendResultsList(
                        matchingUserBooks
                    )
                } catch (e: Exception) {
                    // failed
                    _search.value = _search.value.copy(
                        error="error: ${e}, message: ${e.message ?: "null message"}",
                    )
                } finally {
                    _search.value = _search.value.copy(
                        searching=false,
                    )
                }
            }
        } else {
            println("Not searchign by ISBN")
        }
    }

    fun fetchUserBook(userBook: Book) {
        _search.value = Search()
        _search.value = _search.value.copy(
            result=mutableListOf(userBook),
            searching = false,
        )
    }
}