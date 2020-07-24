package uz.isti.maxtel.model

import uz.isti.maxtel.model.enum.CurrencyEnum
import uz.isti.maxtel.utils.Prefs
import java.io.Serializable

data class MakeOrderModel(
    val skladid: Int,
    val long: String,
    val lat: String,
    val address: String,
    val deliveryAddress: String,
    val summa: Double,
    val array: List<MakeOrderProductModel>,
    val dollar: Boolean = Prefs.getCurrency() == CurrencyEnum.USD
): Serializable

data class MakeOrderProductModel(
    val name: String,
    val pid: String,
    val price: Double,
    val dona: Double,
    val comment: String,
    val psumma: Double,
    val pname: String = ""
): Serializable