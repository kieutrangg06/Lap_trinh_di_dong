package com.nikoniche.booki.architecture.navigation

sealed class Screen(
    val name: String,
    val route: String
) {
    data object HomeScreen : Screen(
        name = "Home",
        route = "home_screen",
    )
    data object BooksCreatedByMeScreen : Screen(
        name = "Books created by me",
        route = "books_created_by_me_screen",
    )

    data object BookDetailsScreen : Screen(
        name="Book details",
        route="book_details_screen",
    )

    data object PersonalBooksScreen : Screen(
        name="Personal books",
        route="personal_books_screen",
    )

    data object AddBookManuallyScreen: Screen(
        name="Add book manually",
        route="add_book_manually_screen",
    )

    data object SearchResultsScreen : Screen(
        name="Results of book search",
        route="search_results_screen",
    )
}