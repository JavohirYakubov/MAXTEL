package uz.isti.maxtel.api

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    val error: Boolean,
    val message: String?,
    val error_code: Int?,
    val data: T
)