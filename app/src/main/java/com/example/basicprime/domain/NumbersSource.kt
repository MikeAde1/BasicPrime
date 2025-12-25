package com.example.basicprime.domain

interface NumbersSource {
    suspend fun getPrimeNumbers(pageNumber: Int): Result<List<Int>>
}