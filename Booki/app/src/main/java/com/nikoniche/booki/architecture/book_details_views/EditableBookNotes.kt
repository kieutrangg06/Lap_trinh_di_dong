package com.nikoniche.booki.architecture.book_details_views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nikoniche.booki.MyHeadline
import com.nikoniche.booki.PersonalBook
import com.nikoniche.booki.personalData.PersonalRecordsViewModel

@Composable
fun EditableBookNotes(
    personalBook: com.nikoniche.booki.PersonalBook,
    personalRecordsViewModel: PersonalRecordsViewModel,
) {
    var writtenNotes by remember {
        mutableStateOf(personalBook.bookNotes)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        MyHeadline(text = "Book notes")
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            colors= OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor= Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                focusedLabelColor = Color.Black,
            ),
            value = writtenNotes,
            onValueChange = {
                writtenNotes = it
                personalBook.bookNotes = writtenNotes
                personalRecordsViewModel.updateBook(personalBook)
            }
        )
    }
}