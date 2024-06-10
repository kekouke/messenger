package com.kekouke.tfsspring.data.api

import com.google.gson.Gson
import com.kekouke.tfsspring.data.api.response.ErrorResponse
import okhttp3.ResponseBody

private val gson by lazy(::Gson)

fun throwExceptionOnFailedRequest(errorBody: ResponseBody): Nothing {
    val result = gson.fromJson(errorBody.string(), ErrorResponse::class.java)
    throw Exception("The request failed due to: ${result.what}}")
}