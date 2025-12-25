package com.example.basicprime.di

import com.example.basicprime.domain.PrimeNumbersRepository
import com.example.basicprime.data.repo.PrimeNumbersRepositoryImpl
import com.example.basicprime.data.source.NumbersSource
import com.example.basicprime.data.source.NumbersSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideNumbersSource(): NumbersSource {
        return NumbersSourceImpl()
    }

    @Provides
    @Singleton
    fun providePrimeNumbersRepository(numbersSource: NumbersSource): PrimeNumbersRepository {
        return PrimeNumbersRepositoryImpl(numbersSource)
    }
}
