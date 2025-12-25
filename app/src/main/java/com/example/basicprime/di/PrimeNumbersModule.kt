package com.example.basicprime.di

import com.example.basicprime.data.NumbersSourceImpl
import com.example.basicprime.domain.NumbersSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PrimeNumbersModule {

    @Provides
    fun provideNumbersSource(): NumbersSource {
        return NumbersSourceImpl()
    }
}