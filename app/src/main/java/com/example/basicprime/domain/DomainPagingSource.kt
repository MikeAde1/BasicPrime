package com.example.basicprime.domain

// File: domain/src/main/java/.../domain/DomainPagingSource.kt

/**
 * A pure Kotlin representation of a paginated data source.
 */
interface DomainPagingSource<Value : Any> {
    suspend fun load(params: LoadParams): LoadResult<Value>

    data class LoadParams(
        val key: Int?,
        val loadSize: Int
    )

    sealed class LoadResult<Value : Any> {
        data class Page<Value : Any>(
            val data: List<Value>,
            val prevKey: Int?,
            val nextKey: Int?
        ) : LoadResult<Value>()

        data class Error<Value : Any>(val throwable: Throwable) : LoadResult<Value>()
    }
}

// File: domain/src/main/java/.../domain/PrimeNumbersRepository.kt
interface PrimeNumbersRepository {
    // Instead of a single fetch, the repo provides the Source factory logic
    fun getPrimePagingSource(): DomainPagingSource<Int>
}