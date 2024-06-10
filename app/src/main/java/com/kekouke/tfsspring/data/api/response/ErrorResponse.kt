package com.kekouke.tfsspring.data.api.response

import com.google.gson.annotations.SerializedName

class ErrorResponse(
    @SerializedName("msg") val what: String
)