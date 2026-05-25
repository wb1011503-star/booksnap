package com.booksnap.app.data.repository

import android.content.Context
import com.booksnap.app.data.db.*
import com.booksnap.app.data.model.Book
import com.booksnap.app.data.model.Photo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepository @Inject constructor(
    private val bookDao: BookDao,
    private val photoDao: PhotoDao,
    private val openLibraryApi: OpenLibraryApi,
    @ApplicationContext private val context: Context
) {
    // ── 책 ──────────────────────────────────────────────
    fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooksOrderedByLastPhoto()

    suspend fun getBookById(id: Long): Book? = bookDao.getBookById(id)

    suspend fun insertBook(book: Book): Long = bookDao.insertBook(book)

    suspend fun deleteBook(book: Book) {
        // 연관 사진 파일 삭제
        photoDao.deleteAllPhotosForBook(book.id)
        bookDao.deleteBook(book)
    }

    // ── 사진 ────────────────────────────────────────────
    fun getPhotosForBook(bookId: Long): Flow<List<Photo>> =
        photoDao.getPhotosForBook(bookId)

    suspend fun savePhoto(bookId: Long, sourceFile: File, pageNumber: Int): Photo {
        // 내부 저장소에 복사
        val dir = File(context.filesDir, "photos/$bookId").also { it.mkdirs() }
        val dest = File(dir, "${System.currentTimeMillis()}.jpg")
        sourceFile.copyTo(dest, overwrite = true)

        val photo = Photo(bookId = bookId, localPath = dest.absolutePath, pageNumber = pageNumber)
        val id = photoDao.insertPhoto(photo)

        // 책의 lastPhotoAt 갱신
        bookDao.getBookById(bookId)?.let {
            bookDao.updateBook(it.copy(lastPhotoAt = System.currentTimeMillis()))
        }
        return photo.copy(id = id)
    }

    suspend fun deletePhoto(photo: Photo) {
        File(photo.localPath).delete()
        photoDao.deletePhoto(photo)
    }

    // ── Open Library 검색 ────────────────────────────────
    suspend fun searchBooks(query: String): List<OpenLibraryBook> {
        return try {
            openLibraryApi.searchBooks(query).docs
        } catch (e: Exception) {
            emptyList()
        }
    }
}
