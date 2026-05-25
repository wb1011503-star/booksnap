package com.booksnap.app.ui.photo

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.booksnap.app.data.model.Book
import com.booksnap.app.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddPhotoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repo: BookRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val bookId: Long = checkNotNull(savedStateHandle["bookId"])

    private val _book = MutableStateFlow<Book?>(null)
    val book: StateFlow<Book?> = _book

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    init {
        viewModelScope.launch {
            _book.value = repo.getBookById(bookId)
        }
    }

    fun savePhoto(uri: Uri, pageNumber: Int) = viewModelScope.launch {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@launch
            val tempFile = File.createTempFile("snap", ".jpg", context.cacheDir)
            tempFile.outputStream().use { out -> inputStream.copyTo(out) }
            repo.savePhoto(bookId, tempFile, pageNumber)
            tempFile.delete()
            _saveSuccess.value = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
