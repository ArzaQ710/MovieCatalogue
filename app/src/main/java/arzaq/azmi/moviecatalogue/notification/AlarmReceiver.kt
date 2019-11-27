package arzaq.azmi.moviecatalogue.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import arzaq.azmi.moviecatalogue.R
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val DAILY_RELEASE_REMINDER_ID = 10
        const val RETURN_TO_APP_REMINDER_ID = 20
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_ID = "extra_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notificationTitle = intent.getStringExtra(EXTRA_TITLE)
        val notificationMassage = intent.getStringExtra(EXTRA_MESSAGE)
        val notificationId = intent.getIntExtra(EXTRA_ID, 0)

        showNotification(context, notificationTitle, notificationMassage, notificationId)
    }

    fun setReturnToAppReminderAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        val notificationTitle = "We Miss You"
        val notificationMassage = "Return to Movie Catalogue Now"
        val notificationId = 200

        alarmIntent.putExtra(EXTRA_TITLE, notificationTitle)
        alarmIntent.putExtra(EXTRA_MESSAGE, notificationMassage)
        alarmIntent.putExtra(EXTRA_ID, notificationId)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 7)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            RETURN_TO_APP_REMINDER_ID, alarmIntent, 0
        )

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        Toast.makeText(
            context,
            R.string.return_to_app_reminder_on,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun cancelReturnToAppReminderAlarm(context: Context) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val requestCode = RETURN_TO_APP_REMINDER_ID
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0)

        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)
        Toast.makeText(
            context,
            R.string.return_to_app_reminder_off,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun setDailyReleaseReminderAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 8)
        }

        val serviceIntent = Intent(context, DailyReleaseIntentService::class.java)

        val servicePendingIntent = PendingIntent.getService(
            context,
            DAILY_RELEASE_REMINDER_ID,
            serviceIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            servicePendingIntent
        )

        Toast.makeText(
            context,
            R.string.daily_release_reminder_on,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun cancelDailyReleaseReminderAlarm(context: Context) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val requestCode = DAILY_RELEASE_REMINDER_ID
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0)

        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)
        Toast.makeText(
            context,
            R.string.daily_release_reminder_off,
            Toast.LENGTH_SHORT
        ).show()
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
