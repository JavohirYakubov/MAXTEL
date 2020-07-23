package uz.isti.maxtel.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CategoryModel(
    val id: String,
    val name: String,
    val priority: Int,
    var selected: Boolean = false
): Serializable