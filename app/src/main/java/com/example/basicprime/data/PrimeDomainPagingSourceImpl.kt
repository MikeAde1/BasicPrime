package com.example.basicprime.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.basicprime.data.source.NumbersSource
import com.example.basicprime.domain.DomainPagingSource

// File: data/src/main/java/.../data/paging/PrimeDomainPagingSourceImpl.kt
class PrimeDomainPagingSourceImpl(
    val numbersSource: NumbersSource
) : DomainPagingSource<Int> {
    override suspend fun load(params: DomainPagingSource.LoadParams): DomainPagingSource.LoadResult<Int> {
        return try {
            val position = params.key ?: 0
            // Your logic to calculate primes or fetch from API
            val data = numbersSource.getPrimeNumbers(position)

            DomainPagingSource.LoadResult.Page(
                data = data,
                prevKey = if (position == 0) null else position - params.loadSize,
                nextKey = if (data.isEmpty()) null else position + params.loadSize
            )
        } catch (e: Exception) {
            DomainPagingSource.LoadResult.Error(e)
        }
    }
}


// File: data/src/main/java/.../data/paging/PagingSourceBridge.kt
/**
 * This bridge lives in the Data layer and converts DomainPagingSource -> Android PagingSource
 */
class PagingSourceBridge<V : Any>(
    private val domainSource: DomainPagingSource<V>
) : PagingSource<Int, V>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, V> {
        val domainParams = DomainPagingSource.LoadParams(params.key, params.loadSize)

        return when (val result = domainSource.load(domainParams)) {
            is DomainPagingSource.LoadResult.Page -> LoadResult.Page(
                data = result.data,
                prevKey = result.prevKey,
                nextKey = result.nextKey
            )

            is DomainPagingSource.LoadResult.Error -> LoadResult.Error(result.throwable)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, V>): Int? = state.anchorPosition
}
