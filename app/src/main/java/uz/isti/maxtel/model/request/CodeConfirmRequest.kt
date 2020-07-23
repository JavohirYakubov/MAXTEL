package uz.isti.maxtel.model.request

import com.google.gson.annotations.SerializedName

data class CodeConfirmRequest(
    @SerializedName("telephone")
    val phone: String,
    @SerializedName("sms")
    val code: String,
    @SerializedName("marketName")
    val marketName: String?,
    @SerializedName("fio")
    val fullName: String?,
    val date: String = ""
)