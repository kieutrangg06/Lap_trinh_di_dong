package com.nikoniche.booki.architecture.book_details_views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nikoniche.booki.MyHeadline
import com.nikoniche.booki.R
import com.nikoniche.booki.PersonalBook
import com.nikoniche.booki.personalData.PersonalRecordsViewModel

@Composable
fun EditableReview(
    personalBook: com.nikoniche.booki.PersonalBook,
    personalRecordsViewModel: PersonalRecordsViewModel
) {
    var writtenReview by remember {
        mutableStateOf(personalBook.review)
    }

    Spacer(Modifier.height(12.dp))
    MyHeadline(text = "Review")
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Black,
            unfocusedLabelColor = Color.Black,
            focusedLabelColor = Color.Black,
        ),
        value = writtenReview,
        onValueChange = {
            writtenReview = it
            personalBook.review = writtenReview
            personalRecordsViewModel.updateBook(personalBook)
        }
    )

    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.padding(top=8.dp).fillMaxWidth().wrapContentHeight()
    ) {
        EditableRating(
            personalBook=personalBook,
            personalRecordsViewModel=personalRecordsViewModel
        )
    }
}

@Composable
fun EditableRating(
    personalBook: com.nikoniche.booki.PersonalBook,
    personalRecordsViewModel: PersonalRecordsViewModel
) {
    val starSize: Dp = 30.dp
    var newRating by remember {
        mutableIntStateOf(personalBook.rating)
    }

    val amountOfFullStars: Int = newRating.div(2)
    val halfStar: Int = newRating.mod(2)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        var writtenRating by remember {
            mutableStateOf(personalBook.rating.toString())
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Text("Rating: ")
            BasicTextField(
                singleLine=true,
                textStyle = TextStyle(
                    textAlign= TextAlign.End,
                    fontSize = 16.sp,
                ),
                modifier= Modifier
                    .width(25.dp)
                    .offset(y=1.dp)
                    .padding(end=2.dp),
                value = writtenRating,
                onValueChange = {
                    writtenRating = it
                    val formattedRating = writtenRating.toIntOrNull()
                    if (formattedRating != null) {
                        if (formattedRating in 1..10) {
                            newRating = formattedRating
                            personalBook.rating = newRating
                            personalRecordsViewModel.updateBook(personalBook)
                        }
                    }
                }
            )
            Text(
                text = "/ 10",
                color = Color.Black,
                fontSize = 16.sp,
                modifier = Modifier
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (i in 0 until amountOfFullStars) {
                Icon(
                    painter= painterResource(id = R.drawable.star),
                    contentDescription ="full star",
                    tint= Color.Unspecified,
                    modifier = Modifier.size(starSize)
                )
            }

            if (halfStar == 1) {
                Icon(
                    painter= painterResource(id = R.drawable.half_star),
                    contentDescription ="half star",
                    tint= Color.Unspecified,
                    modifier = Modifier.size(starSize)
                )
            }

            for (i in 0 until 5-amountOfFullStars-halfStar) {
                Icon(
                    painter= painterResource(id = R.drawable.empty_star),
                    contentDescription ="empty star",
                    tint= Color.Unspecified,
                    modifier = Modifier.size(starSize)
                )
            }
        }
    }
}
