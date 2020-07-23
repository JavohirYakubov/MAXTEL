package uz.isti.maxtel.model

import com.google.gson.annotations.SerializedName

data class SellingModel(
    @SerializedName("Ref_Key")
    val refKey: String?,
    @SerializedName("LineNumber")
    val lineNumber: String?,
    @SerializedName("Склад_Key")
    val stockKey: String?,
    @SerializedName("Цена")
    val price: Double?
)