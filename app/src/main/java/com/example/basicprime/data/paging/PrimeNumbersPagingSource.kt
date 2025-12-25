package com.example.basicprime.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.basicprime.domain.PrimeNumbersRepository
import javax.inject.Inject

class PrimeNumbersPagingSource @Inject constructor(
    private val repository: PrimeNumbersRepository
) : PagingSource<Int, Int>() {
    override fun getRefreshKey(state: PagingState<Int, Int>): Int? {
        return if (state.anchorPosition == null) {
            null
        } else {
            state.anchorPosition?.let {
                // add 1 to previous key (in a scenario where page this is not the first page)
                state.closestPageToPosition(it)?.prevKey?.plus(1)
                // else subtract 1 from next key (in a scenario where this is the first page)
                    ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
            }
        }
    }


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Int> {
        val pageNumber = params.key ?: 0
        val pageSize = params.loadSize

        return try {
            val primeNumbers = repository.getPrimeNumbers(pageNumber)

            LoadResult.Page(
                data = primeNumbers,
                prevKey = if (pageNumber == 0) null else pageNumber.minus(1),
                nextKey = pageNumber.plus(1).takeIf {
                    // means it's not the last page
                    primeNumbers.size >= pageSize
                }
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
