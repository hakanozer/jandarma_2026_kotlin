package com.works.myapplication.di

import com.works.myapplication.data.repository.AuthRepositoryImpl
import com.works.myapplication.data.repository.NoteRepositoryImpl
import com.works.myapplication.data.repository.ProductRepositoryImpl
import com.works.myapplication.domain.repository.AuthRepository
import com.works.myapplication.domain.repository.NoteRepository
import com.works.myapplication.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindNoteRepository(
        noteRepositoryImpl: NoteRepositoryImpl
    ): NoteRepository
}