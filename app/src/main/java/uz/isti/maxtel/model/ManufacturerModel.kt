package uz.isti.maxtel.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ManufacturerModel(
    @SerializedName("Ref_Key")
    val refKey: String?,
    @SerializedName("DataVersion")
    val dataVersion: String?,
    @SerializedName("DeletionMark")
    val deletionMark: Boolean?,
    @SerializedName("тек")
    val tek: Boolean?,
    @SerializedName("Code")
    val code: String?,
    @SerializedName("Description")
    val Description: String?,
    @SerializedName("Приоритет")
    val priority: String?,
    @SerializedName("Predefined")
    val Predefined: Boolean?,
    @SerializedName("PredefinedDataName")
    val predefinedDataName: String?,
    @SerializedName("Owner_Key")
    val ownerKey: String?,
    @SerializedName("АдресКартинки")
    val addressImage: String?,
    @SerializedName("ИмяКартинки")
    val imageName: String?,
    @SerializedName("Owner@navigationLinkUrl")
    val ownerNavigationUrl: String?
): Serializable