package com.example.kathavichar.network

sealed class ServerResponse<T>(val data: T? = null, val message: String? = null) {

    class isLoading<T> : ServerResponse<T>(null)

    class onSuccess<T>(data: T) : ServerResponse<T>(data)

    class onError<T>(data: T?, message: String?) : ServerResponse<T>(data, message)
}
