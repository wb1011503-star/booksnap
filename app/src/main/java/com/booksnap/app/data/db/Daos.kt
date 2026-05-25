package com.booksnap.app.data.db

import androidx.room.*
import com.booksnap.app.data.model.Book
import com.booksnap.app.data.model.Photo
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY lastPhotoAt DESC")
    fun getAllBooksOrderedByLastPhoto(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookById(id: Long): Book?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book): Long

    @Update
    suspend fun updateBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)
}

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos WHERE bookId = :bookId ORDER BY pageNumber ASC")
    fun getPhotosForBook(bookId: Long): Flow<List<Photo>>

    @Query("SELECT COUNT(*) FROM photos WHERE bookId = :bookId")
    suspend fun getPhotoCountForBook(bookId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: Photo): Long

    @Delete
    suspend fun deletePhoto(photo: Photo)

    @Query("DELETE FROM photos WHERE bookId = :bookId")
    suspend fun deleteAllPhotosForBook(bookId: Long)
}
