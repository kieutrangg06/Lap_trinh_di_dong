package com.nikoniche.booki.book_search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nikoniche.booki.MyDivider
import com.nikoniche.booki.MyHeadline
import com.nikoniche.booki.architecture.navigation.Screen

@Composable
fun SearchResultsView(
    searchViewModel: SearchViewModel,
    navHostController: NavHostController,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {
        MyHeadline("Search results")
        Spacer(Modifier.height(8.dp))

        if (!searchViewModel.search.value.searching) {
            if(searchViewModel.search.value.result.isNotEmpty()) {
                LazyColumn {
                    items(searchViewModel.search.value.result) {
                        foundBook ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(bottom = 12.dp)
                                .clickable {
                                    navHostController.navigate(
                                        Screen.BookDetailsScreen.route + "/isbn/${foundBook.getISBN()}"
                                    )
                                },
                            elevation=CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White,
                            ),
                        ) {
                            Row {
                                Image(
                                    painter = foundBook.getCoverPainter(),
                                    contentDescription="cover of the book",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .width(80.dp)
                                        .height(100.dp)
//                                        .padding(8.dp)
                                )
                                Column {
                                    Text(
                                        text=foundBook.title,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 4.dp),
                                        fontSize=21.sp,
                                        fontWeight= FontWeight.Bold,
                                        textAlign = TextAlign.Start,
                                    )
                                    Text(
                                        text=foundBook.getAuthors(),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 2.dp),
                                        fontSize=17.sp,
                                        fontStyle = FontStyle.Italic,
                                        textAlign = TextAlign.Start,
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(vertical = 20.dp),
                ) {
                    Text(
                        text="No book was found",
                        fontSize=21.sp,
                        fontStyle = FontStyle.Italic,
                    )
                }
            }
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MyDivider(Modifier.padding(vertical=8.dp))
            Text(
                text="Haven't found your book?",
                fontSize=21.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
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
                    navHostController.navigate(Screen.AddBookManuallyScreen.route)
                }
            ) {
                Text("Add manually", color=Color.Black, fontSize=16.sp)
            }
        }
    }
}

@Preview(showBackground=true)
@Composable
fun SearchResultsViewPreview() {
    SearchResultsView(
        searchViewModel = viewModel(),
        navHostController = rememberNavController(),
    )
}