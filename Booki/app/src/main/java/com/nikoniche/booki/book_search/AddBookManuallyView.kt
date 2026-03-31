package com.nikoniche.booki.book_search

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.nikoniche.booki.Book
import com.nikoniche.booki.R
import com.nikoniche.booki.architecture.navigation.Screen
import com.nikoniche.booki.personalData.PersonalRecordsViewModel

data class TextFieldState(
    val writtenState: MutableState<String> = mutableStateOf(""),
    val onErrorMessage: MutableState<String> = mutableStateOf(""),
    val validityCheck: (TextFieldState) -> Boolean,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyTextField(
    propertyName: String,
    textFieldState: TextFieldState,
    modifier: Modifier = Modifier,
    placeholderText: String = "",
) {
    Column(
        modifier = modifier
            .padding(vertical = 6.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = propertyName,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
        )

        val borderColor = if (textFieldState.onErrorMessage.value != "") Color.Red else Color.Black

        if (textFieldState.onErrorMessage.value != "") {
            Text(
                text = textFieldState.onErrorMessage.value,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        TextField(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                cursorColor = Color.Black,
                unfocusedPlaceholderColor = Color.Gray,
                focusedIndicatorColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedIndicatorColor = borderColor,
                unfocusedTextColor = borderColor,
            ),
            placeholder = { Text(text = placeholderText) },
            singleLine = true,
            textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
            value = textFieldState.writtenState.value,
            onValueChange = { textFieldState.writtenState.value = it }
        )
    }
}

@Composable
fun AddBookManuallyView(
    navHostController: NavHostController,
    personalRecordsViewModel: PersonalRecordsViewModel,
    searchViewModel: SearchViewModel,
    userBookViewModel: UserBookViewModel,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        val pageTitle: String =
            if (userBookViewModel.userBookToEdit.value == null) "Add book manually"
            else "Edit: ${userBookViewModel.userBookToEdit.value!!.title}"

        var imageUri by remember {
            mutableStateOf(
                if (userBookViewModel.userBookToEdit.value == null) null
                else Uri.parse(userBookViewModel.userBookToEdit.value!!.coverUrl)
            )
        }

        var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
        val context = LocalContext.current
        val (launchGalleryPicker, launchCameraPicker) = rememberImagePickerLauncher(
            onImagePicked = { uri ->
                imageUri = uri
                triggerPersistentUriPermission(imageUri!!, context)
            },
            onCameraImageTaken = { bitmap ->
                imageBitmap = bitmap
                imageUri = getImageUri(context, bitmap)
                triggerPersistentUriPermission(imageUri!!, context)
            }
        )

        Text(
            text = pageTitle,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Button(
                modifier = Modifier.width(150.dp).height(200.dp),
                contentPadding = PaddingValues(0.dp),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                onClick = { launchGalleryPicker() }
            ) {
                val painter = if (imageUri == null) painterResource(R.drawable.select_cover_image)
                else rememberAsyncImagePainter(imageUri)
                Image(
                    painter = painter,
                    contentDescription = "select book cover button",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // State cho các trường nhập liệu
        val titleState = remember { TextFieldState { it.writtenState.value.isNotEmpty().also { valid -> if(!valid) it.onErrorMessage.value = "Field can't be empty" } } }
        val subtitleState = remember { TextFieldState { true } }
        val authorsState = remember { TextFieldState { true } }
        val numberOfPagesState = remember { TextFieldState {
            val asInt = it.writtenState.value.toIntOrNull()
            when {
                asInt == null -> { it.onErrorMessage.value = "Invalid number"; false }
                asInt < 1 -> { it.onErrorMessage.value = "Must be > 0"; false }
                else -> true
            }
        } }
        val isbnState = remember { TextFieldState {
            val trimmed = it.writtenState.value.replace("-", "").replace(" ", "").trim()
            if (trimmed.length != 10 && trimmed.length != 13) {
                it.onErrorMessage.value = "Invalid length"; false
            } else if (trimmed.toLongOrNull() == null) {
                it.onErrorMessage.value = "Invalid characters"; false
            } else true
        } }
        val publisherState = remember { TextFieldState { true } }
        val publishDateState = remember { TextFieldState { true } }

        val listOfTextFieldStates = listOf(titleState, subtitleState, authorsState, numberOfPagesState, isbnState, publisherState, publishDateState)

        // Đổ dữ liệu vào nếu là chế độ Edit
        LaunchedEffect(userBookViewModel.userBookToEdit.value) {
            userBookViewModel.userBookToEdit.value?.let { book ->
                titleState.writtenState.value = book.title
                subtitleState.writtenState.value = book.subtitle
                // SỬA TẠI ĐÂY: Dùng authorsText thay cho getAuthors()
                authorsState.writtenState.value = book.authorsText
                numberOfPagesState.writtenState.value = book.numberOfPages.toString()
                // SỬA TẠI ĐÂY: Dùng displayISBN thay cho getISBN()
                isbnState.writtenState.value = book.displayISBN
                publisherState.writtenState.value = book.publisher
                publishDateState.writtenState.value = book.publishDate
            }
        }

        PropertyTextField(propertyName = "Title", textFieldState = titleState)
        PropertyTextField(propertyName = "Subtitle", textFieldState = subtitleState)
        PropertyTextField(propertyName = "Authors", placeholderText = "separate authors with a comma", textFieldState = authorsState)

        Row(modifier = Modifier.fillMaxWidth().wrapContentHeight(), horizontalArrangement = Arrangement.SpaceBetween) {
            PropertyTextField(propertyName = "Number of pages", modifier = Modifier.weight(1f), textFieldState = numberOfPagesState)
            Spacer(Modifier.width(16.dp))
            PropertyTextField(propertyName = "ISBN", modifier = Modifier.weight(1f), textFieldState = isbnState)
        }

        Row(modifier = Modifier.fillMaxWidth().wrapContentHeight(), horizontalArrangement = Arrangement.SpaceBetween) {
            PropertyTextField(propertyName = "Publisher", modifier = Modifier.weight(1f), textFieldState = publisherState)
            Spacer(Modifier.width(16.dp))
            PropertyTextField(propertyName = "Publish date", modifier = Modifier.weight(1f), textFieldState = publishDateState)
        }

        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "you can set your reading progress after adding the book",
                color = Color.DarkGray, fontStyle = FontStyle.Italic, fontSize = 14.sp, modifier = Modifier.width(200.dp),
            )

            if (userBookViewModel.userBookToEdit.value != null) {
                IconButton(onClick = {
                    personalRecordsViewModel.setViewedBookByBook(userBookViewModel.userBookToEdit.value!!)
                    personalRecordsViewModel.viewedPersonalBook.value?.let { personalRecordsViewModel.removeBook(it) }
                    userBookViewModel.deleteBook(userBookViewModel.userBookToEdit.value!!)
                    navHostController.navigate(Screen.BooksCreatedByMeScreen.route)
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "delete book")
                }
            }

            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                border = BorderStroke(2.5.dp, Color.Black),
                shape = ButtonDefaults.filledTonalShape,
                onClick = {
                    val allValid = listOfTextFieldStates.all { it.validityCheck(it) }
                    if (allValid) {
                        val trimmedIsbn = isbnState.writtenState.value.replace("-", "").replace(" ", "").trim()
                        val userBook = Book(
                            id = userBookViewModel.userBookToEdit.value?.id ?: -1L,
                            title = titleState.writtenState.value,
                            subtitle = subtitleState.writtenState.value,
                            authors = authorsState.writtenState.value.split(",").map { it.trim() },
                            numberOfPages = numberOfPagesState.writtenState.value.toIntOrNull() ?: 0,
                            isbn10 = if (trimmedIsbn.length == 10) trimmedIsbn else "",
                            isbn13 = if (trimmedIsbn.length == 13) trimmedIsbn else "",
                            publishDate = publishDateState.writtenState.value,
                            publisher = publisherState.writtenState.value,
                            coverUrl = imageUri.toString(),
                            source = "User",
                        )

                        if (userBookViewModel.userBookToEdit.value == null) {
                            userBookViewModel.addBook(userBook)
                        } else {
                            personalRecordsViewModel.setViewedBookByBook(userBookViewModel.userBookToEdit.value!!)
                            personalRecordsViewModel.viewedPersonalBook.value?.let {
                                personalRecordsViewModel.updateBook(it.copy(book = userBook))
                            }
                            userBookViewModel.updateBook(userBook)
                        }

                        // SỬA TẠI ĐÂY: Dùng displayISBN thay cho getISBN()
                        searchViewModel.fetchSearchResults(userBook.displayISBN)
                        navHostController.navigate(Screen.BookDetailsScreen.route + "/isbn/${userBook.displayISBN}")
                    }
                }
            ) {
                Text(text = if (userBookViewModel.userBookToEdit.value == null) "Add book" else "Edit book", fontSize = 17.sp)
            }
        }
    }
}