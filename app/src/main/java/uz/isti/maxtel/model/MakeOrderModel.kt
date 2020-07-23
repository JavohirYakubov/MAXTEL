package uz.isti.maxtel.model

import java.io.Serializable

data class MakeOrderModel(
    val skladid: Int,
    val long: String,
    val lat: String,
    val address: String,
    val where: Int,
    val cashbox: Int,
    val delivery: Int,
    val deliveryAddress: String,
    val deliverySumma: Int,
    val discount: Int,
    val summa: Int,
    val Payme: Boolean,
    val array: List<MakeOrderProductModel>,
    val agentid: String = "00000000-0000-0000-0000-000000000000",
    val id: String = "",
    val agentComment: String = "",
    val agentSumma: Int = 0
): Serializable

data class MakeOrderProductModel(
    val name: String,
    val pid: String,
    val price: Double,
    val dona: Int,
    val blok: Double,
    val bonus: Boolean,
    val comment: String,
    val psumma: Double,
    val pname: String = ""
): Serializable

//{
//    "skladid" : 1,
//    "agentid" : "0fd55fb5-7a45-11ea-9c58-b888e308046f",
//    "id" : "ad066245-7696-4b18-87bf-70a8d2d4b8b6",
//    "long" : "72.56565",
//    "lat" : "42.5656565",
//    "where" : 2,
//    "cashbox" :0,
//    "delivery" :0,
//    "deliverySumma" : 15000,
//    "summa" : 134760,
//    "Payme" : false,
//    "array":
//    [{
//    "pid" : "d066d6ef-f9a0-11e9-aa14-c01885f61d70",
//    "price" : 4990,
//    "dona" : 24,
//    "blok" :2,
//    "bonus" :false,
//    "comment" :"tovar uchun izoh",
//    "psumma" : 119760
//}  ]
//}