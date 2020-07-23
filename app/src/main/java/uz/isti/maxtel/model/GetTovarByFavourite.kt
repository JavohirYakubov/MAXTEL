package uz.isti.maxtel.model

import uz.isti.maxtel.utils.Prefs
data class IdValModel(
    val id: String
)

data class GetTovarByFavourite(
    val skladid: Int,
    val items: List<IdValModel>
)