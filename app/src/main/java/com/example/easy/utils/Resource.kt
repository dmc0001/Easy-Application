package com.example.easy.utils

sealed class Resource<T>(
val data: T? = null,
val message: String? = null
) {
    class Success<T>(data: T, message: String) : Resource<T>(data, message)
    class Failed<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
    class Unspecified<T> : Resource<T>()

}