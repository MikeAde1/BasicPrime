package com.example.basicprime.data.source

interface NumbersSource {
    suspend fun getPrimeNumbers(pageNumber: Int): List<Int>
}
