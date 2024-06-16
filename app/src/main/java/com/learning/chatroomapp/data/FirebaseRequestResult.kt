package com.learning.chatroomapp.data

sealed class FirebaseRequestResult<out T> {
    data class Success<out T>(val data: T) : FirebaseRequestResult<T>()
    data class Error(val exception: Exception) : FirebaseRequestResult<Nothing>()
}