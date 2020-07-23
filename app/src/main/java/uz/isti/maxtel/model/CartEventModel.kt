package uz.isti.maxtel.model

data class CartEventModel(
    val event: Int,
    val count: Int,
    val totalAmount: Double
)