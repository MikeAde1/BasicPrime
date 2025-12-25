package com.example.basicprime.data

import com.example.basicprime.data.source.NumbersSourceImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NumbersSourceImplTest {

    private lateinit var numbersSource: NumbersSourceImpl

    @Before
    fun setUp() {
        numbersSource = NumbersSourceImpl()
    }

    @Test
    fun `getPrimeNumbers with page 0 returns the first 20 primes`() = runTest {
        val primes = numbersSource.getPrimeNumbers(0)
        
        assertEquals(20, primes.size)
        // Expected first 5 primes: 2, 3, 5, 7, 11
        assertEquals(2, primes[0])
        assertEquals(3, primes[1])
        assertEquals(5, primes[2])
        assertEquals(7, primes[3])
        assertEquals(11, primes[4])
    }

    @Test
    fun `getPrimeNumbers with page 1 returns the next 20 primes`() = runTest {
        val primes = numbersSource.getPrimeNumbers(1)
        
        assertEquals(20, primes.size)
        // The 21st prime is 73
        assertEquals(73, primes[0])
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `getPrimeNumbers with invalid page throws IndexOutOfBoundsException`() = runTest {
        // There aren't enough primes up to 5000 to have 1000 pages of 20
        numbersSource.getPrimeNumbers(1000)
    }
}
