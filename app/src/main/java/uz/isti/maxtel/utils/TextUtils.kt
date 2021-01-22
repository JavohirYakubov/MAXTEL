package uz.isti.maxtel.utils

import uz.isti.maxtel.R
import uz.isti.maxtel.model.enum.CurrencyEnum
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class TextUtils {
    companion object{
        fun tinyToFormatText(cost: String?): String {
            var amountInTiyins:Long = 0

            if (cost != null && !cost!!.isEmpty()){
                amountInTiyins = cost?.toDouble().toLong() ?: 0
                amountInTiyins = amountInTiyins * 100
            }
            var tiyins = amountInTiyins % 100
            if (tiyins < 0) tiyins *= -1
            var tiyinString: String = if (tiyins < 10) "0$tiyins" else tiyins.toString()
            var amountInSums = if (amountInTiyins > 0) (amountInTiyins - tiyins) / 100
            else (amountInTiyins + tiyins) / 100

            val formatSymbols = DecimalFormatSymbols(Locale.ENGLISH)
            formatSymbols.decimalSeparator = '.'
            formatSymbols.groupingSeparator = ' '
            val formatter = DecimalFormat("###,###.##", formatSymbols)
            var result = formatter.format(amountInSums)
            if (tiyins > 0) {
                if (amountInTiyins > 0) result = "$result.$tiyinString"
                else if (amountInSums == 0L && amountInTiyins < 0) result = "-$result.$tiyinString"
                else result = "$result.$tiyinString"
            }
            return result
        }

        fun getFormattedAmount(amount: Double?, withCurrency: Boolean = true, withRate: Boolean = true, currency: String = Prefs.getCurrency().getName()): String{
            if (amount == null){
                return ""
            }
            var totalAmount = amount
            if (!withRate){

            }else if (Prefs.getCurrency() == CurrencyEnum.UZS){
                totalAmount *= (Prefs.getClientInfo()?.currency ?: 1.0)
            }
            val formatSymbols = DecimalFormatSymbols(Locale.ENGLISH)
            formatSymbols.decimalSeparator = '.'
            formatSymbols.groupingSeparator = ' '
            if (Prefs.getCurrency() != CurrencyEnum.UZS){
                val formatter = DecimalFormat("###,###.##", formatSymbols)
                var result = formatter.format(totalAmount)
                return result + (if (withCurrency) " ${currency}" else "")
            }else{
                val formatter = DecimalFormat("###,###", formatSymbols)
                var result = formatter.format(totalAmount)
                return result + (if (withCurrency) " ${currency}" else "")
            }
        }

        fun getFormattedAmount(amount: Double?, currency: String): String{
            if (amount == null){
                return ""
            }
            var totalAmount = amount
            val formatSymbols = DecimalFormatSymbols(Locale.ENGLISH)
            formatSymbols.decimalSeparator = '.'
            formatSymbols.groupingSeparator = ' '
            val formatter = DecimalFormat("###,###", formatSymbols)
            var result = formatter.format(totalAmount)
            return result + " ${currency}"
        }
    }
}