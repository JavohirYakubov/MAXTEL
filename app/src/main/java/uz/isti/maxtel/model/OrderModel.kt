package uz.isti.maxtel.model

import java.io.Serializable

data class OrderModel(
    val refKey: String,
    val status: Int,
    val date: String,
    val number: Int,
    val skladid: Int,
    val skladname: String,
    val id: String,
    val long: String,
    val lat: String,
    val store_long: String,
    val store_lat: String,
    val delivery: Int,
    val deliveryPerson: String,
    val deliveryPersonPhone: String,
    val payme: Boolean,
    val cashback: Boolean,
    val cashbackSumma: Double,
    val deliverySumma: Double,
    val summa: Double,
    val array: List<MakeOrderProductModel>,
    val dollar: Boolean
): Serializable