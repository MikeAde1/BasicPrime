package com.example.basicprime.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.basicprime.domain.PrimeNumbersRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class PrimeNumbersPagingSourceTest {

    private val repository: PrimeNumbersRepository = mock()
    private lateinit var pagingSource: PrimeNumbersPagingSource

    @Before
    fun setUp() {
        pagingSource = PrimeNumbersPagingSource(repository)
    }

    @Test
    fun `load returns Success when repository returns data`() = runTest {
        val page0Data = listOf(2, 3, 5)
        whenever(repository.getPrimeNumbers(0)).thenReturn(page0Data)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 3,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(page0Data, page.data)
        assertEquals(null, page.prevKey)
        assertEquals(1, page.nextKey)
    }

    @Test
    fun `load returns Success for subsequent page`() = runTest {
        val page1Data = listOf(7, 11, 13)
        whenever(repository.getPrimeNumbers(1)).thenReturn(page1Data)

        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 1,
                loadSize = 3,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(page1Data, page.data)
        assertEquals(0, page.prevKey)
        assertEquals(2, page.nextKey)
    }

    @Test
    fun `load returns Success with null nextKey when end of pagination reached`() = runTest {
        val lastPageData = listOf(17, 19) // Less than loadSize (3)
        whenever(repository.getPrimeNumbers(2)).thenReturn(lastPageData)

        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 2,
                loadSize = 3,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(null, page.nextKey)
    }

    @Test
    fun `load returns Error when repository throws exception`() = runTest {
        val exception = RuntimeException("Network error")
        whenever(repository.getPrimeNumbers(0)).thenThrow(exception)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 3,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Error)
        assertEquals(exception, (result as PagingSource.LoadResult.Error).throwable)
    }

    @Test
    fun `getRefreshKey returns null when anchorPosition is null`() {
        val state = PagingState<Int, Int>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 3),
            leadingPlaceholderCount = 0
        )

        val refreshKey = pagingSource.getRefreshKey(state)

        assertEquals(null, refreshKey)
    }

    @Test
    fun `getRefreshKey returns next page number when anchorPosition is not null`() {
        // Mock state with anchorPosition and loaded pages
        val page0 = PagingSource.LoadResult.Page(
            data = listOf(2, 3, 5),
            prevKey = null,
            nextKey = 1
        )
        val state = PagingState(
            pages = listOf(page0),
            anchorPosition = 1,
            config = PagingConfig(pageSize = 3),
            leadingPlaceholderCount = 0
        )

        val refreshKey = pagingSource.getRefreshKey(state)

        // anchorPosition 1 is in page0, which has nextKey 1. 
        // Logic: nextKey (1) - 1 = 0
        assertEquals(0, refreshKey)
    }
}
