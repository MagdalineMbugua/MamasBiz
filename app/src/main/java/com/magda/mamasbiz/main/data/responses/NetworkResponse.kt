package com.magda.mamasbiz.main.data.responses

import com.magda.mamasbiz.main.utils.Status

data class NetworkResponse <out T>(
    val status: Status,
    val isUploaded: Boolean?,
    val data: T ?,
    val error: String?

) {
    companion object {
        fun <T> loading(): NetworkResponse<T> = NetworkResponse(Status.LOADING, null, null,null)
        fun <T>success( isUploaded: Boolean?, data: T?): NetworkResponse <T> =
            NetworkResponse(Status.SUCCESS,  isUploaded, data, null)

        fun <T>error(error: String?): NetworkResponse<T>  =
            NetworkResponse(Status.ERROR, null,null,  error)


    }
}
