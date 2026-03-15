package com.nikoniche.booki.architecture.book_details_views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nikoniche.booki.MyDivider
import com.nikoniche.booki.architecture.navigation.Screen
import com.nikoniche.booki.book_search.UserBookViewModel
import com.nikoniche.booki.personalData.PersonalRecordsViewModel
import com.nikoniche.booki.Book
import com.nikoniche.booki.PersonalBook
import com.nikoniche.booki.Status

@Composable
fun BookView(
    book: Book,
    navHostController: NavHostController,
    personalRecordsViewModel: PersonalRecordsViewModel,
    userBookViewModel: UserBookViewModel,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GenericBookData(
            book=book,
        )

        MyDivider(Modifier.padding(vertical=8.dp))

        personalRecordsViewModel.setViewedBookByBook(book)
        var bookStatusState by remember {
            mutableStateOf(personalRecordsViewModel.viewedPersonalBook.value?.status) // null status state means
            // that its not added to users personal books
        }

        // personal data
        Column(
            modifier = Modifier.fillMaxSize()
        )
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                var dropDownMenuExpandedState by remember {
                    mutableStateOf(false)
                }

                Button(
                    onClick = {
                        dropDownMenuExpandedState = !dropDownMenuExpandedState
                    },
                    colors = ButtonDefaults
                        .buttonColors(
                            containerColor= Color.White,
                            contentColor= Color.Black
                        ),
                    border = BorderStroke(2.5.dp, Color.Black),
                    modifier = Modifier.height(30.dp),
                    shape = ButtonDefaults.filledTonalShape,
                    contentPadding = PaddingValues(horizontal=10.dp, vertical=1.dp),
                )
                {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text=bookStatusState?.inText ?: "Not read",
                            color=bookStatusState?.color ?: Color.Black,
                        )
                        Icon(Icons.Default.KeyboardArrowDown, null)
                    }

                    DropdownMenu(
                        modifier = Modifier
                            .background(Color.White)
                            .border(1.dp, Color.Black)
                            .wrapContentWidth(),
                        expanded = dropDownMenuExpandedState,
                        onDismissRequest = {
                            dropDownMenuExpandedState = false
                        }) {
                        @Composable
                        fun BookStatusSelection(status: com.nikoniche.booki.Status) {
                            DropdownMenuItem(
                                text = {
                                    Text(status.inText, color=status.color)
                                },
                                onClick = {
                                    if (personalRecordsViewModel.viewedPersonalBook.value == null) {
                                        personalRecordsViewModel.addBook(
                                            com.nikoniche.booki.PersonalBook(
                                                book = book,
                                                status = status, // assuming we arent trying to delete non-existent book
                                                readPages = if (status == com.nikoniche.booki.Status.Finished) book.numberOfPages else 0
                                            )
                                        )
                                    } else {
                                        when (status) {
                                            null -> personalRecordsViewModel.removeBook(
                                                personalRecordsViewModel.viewedPersonalBook.value!!
                                            )
                                            else -> personalRecordsViewModel.viewedPersonalBook.value!!.status = status
                                        }
                                        if (status == com.nikoniche.booki.Status.Finished) {
                                            personalRecordsViewModel.viewedPersonalBook.value!!.readPages = personalRecordsViewModel.viewedPersonalBook.value!!.book.numberOfPages
                                        }
                                        personalRecordsViewModel.updateBook(personalRecordsViewModel.viewedPersonalBook.value!!)
                                    }
                                    bookStatusState = status
                                    dropDownMenuExpandedState = false
                                }
                            )
                        }
                        BookStatusSelection(com.nikoniche.booki.Status.PlanToRead)
                        BookStatusSelection(com.nikoniche.booki.Status.Reading)
                        BookStatusSelection(com.nikoniche.booki.Status.Finished)
                        BookStatusSelection(com.nikoniche.booki.Status.Dropped)
                    }
                }

                if (personalRecordsViewModel.viewedPersonalBook.value != null) {
                    if (bookStatusState == com.nikoniche.booki.Status.Reading || bookStatusState == com.nikoniche.booki.Status.Finished || bookStatusState == com.nikoniche.booki.Status.Dropped) {
                        EditableReadPages(
                            personalRecordsViewModel = personalRecordsViewModel,
                        )
                    }
                }
                Row(
                    modifier=Modifier.wrapContentSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {

                    // navigation button to update screen for user created books
                    if(book.source == "User") {
                        IconButton(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(end = 8.dp),
                            onClick = {
                                userBookViewModel.triggerBookEdit(book)
                                navHostController.navigate(Screen.AddBookManuallyScreen.route)
                            }
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "edit user book button",
                            )
                        }
                    }

                    // deleting a book from personal books
                    if (personalRecordsViewModel.viewedPersonalBook.value != null) {
                        if(bookStatusState != null) {
                            IconButton(
                                onClick={
                                    personalRecordsViewModel.removeBook(personalRecordsViewModel.viewedPersonalBook.value!!)
                                    bookStatusState = null
                                }
                            ) {
                                Icon(Icons.Default.Delete, "delete button")
                            }
                        }
                    }
                }
            }

            if (personalRecordsViewModel.viewedPersonalBook.value != null) {
                if(bookStatusState == com.nikoniche.booki.Status.Finished || bookStatusState == com.nikoniche.booki.Status.Dropped) {
                    EditableReview(
                        personalBook = personalRecordsViewModel.viewedPersonalBook.value!!,
                        personalRecordsViewModel = personalRecordsViewModel
                    )
                }

                if(bookStatusState == com.nikoniche.booki.Status.Reading || bookStatusState == com.nikoniche.booki.Status.Finished) {
                    Spacer(Modifier.height(12.dp))
                    EditableBookNotes(
                        personalBook = personalRecordsViewModel.viewedPersonalBook.value!!,
                        personalRecordsViewModel = personalRecordsViewModel,
                    )
                }
            }
        }
    }
}

