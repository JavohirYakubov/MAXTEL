package uz.isti.maxtel.model

import java.io.Serializable

data class OrderModel(
    val refKey: String,
    val status: Int,
    val date: String,
    val number: Int,
    val skladid: Int,
    val skladname: String,
    val agentid: String,
    val agentname: String,
    val id: String,
    val long: String,
    val lat: String,
    val where: Int,
    val cashbox: Int,
    val delivery: Int,
    val deliverySumma: Double,
    val discount: Double,
    val summa: Double,
    val Payme: Boolean,
    val isAccepted: Boolean,
    val paidSumma: Double,
    val array: List<MakeOrderProductModel>,
    val PaymeURL: String?
): Serializable