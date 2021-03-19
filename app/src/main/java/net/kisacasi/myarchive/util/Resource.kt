package net.kisacasi.myarchive.util

sealed class Resource<T>(
    val data: T? = null,
    val message: Any? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: Any, data: T? = null) : Resource<T>(data, message)
//    class ErrorNumber<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}