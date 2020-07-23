package uz.isti.maxtel.model

import java.io.Serializable

data class UserSettings(
    val id: Int,
    val phone: String,
    val username: String?,
    val token: String,
    val coins: String,
    val province_id: Int?,
    val region_id: Int?,
    val district_id: Int?
): Serializable