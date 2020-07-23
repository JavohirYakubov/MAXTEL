package uz.isti.maxtel.model.request

data class RatingRequest(
    val id: String,
    val skladid: Int,
    val servis: Int,
    val product: Int,
    val courier: Int,
    val comment: String
)