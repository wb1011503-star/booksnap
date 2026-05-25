package com.booksnap.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val author: String,
    val coverUrl: String? = null,
    val openLibraryKey: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastPhotoAt: Long = System.currentTimeMillis() // 마지막 사진 저장 시각
)

@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long,
    val localPath: String,       // 내부 저장소 경로
    val pageNumber: Int,
    val savedAt: Long = System.currentTimeMillis()
)
