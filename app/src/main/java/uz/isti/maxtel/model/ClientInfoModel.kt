package uz.isti.maxtel.model

import java.io.Serializable

data class ClientInfoModel(
    val name: String,
    val phone: String,
    val tuman: String,
    val date: String,
    val jins: String,
    val token: String,
    val forRegDiscount: Int,
    val usuallyDiscountMin1: Int,
    val usuallyDiscountMin2: Int,
    val usuallyDiscountMin3: Int,
    val usuallyDiscount1: Int,
    val usuallyDiscount2: Int,
    val usuallyDiscount3: Int,
    val marketName: String?
): Serializable
