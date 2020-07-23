package uz.isti.maxtel.model

import java.io.Serializable

data class AddressModel(
    var address: String?,
    val lat: Double,
    val lon: Double,
    var region: String,
    var district: String,
    var additional: String = ""
): Serializable