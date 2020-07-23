package uz.isti.maxtel.model.request

data class LoginRequest(
    val phone: String,
    val code: String
)