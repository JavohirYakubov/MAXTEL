package uz.isti.maxtel.model.enum

import com.google.gson.annotations.SerializedName
import uz.isti.maxtel.R


enum class CurrencyEnum(val value: String){
    @SerializedName("usd")
    USD("usd"),

    @SerializedName("uzs")
    UZS("uzs");

    fun getImage(): Int{
        when(this){
            USD -> return R.drawable.ic_usa_today
            UZS -> return R.drawable.ic_uzbekistan
        }
    }

    fun getName(): String{
        when(this){
            USD -> return "$"
            UZS -> return "сум"
        }
    }
}