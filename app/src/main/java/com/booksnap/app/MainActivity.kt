package com.booksnap.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.booksnap.app.ui.Screen
import com.booksnap.app.ui.book.AddBookScreen
import com.booksnap.app.ui.book.BookDetailScreen
import com.booksnap.app.ui.home.HomeScreen
import com.booksnap.app.ui.photo.AddPhotoScreen
import com.booksnap.app.ui.theme.BookSnapTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookSnapTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    BookSnapNavGraph()
                }
            }
        }
    }
}

@Composable
fun BookSnapNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            HomeScreen(
                onAddBook = { navController.navigate(Screen.AddBook.route) },
                onBookClick = { bookId -> navController.navigate(Screen.BookDetail.createRoute(bookId)) }
            )
        }

        composable(Screen.AddBook.route) {
            AddBookScreen(
                onBack = { navController.popBackStack() },
                onBookAdded = { bookId ->
                    navController.navigate(Screen.BookDetail.createRoute(bookId)) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(
            route = Screen.BookDetail.route,
            arguments = listOf(navArgument("bookId") { type = NavType.LongType })
        ) {
            BookDetailScreen(
                onBack = { navController.popBackStack() },
                onAddPhoto = { bookId -> navController.navigate(Screen.AddPhoto.createRoute(bookId)) }
            )
        }

        composable(
            route = Screen.AddPhoto.route,
            arguments = listOf(navArgument("bookId") { type = NavType.LongType })
        ) {
            AddPhotoScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}
