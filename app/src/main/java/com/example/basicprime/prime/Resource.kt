package com.example.basicprime.prime

sealed class Resource<out T> {
    data object Loading : Resource<Nothing>()
    data class Error(val message: String?) : Resource<Nothing>()
    data class Ready<T>(val data: T) : Resource<T>()
}