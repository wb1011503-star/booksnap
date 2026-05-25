package com.booksnap.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.booksnap.app.data.model.Book
import com.booksnap.app.data.model.Photo

@Database(entities = [Book::class, Photo::class], version = 1, exportSchema = false)
abstract class BookSnapDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun photoDao(): PhotoDao
}
