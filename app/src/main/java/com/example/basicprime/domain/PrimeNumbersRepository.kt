package com.example.basicprime.domain

interface PrimeNumbersRepository {
    suspend fun getPrimeNumbers(pageNumber: Int): List<Int>
}
