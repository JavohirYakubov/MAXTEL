package cuz.isti.maxtel.services.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern

class SmsReceiverService : BroadcastReceiver() {


    private var listenerSms: ((String) -> Unit)? = null
    private var listenerError: (() -> Unit)? = null

    companion object {
        val TAG = "SMS_ARZONI_BIZDA"
    }


    var p = Pattern.compile("(|^)\\d{6}")
    override fun onReceive(p0: Context?, intent: Intent?) {

        Log.d(TAG, "receive")
        Log.d(TAG, "received mssg ${intent?.action}")

        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            Log.d(TAG, "received mssg ichida")

            val extras = intent?.extras
            val status = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status

            Log.d(TAG, "received mssg ${status.statusCode} ")
            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {

                    // Get SMS message contents
                    var otp: String = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    if (listenerSms != null) {
                        otp = otp.replace("<#> ", "").split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                        StringBuilder().apply {
                            append("SMS code = $otp")
                            toString().also { log ->
                                Log.d(TAG, "SMSCODE ${log}")
                            }
                        }
                        listenerSms?.invoke(otp.substring(0, 6))
                    } else {
                    }

                }

                CommonStatusCodes.TIMEOUT -> {
                    StringBuilder().apply {
                        append("SMS timeout")
                        toString().also { log ->
                            Log.d(TAG, "SMSCODE ${log}")

                        }
                    }
                    listenerError?.invoke()
                }
                // Waiting for SMS timed out (5 minutes)
            }
        }


//
//        val data = p1!!.getExtras()
//        val pdus = data.get("pdus") as Array<*>
//        for (i in pdus.indices) {
//            val smsMessage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//                 SmsMessage.createFromPdu(pdus[i] as ByteArray?, data.getString("format"));
//            }
//            else {
//                 SmsMessage.createFromPdu(pdus[i] as ByteArray?);
//            }
//            val sender = smsMessage.displayOriginatingAddress
//            val phoneNumber = smsMessage.displayOriginatingAddress
//            val senderNum = phoneNumber
//            val messageBody = smsMessage.messageBody
//            try {
//                if (messageBody != null) {
//                    val m = p.matcher(messageBody)
//                    if (m.find()) {
//                        val smsCode = m.group(0)
//                        Log.d(TAG, "sms received ${smsCode}")
//                        if (senderNum == "1900"){
//                            listener?.set(smsCode)
//                        }
//
////                        mListener.messageReceived(m.group(0))
//                    } else {
//                        Log.d(TAG, "sms not receiver")
//                    }
//                }
//            } catch (e: Exception) {
//                Log.d(TAG, "exception ${e}")
//            }
//
//        }
    }


    fun setSmsListener(f: (String) -> Unit) {
        listenerSms = f
    }

    fun setTimeoutListener(f: () -> Unit) {
        listenerError = f
    }


}