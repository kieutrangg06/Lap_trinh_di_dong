package com.nikoniche.booki.architecture.menuViews

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nikoniche.booki.MyAppColumn
import com.nikoniche.booki.MyHeadline
import com.nikoniche.booki.personalData.PersonalBookCard
import com.nikoniche.booki.architecture.navigation.Screen
import com.nikoniche.booki.book_search.SearchViewModel
import com.nikoniche.booki.personalData.PersonalRecordsViewModel

@Composable
fun HomeView(
    navHostController: NavHostController,
    personalRecordsViewModel: PersonalRecordsViewModel,
    searchViewModel: SearchViewModel,
) {
    MyAppColumn {
//        ReadInLast("Books read this year", 27)
//        Spacer(Modifier.height(6.dp))
//        ReadInLast("Books read this month", 1)

//        Spacer(Modifier.height(12.dp))
//        MyDivider()
//        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MyHeadline(text="Your books")
            Button(
                colors = ButtonDefaults
                    .buttonColors(
                        containerColor=Color.White,
                        contentColor=Color.Black
                    ),
                border = BorderStroke(2.5.dp, Color.Black),
                modifier = Modifier.height(30.dp),
                shape = ButtonDefaults.filledTonalShape,
                contentPadding = PaddingValues(horizontal=10.dp, vertical=1.dp),
                onClick={
                    navHostController.navigate(Screen.PersonalBooksScreen.route)
                }
            ) {
                Text("Browse", color=Color.Black, fontSize=16.sp)
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
            }
        }
        Spacer(Modifier.height(8.dp))

        if (personalRecordsViewModel.books.value.isNotEmpty()) {
            LazyRow {
                items(personalRecordsViewModel.books.value) {
                        personalBook ->
                    PersonalBookCard(
                        personalBook,
                        showPageProgress = true,
                        showReadingStatus = true,
                        navHostController=navHostController,
                        searchViewModel = searchViewModel,
                    )
                    // dev for bugs
//                    Button(
//                        onClick={
//                            personalRecordsViewModel.removeBook(personalBook)
//                        }
//                    ) {
//                        Text("REMOVE")
//                    }
                }
            }
        } else {
            Text(
                text="You have no books saved."
            )
        }
    }
}

@Composable
fun ReadInLast(timeFrame: String, amount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.5.dp, color = Color.Black, shape = RoundedCornerShape(25))
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
        ) {
        Text(
            text=timeFrame,
            fontSize=17.sp,
            fontWeight= FontWeight.Normal,
            modifier = Modifier.padding(horizontal=12.dp)
        )
        Text(
            text="$amount",
            fontSize=20.sp,
            fontWeight= FontWeight.Bold,
            modifier = Modifier.padding(horizontal=12.dp)
        )
    }
}
