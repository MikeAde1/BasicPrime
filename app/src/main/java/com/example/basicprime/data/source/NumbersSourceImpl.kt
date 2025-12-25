package com.example.basicprime.data.source

import com.example.basicprime.prime.isPrime
import kotlinx.coroutines.delay
import javax.inject.Inject

class NumbersSourceImpl @Inject constructor() : NumbersSource {

    private val pagedPrimes by lazy {
        (1..5000).toList().filter { it.isPrime }.chunked(20)
    }

    override suspend fun getPrimeNumbers(pageNumber: Int): List<Int> {
        delay(2000)
        return pagedPrimes[pageNumber]
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
