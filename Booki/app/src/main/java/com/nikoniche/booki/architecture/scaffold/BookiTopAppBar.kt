package com.nikoniche.booki.architecture.scaffold

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nikoniche.booki.BarcodeScanner
import com.nikoniche.booki.R
import com.nikoniche.booki.architecture.navigation.Screen
import com.nikoniche.booki.book_search.SearchViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookiTopBar(
    scaffoldViewModel: ScaffoldViewModel,
    searchViewModel: SearchViewModel,
    navHostController: NavHostController,
) {
    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = {
                    navHostController.navigate(Screen.HomeScreen.route)
                },
            ) {
                Icon(painter=painterResource(R.drawable.ic_menu_book),
                    contentDescription = "app icon",
                    tint= Color.White,
                    modifier = Modifier
                        .size(35.dp)
                        .padding(start = 4.dp)
                )
            }
        },
        title = {
            Box(
                modifier= Modifier
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier= Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .border(1.dp, Color.White),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    var searchQuery by remember {
                        mutableStateOf("")
                    }
                    val verticalScrollState = rememberScrollState()
                    val coroutineScope = rememberCoroutineScope()
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            coroutineScope.launch {
                                verticalScrollState.scrollTo(verticalScrollState.maxValue)
                            }
                        },
                        textStyle = TextStyle(color=Color.White, fontSize=16.sp),
                        cursorBrush = SolidColor(Color.White),
                        modifier = Modifier
                            .padding(horizontal = 7.dp, vertical = 5.dp)
                            .width(170.dp)
                            .verticalScroll(verticalScrollState),
                        decorationBox = {
                            innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text="Search any book",
//                                    modifier=Modifier.padding(start=10.dp),
                                    fontSize=16.sp,
                                    color=Color.LightGray,
                                    fontStyle = FontStyle.Italic,
                                )
                            }
                            innerTextField()
                        }
                    )

                    val barcodeScanner = BarcodeScanner(LocalContext.current)
                    Row {
                        if(searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick={
                                    navHostController.navigate(Screen.SearchResultsScreen.route)
                                    searchViewModel.fetchSearchResults(searchQuery)
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_search),
                                    contentDescription = "search",
                                    tint=Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                        IconButton(
                            onClick={
                                coroutineScope.launch {
                                    barcodeScanner.startScan()
                                    if (barcodeScanner.barCodeResults.value != null) {
                                        searchQuery = barcodeScanner.barCodeResults.value.toString()
                                        navHostController.navigate(Screen.SearchResultsScreen.route)
                                        searchViewModel.fetchSearchResults(searchQuery)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_scan),
                                contentDescription = "scan isbn",
                                tint=Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }
        },
        actions = {
            IconButton(
                onClick={
                    scaffoldViewModel.menuOpened.value = !scaffoldViewModel.menuOpened.value
                }
            ) {
                Icon(
                    painter= painterResource(id = R.drawable.ic_menu),
                    contentDescription = "browse",
                    tint=Color.White,
                    modifier = Modifier.size(35.dp)
                )
            }
        },
        colors=TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black,
            actionIconContentColor = Color.White,
            navigationIconContentColor = Color.White,
            titleContentColor = Color.White,
        )
    )
}

@Preview(showBackground=true)
@Composable
fun BookiTopBarPreview() {
    BookiTopBar(viewModel(), viewModel(), rememberNavController())
}