package uz.isti.maxtel.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductModel(
    val id: String,
    val name: String,
    val priority: Int,
    val image: String?,
    val price: Double,
    @SerializedName("ostatka")
    val productCount: Int,
    @SerializedName("donaBlok")
    val unity: String,
    val blok: Double?,
    val blokostatok: Double,
    val blokprice: Double,
    val kg: Double,
    val information: String,
    val donalibonus: Boolean,
    val limitbonus: Int,
    val tovarbonus: Int,
    val foizlibonus: Boolean,
    val foiz: Int,
    val yangi_rasm: Int,
    val file_id: String,
    val calculation: List<Double>,
    var favourite: Boolean = false,
    var cartCount: Int = 0
): Serializable