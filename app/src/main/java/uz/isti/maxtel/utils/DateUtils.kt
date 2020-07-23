package uz.isti.maxtel.utils

import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.ZoneOffset.UTC
import java.util.*
import java.util.logging.SimpleFormatter

class DateUtils {
    companion object{
        fun getTimeFromServerTime(time: String?): String{

            val SERVER_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            val SERVER_TIME_FORMAT2 = "yyyy-MM-dd'T'HH:mm:ss"
            val LOCAL_TIME_FORMAT = "yyyy-MM-dd hh:mm"

            if (time == null){
                return ""
            }

            val formatter = SimpleDateFormat(SERVER_TIME_FORMAT2)
            val outFormatter = SimpleDateFormat(LOCAL_TIME_FORMAT)
            formatter.timeZone = TimeZone.getTimeZone("Asia/Tashkent")

            try {
                return outFormatter.format(formatter.parse(time))
            }catch (e: Exception){
                return ""
            }
        }

        fun getDateFromServerTime(time: String?): String{

            val SERVER_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            val SERVER_TIME_FORMAT2 = "yyyy-MM-dd'T'HH:mm:ss"
            val LOCAL_TIME_FORMAT = "dd.MM.yyyy"

            if (time == null){
                return ""
            }

            val formatter = SimpleDateFormat(SERVER_TIME_FORMAT2)
            val outFormatter = SimpleDateFormat(LOCAL_TIME_FORMAT)
            formatter.timeZone = TimeZone.getTimeZone("UTC")

            try {
                return outFormatter.format(formatter.parse(time))
            }catch (e: Exception){
                return ""
            }
        }
    }
}