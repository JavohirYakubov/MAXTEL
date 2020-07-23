package uz.isti.maxtel.model.enum

import com.google.gson.annotations.SerializedName
import uz.isti.maxtel.R

enum class OrderStatus(val value: String) {
    @SerializedName("cart")
    CART("cart"),
    @SerializedName("waiting")
    WAITING("waiting"),
    @SerializedName("processing")
    PROCESSING("processing"),
    @SerializedName("sent")
    SENT("sent"),
    @SerializedName("on_way")
    ONWAY("on_way"),
    @SerializedName("delivered")
    DELIVERED("delivered"),
    @SerializedName("completed")
    COMPLETED("completed"),
    @SerializedName("cancelled")
    CANCELLED("cancelled");

}