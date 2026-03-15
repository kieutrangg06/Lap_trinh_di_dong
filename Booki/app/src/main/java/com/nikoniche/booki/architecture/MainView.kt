package com.nikoniche.booki.architecture

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nikoniche.booki.architecture.navigation.NavigationManager
import com.nikoniche.booki.architecture.navigation.Screen
import com.nikoniche.booki.book_search.SearchViewModel
import com.nikoniche.booki.architecture.scaffold.BookiTopBar
import com.nikoniche.booki.architecture.scaffold.DropdownNavigationMenu
import com.nikoniche.booki.architecture.scaffold.ScaffoldViewModel
import com.nikoniche.booki.book_search.UserBookViewModel
import com.nikoniche.booki.personalData.PersonalRecordsViewModel

@Composable
fun MainView() {
    val navHostController: NavHostController = rememberNavController()

    val personalRecordsViewModel: PersonalRecordsViewModel = viewModel()
    val userBookViewModel: UserBookViewModel = viewModel()
    val searchViewModel: SearchViewModel = viewModel()
    val scaffoldViewModel: ScaffoldViewModel = viewModel()

    val navigationManager = NavigationManager(
        navHostController = navHostController,

        personalRecordsViewModel=personalRecordsViewModel,
        userBookViewModel=userBookViewModel,
        searchViewModel=searchViewModel,
    )

    var currentScreen by remember {
        mutableStateOf<Screen>(Screen.HomeScreen)
    }

    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            BookiTopBar(
                scaffoldViewModel=scaffoldViewModel,
                searchViewModel=searchViewModel,
                navHostController=navHostController,
            )
        },
        /*bottomBar = {
            BottomAppBar(
                containerColor = Color.Black,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(Screen.HomeScreen, Screen.AccountScreen).forEach {
                        screen ->
                        IconButton(
                            onClick = {
                                navigationManager.NavigateToScreen(screen)
                                currentScreen = screen
                            },
                        )
                        {
                            val tint =
                                if (currentRoute == screen.route)
                                    Color.White
                                else
                                    Color.LightGray
                            Icon(
                                modifier = Modifier.size(35.dp),
                                painter = painterResource(id = when(screen) {
                                    Screen.HomeScreen -> R.drawable.ic_home
                                    Screen.AccountScreen -> R.drawable.ic_account
                                    else -> R.drawable.empty_star
                                }),
                                contentDescription = screen.name,
                                tint=tint
                            )
                        }
                    }
                }
            }
        }*/
    ) {
        if (scaffoldViewModel.menuOpened.value) {
            DropdownNavigationMenu(
                scaffoldViewModel=scaffoldViewModel,
                navHostController=navHostController,
            )
        }
        navigationManager.NavigationView(it)
    }
}

@Preview(showBackground=true)
@Composable
fun Prw() {
    MainView()
}