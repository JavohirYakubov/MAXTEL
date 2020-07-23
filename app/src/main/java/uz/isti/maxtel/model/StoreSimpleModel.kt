package uz.isti.maxtel.model

import java.io.Serializable


data class StoreSimpleModel(
    val id: String,
    val name: String,
    val phone: String,
    var checked: Boolean = false
): Serializable