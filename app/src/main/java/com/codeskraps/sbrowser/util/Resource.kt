package com.codeskraps.sbrowser.util

sealed interface Resource<T> {
    class Success<T>(val data: T) : Resource<T>
    class Error<T>(val message: String) : Resource<T>
}