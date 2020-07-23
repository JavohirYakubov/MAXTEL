package uz.isti.maxtel.model

data class LoginConfirmResponse(
    val token: String,
    val tuman: String?
)