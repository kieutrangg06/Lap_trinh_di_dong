package com.nikoniche.booki.architecture.book_details_views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nikoniche.booki.Book
import com.nikoniche.booki.book_search.SearchViewModel
import com.nikoniche.booki.book_search.UserBookViewModel
import com.nikoniche.booki.personalData.PersonalRecordsViewModel

@Composable
fun BookDetailsView(
    bookIsbn: String,
    navHostController: NavHostController,
    personalRecordsViewModel: PersonalRecordsViewModel,
    userBookViewModel: UserBookViewModel,
    searchViewModel: SearchViewModel,
) {
    if(!searchViewModel.search.value.searching) {
        var foundBook: Book? = null
        searchViewModel.search.value.result.forEach {
            book ->
            if(book.getISBN() == bookIsbn) {
                foundBook = book
            }
        }
        if(foundBook != null) {
            BookView(
                book=foundBook as Book,
                personalRecordsViewModel=personalRecordsViewModel,
                navHostController = navHostController,
                userBookViewModel=userBookViewModel,
            )
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 20.dp),
            )
            {
                Text("Error during search: ${searchViewModel.search.value.error}")
            }
        }
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp),
        )
        {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                color = Color.Black,
                backgroundColor = Color.LightGray,
            )
        }
    }
}
