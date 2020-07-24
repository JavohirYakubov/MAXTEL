package uz.isti.maxtel.model

data class ActReportModel(
    var saldo: ActItemModel,
    var table: List<ActItemModel>,
    var oborot: ActItemModel,
    var dolg: ActItemModel
)