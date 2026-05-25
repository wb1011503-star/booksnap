package com.booksnap.app.ui.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.booksnap.app.data.db.OpenLibraryBook
import com.booksnap.app.data.model.Book
import com.booksnap.app.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class AddBookViewModel @Inject constructor(
    private val repo: BookRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _results = MutableStateFlow<List<OpenLibraryBook>>(emptyList())
    val results: StateFlow<List<OpenLibraryBook>> = _results

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            _query
                .debounce(500)
                .filter { it.length >= 2 }
                .distinctUntilChanged()
                .collect { q ->
                    _isLoading.value = true
                    _results.value = repo.searchBooks(q)
                    _isLoading.value = false
                }
        }
    }

    fun onQueryChange(q: String) { _query.value = q }

    suspend fun addBook(olBook: OpenLibraryBook): Long {
        val book = Book(
            title = olBook.title,
            author = olBook.authorDisplay(),
            coverUrl = olBook.coverUrl(),
            openLibraryKey = olBook.key
        )
        return repo.insertBook(book)
    }

    suspend fun addManualBook(title: String, author: String): Long {
        return repo.insertBook(Book(title = title, author = author))
    }
}
