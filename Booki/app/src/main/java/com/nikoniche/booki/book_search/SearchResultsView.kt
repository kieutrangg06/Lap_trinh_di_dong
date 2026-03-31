package com.nikoniche.booki.book_search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.*
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
                    items(searchViewModel.search.value.result) { foundBook ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(bottom = 12.dp)
                                .clickable {
                                    // SỬA TẠI ĐÂY: Dùng displayISBN thay cho getISBN()
                                    navHostController.navigate(
                                        Screen.BookDetailsScreen.route + "/isbn/${foundBook.displayISBN}"
                                    )
                                },
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White,
                            ),
                        ) {
                            Row {
                                Image(
                                    painter = foundBook.getCoverPainter(),
                                    contentDescription = "cover of the book",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .width(80.dp)
                                        .height(100.dp)
                                )
                                Column {
                                    Text(
                                        text = foundBook.title,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 4.dp, start = 8.dp),
                                        fontSize = 21.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Start,
                                    )
                                    // SỬA TẠI ĐÂY: Dùng authorsText thay cho getAuthors()
                                    Text(
                                        text = foundBook.authorsText,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 2.dp, start = 8.dp),
                                        fontSize = 17.sp,
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
                        text = "No book was found",
                        fontSize = 21.sp,
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
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp),
                    color = Color.Black
                )
            }
        }

        // Phần gợi ý thêm thủ công nếu không tìm thấy
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MyDivider(Modifier.padding(vertical = 8.dp))
            Text(
                text = "Haven't found your book?",
                fontSize = 21.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                border = BorderStroke(2.5.dp, Color.Black),
                modifier = Modifier.height(35.dp),
                shape = ButtonDefaults.filledTonalShape,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                onClick = {
                    navHostController.navigate(Screen.AddBookManuallyScreen.route)
                }
            ) {
                Text("Add manually", color = Color.Black, fontSize = 16.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchResultsViewPreview() {
    SearchResultsView(
        searchViewModel = viewModel(),
        navHostController = rememberNavController(),
    )
}