package uz.isti.maxtel.model

data class EventModel<T>(
    val event: Int,
    val data: T
)