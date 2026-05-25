package com.booksnap.app.di

import android.content.Context
import androidx.room.Room
import com.booksnap.app.data.db.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): BookSnapDatabase =
        Room.databaseBuilder(ctx, BookSnapDatabase::class.java, "booksnap.db").build()

    @Provides fun provideBookDao(db: BookSnapDatabase): BookDao = db.bookDao()
    @Provides fun providePhotoDao(db: BookSnapDatabase): PhotoDao = db.photoDao()

    @Provides @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            .build()

    @Provides @Singleton
    fun provideOpenLibraryApi(client: OkHttpClient): OpenLibraryApi =
        Retrofit.Builder()
            .baseUrl("https://openlibrary.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenLibraryApi::class.java)
}
