package uz.isti.maxtel.api

data class ISTICheckModel(
    val ipaddress: String,
    val ipport: String,
    val href_address: String,
    val bdate: String,
    val edate: String,
    val status: String,
    val secret_name: String,
    val client_version_code: String?,
    val mobile_username: String?,
    val mobile_password: String?
)