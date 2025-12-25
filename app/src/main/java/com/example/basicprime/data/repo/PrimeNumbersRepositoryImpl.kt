package com.example.basicprime.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.basicprime.data.paging.PrimeNumbersPagingSource
import com.example.basicprime.data.source.NumbersSource
import com.example.basicprime.domain.PrimeNumbersRepository
import javax.inject.Inject

class PrimeNumbersRepositoryImpl @Inject constructor(
    private val numbersSource: NumbersSource
) : PrimeNumbersRepository {
    override suspend fun getPrimeNumbers(pageNumber: Int): List<Int> {
        return numbersSource.getPrimeNumbers(pageNumber)
    }
}
