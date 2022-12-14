package arzaq.azmi.favoriteMovie.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.net.toUri
import arzaq.azmi.favoriteMovie.R

class FavoriteWidget : AppWidgetProvider() {

    companion object {

        private const val TOAST_ACTION = "arzaq.azmi.favoriteMovie.widget.action.TOAST_ACTION"
        const val EXTRA_ITEM = "arzaq.azmi.favoriteMovie.EXTRA_ITEM"
        private const val ACTION_UPDATE = AppWidgetManager.ACTION_APPWIDGET_UPDATE

        private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, appWidgetIds: IntArray) {
            val intent = Intent(context, StackWidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

            val views = RemoteViews(context.packageName, R.layout.favorite_widget)
            views.setRemoteAdapter(R.id.stack_view, intent)
            views.setEmptyView(R.id.stack_view, R.id.empty_view)

            val toastIntent = Intent(context, FavoriteWidget::class.java)
            toastIntent.action = TOAST_ACTION
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

            val toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, appWidgetIds)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action != null) {
            when (intent.action) {
                TOAST_ACTION -> {
                    val itemName = intent.getStringExtra(EXTRA_ITEM)
                    Toast.makeText(context, itemName, Toast.LENGTH_SHORT).show()
                }

                ACTION_UPDATE -> {
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    val componentName = ComponentName(context, FavoriteWidget::class.java)

                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(componentName), R.id.stack_view)
                }
            }
        }
    }

    fun sendRefreshBroadcast(context: Context) {
        val intent = Intent(ACTION_UPDATE)
        intent.component = ComponentName(context, FavoriteWidget::class.java)
        context.sendBroadcast(intent)
    }
}