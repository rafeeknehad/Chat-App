package com.example.myapplication.state

sealed class Result<out R> {
    data class Success<out T>(val data: T? = null) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
    object Empty : Result<Nothing>()
}
