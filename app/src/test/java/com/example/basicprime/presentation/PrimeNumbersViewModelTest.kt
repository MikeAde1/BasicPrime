package com.example.basicprime.presentation

import androidx.paging.testing.asSnapshot
import com.example.basicprime.domain.PrimeNumbersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class PrimeNumbersViewModelTest {

    private val repository: PrimeNumbersRepository = mock()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `primeNumbersList emits correct data from repository`() = runTest {
        val primesPage0 = listOf(2, 3, 5, 7, 11)
        whenever(repository.getPrimeNumbers(0)).thenReturn(primesPage0)

        val viewModel = PrimeNumbersViewModel(repository)

        // Using asSnapshot from paging-testing to collect the flow into a list
        val snapshot = viewModel.primeNumbersList.asSnapshot()

        assertEquals(primesPage0, snapshot)
    }

    @Test
    fun `primeNumbersList pagination loads more data when scrolling`() = runTest {
        // Page size is 20 in ViewModel
        val primesPage0 = List(20) { it + 1 }
        val primesPage1 = listOf(21, 22, 23)
        
        whenever(repository.getPrimeNumbers(0)).thenReturn(primesPage0)
        whenever(repository.getPrimeNumbers(1)).thenReturn(primesPage1)

        val viewModel = PrimeNumbersViewModel(repository)

        // asSnapshot allows simulating interactions like scrolling
        val snapshot = viewModel.primeNumbersList.asSnapshot {
            scrollTo(19) // Trigger prefetch/next page load
        }

        assertEquals(primesPage0 + primesPage1, snapshot)
    }
}