@Composable
fun GenericBookData(
    book: com.nikoniche.booki.Book,
) {
    // universal slash public book data
    Column(
        modifier = Modifier
            .wrapContentSize()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = book.getCoverPainter(),
//            painter=painterResource(R.drawable.nineteen_eighty_four),
            contentDescription = "book cover",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(200.dp)
                .width(150.dp)
                .padding(vertical = 8.dp),
        )

        Text(
            text =book.title,
            fontWeight= FontWeight.Bold,
            fontSize = 21.sp,
        )
        if (book.subtitle != "") {
            Text(
                text=book.subtitle,
                fontWeight=FontWeight.Normal,
                textAlign = TextAlign.Center,
                fontSize =17.sp,
            )
        }
        Text(
            text=book.getAuthors(),
            fontStyle= FontStyle.Italic,
            fontSize=16.sp,
        )

        Spacer(Modifier.height(4.dp))
        val detailsText: String = "" +
                "${book.numberOfPages} pages\n" +
                "ISBN: ${book.getISBN()}\n" +
                (if (book.publisher != "") "publisher: ${book.publisher}\n" else "") +
                (if (book.publishDate != "") "published: ${book.publishDate}\n" else "") +
                (if (book.language != "") "language: ${book.language}\n" else "")
        Text(
            text=detailsText,
            fontSize=13.sp,
            color= Color.Gray
        )

//        if (book.genres.isNotEmpty()) {
//            LazyVerticalGrid(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(50.dp),
//                columns = GridCells.Fixed(4),
//                horizontalArrangement = Arrangement.spacedBy(4.dp),  // Space between columns
//                verticalArrangement = Arrangement.spacedBy(4.dp),
//                contentPadding = PaddingValues(horizontal = 8.dp)
//            ) {
//                items(book.genres) {
//                    Box(
//                        modifier = Modifier
//                            .border(1.dp, Color.Black, RoundedCornerShape(5.dp))
//                            .height(20.dp)
//                            .width(40.dp),
//                        contentAlignment = Alignment.Center,
//                    ) {
//                        Text(
//                            text=it,
//                            fontWeight = FontWeight.Light,
//                            fontSize = 13.sp,
//                            maxLines = 1,                             // Ensure text does not wrap to the next line
//                            overflow = TextOverflow.Ellipsis,
//                        )
//                    }
//                }
//            }
//        }
    }
}

@Preview(showBackground=true)
@Composable
fun GenericBookDataPreview() {
    GenericBookData(book = com.nikoniche.booki.Book(
        title = "Testing title",
        authors = listOf("Kafka", "Orwell"),
        subtitle = "THERE is but one truly serious philosophical problem and that is the queistion of suicide",
        isbn10 = "9879999999999",
        numberOfPages = 201,
        publisher = "ARTUR",
        publishDate = "14. 2. 2004",
        language = "English",
        genres = listOf("Fantasy", "Drama", "Romance", "Drama", "Romance", "Drama", "Romance"),
        source = "User",
    )
    )
}