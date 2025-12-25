package com.example.basicprime.presentation

import app.cash.turbine.test
import com.example.basicprime.domain.GetPagedPrimeNumbersUseCase
import com.example.basicprime.prime.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class PrimeNumbersViewModelTest {

    private val pagedPrimeNumbersUseCase: GetPagedPrimeNumbersUseCase = mock()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `initialization loads data successfully`() = runTest {
        val primes = listOf(2, 3, 5)
        whenever(pagedPrimeNumbersUseCase.getPrimeNumbers(0)).thenReturn(Result.success(primes))

        val viewModel = PrimeNumbersViewModel(pagedPrimeNumbersUseCase)

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(Resource.Loading, initialState.resource)

            val readyState = awaitItem()
            assertTrue(readyState.resource is Resource.Ready)
            assertEquals(primes, (readyState.resource as Resource.Ready).data)
            assertEquals(0, readyState.nextPageNumber) // size is not 20
        }
    }

    @Test
    fun `initialization handles error`() = runTest {
        whenever(pagedPrimeNumbersUseCase.getPrimeNumbers(0))
            .thenReturn(Result.failure(Exception("Error")))

        val viewModel = PrimeNumbersViewModel(pagedPrimeNumbersUseCase)

        viewModel.uiState.test {
            awaitItem() // Loading
            val errorState = awaitItem()
            assertTrue(errorState.resource is Resource.Error)
            assertEquals("An error has occurred", (errorState.resource as Resource.Error).message)
        }
    }

    @Test
    fun `loadMore appends data and updates page number`() = runTest {
        val initialPrimes = List(20) { it }
        val morePrimes = listOf(21, 22)
        
        whenever(pagedPrimeNumbersUseCase.getPrimeNumbers(0)).thenReturn(Result.success(initialPrimes))
        whenever(pagedPrimeNumbersUseCase.getPrimeNumbers(1)).thenReturn(Result.success(morePrimes))

        val viewModel = PrimeNumbersViewModel(pagedPrimeNumbersUseCase)
        
        viewModel.uiState.test {
            awaitItem() // Initial Loading
            val firstPageItem = awaitItem() // First page loaded
            assertEquals(1, firstPageItem.nextPageNumber)

            viewModel.loadMore()
            
            val loadingMoreItem = awaitItem()
            assertTrue(loadingMoreItem.showLoader)

            advanceTimeBy(2001) // Account for delay(2000)

            val secondPageItem = awaitItem()
            val totalPrimes = (secondPageItem.resource as Resource.Ready).data
            assertEquals(22, totalPrimes.size)
            assertEquals(1, secondPageItem.nextPageNumber)
            assertTrue(!secondPageItem.showLoader)
        }
    }
}
