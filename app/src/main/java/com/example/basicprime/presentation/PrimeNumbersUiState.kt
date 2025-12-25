package com.example.basicprime.presentation

import com.example.basicprime.prime.Resource

data class PrimeNumbersUiState(
    val resource: Resource<List<Int>> = Resource.Loading,
    val nextPageNumber: Int = 0,
    val showLoader: Boolean = false
)