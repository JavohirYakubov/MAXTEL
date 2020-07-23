package uz.isti.maxtel.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import uz.isti.maxtel.R
import uz.isti.maxtel.model.FCMModel
import uz.isti.maxtel.screen.main.MainActivity
import uz.isti.maxtel.screen.main.news.NewsActivity
import uz.isti.maxtel.screen.main.orders.OrdersActivity
import uz.isti.maxtel.screen.main.rating.RatingActivity
import uz.isti.maxtel.utils.Prefs


class AppFirebaseMessagingService : FirebaseMessagingService(){

    private var count = 0

    override fun onNewToken(token: String) {
        Log.d("tag-debug : " , token)
        Prefs.setFCM(token)
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            Log.d("tag-debug : body " , remoteMessage?.notification?.body.toString())
            Log.d("tag-debug : title " , remoteMessage?.notification?.title.toString())
            Log.d("tag-debug : " , remoteMessage?.from.toString())
            Log.d("tag-debug : " , remoteMessage?.data.toString() ?:"")
            try{

                sendNotification(
                    remoteMessage.data.get("title").toString() ?: "",
                    remoteMessage.data.get("body").toString() ?: "",
                    remoteMessage.data.get("type").toString() ?: ""
                )
            }catch (e: java.lang.Exception){

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun sendNotification(
        title: String?,
        body: String?,
        type: String?
    ) {

        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var intent = Intent(this, MainActivity::class.java)
        if (type == "news"){
            intent = Intent(this, NewsActivity::class.java)
        }else if (type == "rating"){
            intent = Intent(this, RatingActivity::class.java)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("type", type)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = "MAXTEL"
        val builder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(BitmapFactory.decodeResource(applicationContext.resources, R.drawable.logo))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setColor(Color.parseColor("#3568D8"))
                .setSound(defaultSoundUri)
                .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
                .setContentIntent(pendingIntent)

        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "MAXTEL channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
        manager.notify(count, builder.build())
        count++
    }


}