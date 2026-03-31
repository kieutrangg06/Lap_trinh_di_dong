package com.nikoniche.booki.personalData

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nikoniche.booki.R
import com.nikoniche.booki.architecture.navigation.Screen
import com.nikoniche.booki.PersonalBook
import com.nikoniche.booki.book_search.SearchViewModel

@Composable
fun PersonalBookCard(
    personalBook: PersonalBook,
    showPageProgress: Boolean = false,
    showReadingStatus: Boolean = false,
    showRating: Boolean = false,
    navHostController: NavHostController, // để truy cập nhanh chi tiết sách
    searchViewModel: SearchViewModel,
) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .wrapContentHeight()
            .padding(end = 9.dp)
            .clickable {
                // SỬA TẠI ĐÂY: Dùng displayISBN thay cho getISBN()
                val isbn = personalBook.book.displayISBN
                searchViewModel.fetchSearchResults(isbn)
                navHostController.navigate(Screen.BookDetailsScreen.route + "/isbn/$isbn")
            },
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 10.dp)
        ) {
            Image(
                painter = personalBook.book.getCoverPainter(),
                contentDescription = null,
                modifier = Modifier
                    .width(95.dp)
                    .height(110.dp) // Tăng nhẹ chiều cao để ảnh bìa cân đối hơn
            )
            Spacer(Modifier.height(10.dp))

            val textSpacerHeight: Dp = 2.dp
            Text(
                text = personalBook.book.title,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2 // Tránh text quá dài làm vỡ layout
            )
            Spacer(Modifier.height(textSpacerHeight))

            // SỬA TẠI ĐÂY: Dùng authorsText thay cho getAuthors()
            Text(
                text = personalBook.book.authorsText,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Light,
                fontSize = 12.sp,
                maxLines = 1
            )

            if (showReadingStatus) {
                Spacer(Modifier.height(textSpacerHeight))
                Text(
                    text = personalBook.status.inText,
                    textAlign = TextAlign.Center,
                    // Gọi thuộc tính color của Enum Status
                    color = personalBook.status.color,
                )
            }

            if (showPageProgress) {
                Spacer(Modifier.height(textSpacerHeight))
                Text(
                    text = "${personalBook.readPages}/${personalBook.book.numberOfPages}",
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }

            if (showRating) {
                Spacer(Modifier.height(textSpacerHeight))
                StarRating(personalBook, 16.dp)
            }
        }
    }
}

@Composable
fun StarRating(personalBook: PersonalBook, starSize: Dp = 16.dp) {
    // Rating 1-10 chia 2 để ra 5 sao
    val amountOfFullStars: Int = personalBook.rating / 2
    val halfStar: Int = personalBook.rating % 2

    Row {
        for (i in 0 until amountOfFullStars) {
            Icon(
                painter = painterResource(id = R.drawable.star),
                contentDescription = "full star",
                tint = Color.Unspecified,
                modifier = Modifier.size(starSize)
            )
        }

        if (halfStar == 1) {
            Icon(
                painter = painterResource(id = R.drawable.half_star),
                contentDescription = "half star",
                tint = Color.Unspecified,
                modifier = Modifier.size(starSize)
            )
        }

        for (i in 0 until (5 - amountOfFullStars - halfStar)) {
            Icon(
                painter = painterResource(id = R.drawable.empty_star),
                contentDescription = "empty star",
                tint = Color.Unspecified,
                modifier = Modifier.size(starSize)
            )
        }
    }
}