package arzaq.azmi.moviecatalogue

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import arzaq.azmi.moviecatalogue.notification.AlarmReceiver
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var alarmReceiver: AlarmReceiver

    companion object {
        const val DAILY_RELEASE_REMINDER = "daily_release_reminder"
        const val RETURN_TO_APP_REMINDER = "return_to_app_reminder"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        alarmReceiver = AlarmReceiver()

        tglbtn_daily_release_reminder.isChecked = isChecked(DAILY_RELEASE_REMINDER)
        tglbtn_return_to_app_reminder.isChecked = isChecked(RETURN_TO_APP_REMINDER)

        tglbtn_daily_release_reminder.setOnClickListener(this)
        tglbtn_return_to_app_reminder.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tglbtn_daily_release_reminder -> {
                if (isChecked(DAILY_RELEASE_REMINDER)) {
                    stopDailyReleaseReminder()
                    getSharedPreferences(DAILY_RELEASE_REMINDER, MODE_PRIVATE).edit()
                        .putBoolean("isChecked", false).apply()
                } else {
                    startDailyReleaseReminder()
                    getSharedPreferences(DAILY_RELEASE_REMINDER, MODE_PRIVATE).edit()
                        .putBoolean("isChecked", true).apply()
                }
            }

            R.id.tglbtn_return_to_app_reminder -> {
                if (isChecked(RETURN_TO_APP_REMINDER)) {
                    stopReturnToAppReminder()
                    getSharedPreferences(RETURN_TO_APP_REMINDER, MODE_PRIVATE).edit()
                        .putBoolean("isChecked", false).apply()
                } else {
                    startReturnToAppReminder()
                    getSharedPreferences(RETURN_TO_APP_REMINDER, MODE_PRIVATE).edit()
                        .putBoolean("isChecked", true).apply()
                }
            }
        }
    }

    private fun isChecked(prefName: String): Boolean {
        return getSharedPreferences(prefName, MODE_PRIVATE).getBoolean("isChecked", false)
    }

    private fun startReturnToAppReminder() {
        alarmReceiver.setReturnToAppReminderAlarm(this)
    }

    private fun stopReturnToAppReminder() {
        alarmReceiver.cancelReturnToAppReminderAlarm(this)
    }

    private fun startDailyReleaseReminder() {
        alarmReceiver.setDailyReleaseReminderAlarm(this)
    }

    private fun stopDailyReleaseReminder() {
        alarmReceiver.cancelDailyReleaseReminderAlarm(this)
    }
}
