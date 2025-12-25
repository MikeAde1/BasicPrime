package com.example.basicprime.domain

import javax.inject.Inject

class GetPagedPrimeNumbersUseCase @Inject constructor(
    private val numbersSource: NumbersSource
) {
    suspend fun getPrimeNumbers(nextPageNumber: Int): Result<List<Int>> {
        return numbersSource.getPrimeNumbers(nextPageNumber)
    }
}