package arzaq.azmi.moviecatalogue.notification

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import arzaq.azmi.moviecatalogue.R
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class DailyReleaseIntentService : IntentService("DailyReleaseIntentService") {

    companion object {
        const val API_KEY = "5bb04aa026762c71c1affb68fe1bc79d"
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d("service", "started")

        var notificationTitle = "Release Today!"
        var notificationMassage = ""
        val notificationId = 100

        val currentDate = Date()
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(currentDate)

        val url =
            "https://api.themoviedb.org/3/discover/movie?api_key=$API_KEY&primary_release_date.gte=$formattedDate&primary_release_date.lte=$formattedDate"

        AndroidNetworking.get(url)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    val jsonArray = response.getJSONArray("results")

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)

                        notificationMassage += "${jsonObject.optString("title")} "

                        if (i == 4) break
                    }

                    notificationMassage += "and ${jsonArray.length() - 4} more"

                    showNotification(
                        applicationContext,
                        notificationTitle,
                        notificationMassage,
                        notificationId
                    )
                }

                override fun onError(error: ANError) {
                    Log.d("service", "failure")
                    notificationTitle = applicationContext.resources.getText(R.string.dailyReleaseNotificationTitle).toString()
                    notificationMassage = applicationContext.resources.getText(R.string.dailyReleaseNotificationMessage).toString()

                    showNotification(
                        applicationContext,
                        notificationTitle,
                        notificationMassage,
                        notificationId
                    )
                }
            })
    }

    private fun showNotification(
        context: Context,
        notificationTitle: String,
        notificationMassage: String,
        notificationId: Int
    ) {
        val channelId = "Channel_1"
        val channelName = "Reminder channel"
        val notificationManagerCompat =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(notificationTitle)
            .setSmallIcon(R.drawable.ic_notifications_24dp)
            .setContentText(notificationMassage)
            .setColor(ContextCompat.getColor(context, android.R.color.black))
            .setVibrate(longArrayOf(200, 25, 200, 25))
            .setSound(alarmSound)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(200, 25, 200, 25)
            builder.setChannelId(channelId)
            notificationManagerCompat.createNotificationChannel(channel)
        }

        val notification = builder.build()

        notificationManagerCompat.notify(notificationId, notification)
    }
}
