package com.example.basicprime.data

import com.example.basicprime.domain.NumbersSource
import com.example.basicprime.prime.isPrime
import javax.inject.Inject

class NumbersSourceImpl @Inject constructor() : NumbersSource {

    private val pagedPrimes by lazy {
        (1..5000).toList().filter { it.isPrime }.chunked(20)
    }

    override suspend fun getPrimeNumbers(pageNumber: Int): Result<List<Int>> {
        return try {
            // val pagedPrimes = getPagedPrimeNumbers()
            Result.success(pagedPrimes[pageNumber])
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getPagedPrimeNumbers(): MutableList<List<Int>> {
        val pageSize = 20
        val allInts = (1..5000).toList().filter { it.isPrime }
        val finalList = mutableListOf<List<Int>>()
        var index = 1
        while (finalList.size < allInts.size) {
            val pagedList = allInts.take(pageSize * index).takeLast(pageSize)
            finalList.add(pagedList)
            index++
        }
        return finalList
    }
}
