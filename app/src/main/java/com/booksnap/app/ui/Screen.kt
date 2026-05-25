package com.booksnap.app.ui

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddBook : Screen("add_book")
    object BookDetail : Screen("book_detail/{bookId}") {
        fun createRoute(bookId: Long) = "book_detail/$bookId"
    }
    object AddPhoto : Screen("add_photo/{bookId}") {
        fun createRoute(bookId: Long) = "add_photo/$bookId"
    }
}
