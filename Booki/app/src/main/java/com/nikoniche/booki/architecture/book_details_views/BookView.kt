package com.nikoniche.booki.architecture.book_details_views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
            mutableStateOf(personalRecordsViewModel.viewedPersonalBook.value?.status)
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
                                                status = status,
                                                readPages = if (status == com.nikoniche.booki.Status.Finished) book.numberOfPages else 0
                                            )
                                        )
                                    } else {
                                        val currentBook = personalRecordsViewModel.viewedPersonalBook.value!!
                                        currentBook.status = status

                                        if (status == com.nikoniche.booki.Status.Finished) {
                                            currentBook.readPages = currentBook.book.numberOfPages
                                        }
                                        personalRecordsViewModel.updateBook(currentBook)
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
    Column(
        modifier = Modifier
            .wrapContentSize()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = book.getCoverPainter(),
            contentDescription = "book cover",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(200.dp)
                .width(150.dp)
                .padding(vertical = 8.dp),
        )

        Text(
            text = book.title,
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

        // SỬA TẠI ĐÂY: Dùng authorsText thay cho getAuthors()
        Text(
            text=book.authorsText,
            fontStyle= FontStyle.Italic,
            fontSize=16.sp,
        )

        Spacer(Modifier.height(4.dp))

        // SỬA TẠI ĐÂY: Dùng displayISBN thay cho getISBN()
        val detailsText: String = "" +
                "${book.numberOfPages} pages\n" +
                "ISBN: ${book.displayISBN}\n" +
                (if (book.publisher != "") "publisher: ${book.publisher}\n" else "") +
                (if (book.publishDate != "") "published: ${book.publishDate}\n" else "") +
                (if (book.language != "") "language: ${book.language}\n" else "")
        Text(
            text=detailsText,
            fontSize=13.sp,
            color= Color.Gray
        )
    }
}