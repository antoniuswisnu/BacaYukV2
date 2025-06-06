package com.nara.bacayuk.data.model

sealed class Response<out T> {
    object Loading: Response<Nothing>()

    data class Success<out T>(
        val data: T
    ): Response<T>()

    data class Error(
        val e: String,
        val message: String? = null
    ): Response<Nothing>()
}