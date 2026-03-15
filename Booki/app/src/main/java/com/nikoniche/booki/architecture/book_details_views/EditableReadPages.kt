package com.nikoniche.booki.architecture.book_details_views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nikoniche.booki.PersonalBook
import com.nikoniche.booki.Status
import com.nikoniche.booki.personalData.PersonalRecordsViewModel

@Composable
fun EditableReadPages(
    personalRecordsViewModel: PersonalRecordsViewModel
) {
    val trackedBook: com.nikoniche.booki.PersonalBook = personalRecordsViewModel.viewedPersonalBook.value!!
    val writtenPages = remember {
        derivedStateOf {
            trackedBook.readPages
        }
    }
    var userInput by remember {
        mutableStateOf(writtenPages.value.toString())
    }

    LaunchedEffect(trackedBook.readPages) {
        userInput = trackedBook.readPages.toString()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        BasicTextField(
            singleLine=true,
            textStyle = TextStyle(
                textAlign= TextAlign.End,
                fontSize = 16.sp,
            ),
            modifier= Modifier
                .width(40.dp)
                .offset(y=0.75.dp)
                .padding(end=2.dp),
            value = userInput,
            onValueChange = {
                userInput = it
                val formattedPages = it.toIntOrNull()
                if (formattedPages != null) {
                    if (formattedPages <= trackedBook.book.numberOfPages) {
                        trackedBook.readPages = formattedPages
                        if (trackedBook.readPages == trackedBook.book.numberOfPages) {
                            trackedBook.status = com.nikoniche.booki.Status.Finished
                        }
                        personalRecordsViewModel.updateBook(trackedBook)
                    }
                }
            }
        )

        Text(
            text = "/ ${personalRecordsViewModel.viewedPersonalBook.value!!.book.numberOfPages}",
            color = Color.Black,
            fontSize = 16.sp,
            modifier = Modifier
        )
    }
}
