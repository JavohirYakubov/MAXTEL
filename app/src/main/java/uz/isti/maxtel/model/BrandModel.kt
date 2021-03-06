package uz.isti.maxtel.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BrandModel(
    val id: String,
    val name: String,
    val items: Int,
    val image: String?
): Serializable