package com.booksnap.app.ui.book

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.booksnap.app.data.model.Book
import com.booksnap.app.data.model.Photo
import com.booksnap.app.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repo: BookRepository
) : ViewModel() {

    private val bookId: Long = checkNotNull(savedStateHandle["bookId"])

    private val _book = MutableStateFlow<Book?>(null)
    val book: StateFlow<Book?> = _book

    val photos: StateFlow<List<Photo>> = repo.getPhotosForBook(bookId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            _book.value = repo.getBookById(bookId)
        }
    }

    fun deletePhoto(photo: Photo) = viewModelScope.launch {
        repo.deletePhoto(photo)
    }

    fun getBookId() = bookId
}
