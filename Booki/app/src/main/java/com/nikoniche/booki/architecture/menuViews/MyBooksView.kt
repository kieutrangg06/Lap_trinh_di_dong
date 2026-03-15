package com.nikoniche.booki.architecture.menuViews

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nikoniche.booki.MyAppColumn
import com.nikoniche.booki.MyDivider
import com.nikoniche.booki.MyHeadline
import com.nikoniche.booki.PersonalBook
import com.nikoniche.booki.book_search.SearchViewModel
import com.nikoniche.booki.Status
import com.nikoniche.booki.personalData.PersonalBookCard
import com.nikoniche.booki.personalData.PersonalRecordsViewModel

@Composable
fun PersonalBooksView(
    navHostController: NavHostController,
    personalRecordsViewModel: PersonalRecordsViewModel,
    searchViewModel: SearchViewModel,
) {
    MyAppColumn(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        RowOfBooks(
            status = Status.Reading,
            navHostController=navHostController,
            personalRecordsViewModel=personalRecordsViewModel,
            searchViewModel = searchViewModel,
        )

        Spacer(Modifier.height(8.dp))
        MyDivider()
        Spacer(Modifier.height(8.dp))

        RowOfBooks(
            status = Status.Finished,
            navHostController=navHostController,
            personalRecordsViewModel=personalRecordsViewModel,
            searchViewModel = searchViewModel,
        )

        Spacer(Modifier.height(8.dp))
        MyDivider()
        Spacer(Modifier.height(8.dp))

        RowOfBooks(
            status = Status.PlanToRead,
            navHostController=navHostController,
            personalRecordsViewModel=personalRecordsViewModel,
            searchViewModel = searchViewModel,
        )

        Spacer(Modifier.height(8.dp))
        MyDivider()
        Spacer(Modifier.height(8.dp))

        RowOfBooks(
            status = Status.Dropped,
            navHostController=navHostController,
            personalRecordsViewModel=personalRecordsViewModel,
            searchViewModel = searchViewModel,
        )
    }
}

@Composable
fun RowOfBooks(
    status: Status,
    navHostController: NavHostController,
    personalRecordsViewModel: PersonalRecordsViewModel,
    searchViewModel: SearchViewModel,
) {
    MyHeadline(text = status.inText)
    Spacer(Modifier.height(8.dp))

    val books: List<PersonalBook> = personalRecordsViewModel.getBooks(status)
    if (books.isNotEmpty()) {
        LazyRow {
            items(books) {
                    personalBook ->
                when (status) {
                    com.nikoniche.booki.Status.PlanToRead -> PersonalBookCard(
                        personalBook = personalBook,
                        navHostController=navHostController,
                        searchViewModel = searchViewModel,
                    )
                    com.nikoniche.booki.Status.Reading -> PersonalBookCard(
                        personalBook = personalBook,
                        showPageProgress = true,
                        navHostController=navHostController,
                        searchViewModel = searchViewModel,
                    )
                    com.nikoniche.booki.Status.Finished -> PersonalBookCard(
                        personalBook = personalBook,
                        showRating = true,
                        navHostController=navHostController,
                        searchViewModel = searchViewModel,
                    )
                    com.nikoniche.booki.Status.Dropped -> PersonalBookCard(
                        personalBook = personalBook,
                        showPageProgress = true,
                        showRating = true,
                        navHostController=navHostController,
                        searchViewModel = searchViewModel,
                    )
                }
            }
        }
    } else {
        Text("You have no books here.")
    }

}
@Preview(showBackground=true)
@Composable
fun PersonalBooksViewPreview() {
    PersonalBooksView(rememberNavController(), viewModel(), viewModel())
}