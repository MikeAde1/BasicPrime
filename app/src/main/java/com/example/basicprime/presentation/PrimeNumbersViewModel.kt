package com.example.basicprime.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.basicprime.data.paging.PrimeNumbersPagingSource
import com.example.basicprime.domain.PrimeNumbersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PrimeNumbersViewModel @Inject constructor(
    primeNumbersRepository: PrimeNumbersRepository
) : ViewModel() {

    val primeNumbersList = Pager(
        config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 1,
            enablePlaceholders = false,
            initialLoadSize = 20
        ),
        pagingSourceFactory = { PrimeNumbersPagingSource(primeNumbersRepository) } // Source created here
    ).flow.cachedIn(viewModelScope)
}
