package uz.isti.maxtel.utils

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
            return result + " сум"
        }

        fun getFormattedAmount(amount: Double?, withCurrency: Boolean = true): String{
            if (amount == null){
                return ""
            }

            val formatSymbols = DecimalFormatSymbols(Locale.ENGLISH)
            formatSymbols.decimalSeparator = '.'
            formatSymbols.groupingSeparator = ' '
            val formatter = DecimalFormat("###,###.##", formatSymbols)
            var result = formatter.format(amount)
            return result + (if (withCurrency) " сум" else "")
        }
    }
}