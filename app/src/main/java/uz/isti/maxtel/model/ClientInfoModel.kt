package uz.isti.maxtel.model

import java.io.Serializable

data class ClientInfoModel(
    val name: String,
    val phone: String,
    val tuman: String,
    val date: String,
    val jins: String,
    val token: String,
    val minimalDostavkaSumma: Double,
    val minimalDostavkaKm: Double,
    val kmSumma: Double,
    val marketName: String?,
    val currency: Double
): Serializable
