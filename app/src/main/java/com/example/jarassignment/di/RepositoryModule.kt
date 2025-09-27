package com.example.jarassignment.di

import com.example.jarassignment.data.repository.EducationRepository
import com.example.jarassignment.data.repository.EducationRepositoryImpl
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
    abstract fun bindEducationRepository(
        impl: EducationRepositoryImpl
    ): EducationRepository
}
