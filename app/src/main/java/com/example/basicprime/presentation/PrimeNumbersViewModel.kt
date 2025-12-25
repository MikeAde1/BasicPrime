package com.example.basicprime.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basicprime.domain.GetPagedPrimeNumbersUseCase
import com.example.basicprime.prime.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrimeNumbersViewModel @Inject constructor(
    private val pagedPrimeNumbersUseCase: GetPagedPrimeNumbersUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<PrimeNumbersUiState> = MutableStateFlow(
        PrimeNumbersUiState()
    )
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        // using Dispatchers.Default here because we're executing cpu intensive task on the background thread
        // Dispatchers.IO will be used when making operations like network calls, file queries, writing/reading to disk etc
        viewModelScope.launch {
            pagedPrimeNumbersUseCase.getPrimeNumbers(uiState.value.nextPageNumber)
                .fold({ primesList ->
                    _uiState.update {
                        it.copy(
                            resource = Resource.Ready(primesList),
                            nextPageNumber = if (primesList.size == 20) {
                                it.nextPageNumber + 1
                            } else it.nextPageNumber
                        )
                    }

                }, {
                    _uiState.update { it.copy(resource = Resource.Error("An error has occurred")) }
                })
        }
    }

    fun loadMore() {
        if (_uiState.value.showLoader) return

        _uiState.update { it.copy(showLoader = true) }

        viewModelScope.launch {
            delay(2000)
            pagedPrimeNumbersUseCase.getPrimeNumbers(uiState.value.nextPageNumber)
                .fold({ primesList ->
                    _uiState.update {
                        it.copy(
                            resource = when (it.resource) {
                                is Resource.Ready -> Resource.Ready(
                                    it.resource.data.plus(primesList)
                                )

                                else -> {
                                    Resource.Ready(primesList)
                                }
                            },
                            nextPageNumber = if (primesList.size == 20) {
                                it.nextPageNumber + 1
                            } else it.nextPageNumber,
                            showLoader = false
                        )
                    }

                }, {
                    _uiState.update { it.copy(showLoader = false) }
                })
        }
    }
}
