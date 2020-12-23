package uz.isti.maxtel.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductModel(
    val id: String,
    val name: String,
    val image: String?,
    val price: Double,
    @SerializedName("ostatka")
    val productCount: Int,
    val discount_percent: Int,
    val old_price: Double,
    val information: String,
    var favourite: Boolean = false,
    var cartCount: Int = 0
): Serializable