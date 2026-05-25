package com.booksnap.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.booksnap.app.data.model.Book
import com.booksnap.app.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: BookRepository
) : ViewModel() {

    val books = repo.getAllBooks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteBook(book: Book) = viewModelScope.launch {
        repo.deleteBook(book)
    }
}
