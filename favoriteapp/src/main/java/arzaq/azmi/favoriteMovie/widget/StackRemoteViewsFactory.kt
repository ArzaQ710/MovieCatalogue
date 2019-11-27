package arzaq.azmi.favoriteMovie.widget

import android.content.Context
import android.content.Intent
import android.os.Binder
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import arzaq.azmi.favoriteMovie.db.DatabaseContract.FilmColumns.Companion.CONTENT_URI
import arzaq.azmi.favoriteMovie.entity.DataModelFilm
import arzaq.azmi.favoriteMovie.helper.MappingHelper
import java.util.Locale
import kotlin.collections.ArrayList
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import arzaq.azmi.favoriteMovie.R

class StackRemoteViewsFactory(private val context: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private lateinit var widgetItems: ArrayList<DataModelFilm>

    override fun onCreate() {

    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getItemId(position: Int): Long = 0

    override fun onDataSetChanged() {

        val identityToken = Binder.clearCallingIdentity()
        val language = Locale.getDefault().language

        widgetItems = context.contentResolver?.query(
            CONTENT_URI,
            null,
            "language = ?",
            arrayOf(language),
            null
        )!!
            .use {
                MappingHelper.mapCursorToArrayList(it)
            }

        Binder.restoreCallingIdentity(identityToken)
    }

    override fun hasStableIds(): Boolean = false

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.widget_item)

        try {

            val bitmap = Glide.with(context)
                .asBitmap()
                .load(widgetItems[position].img)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(30)))
                .submit(512, 512)
                .get()

            rv.setImageViewBitmap(R.id.widget_item_img, bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val extras = bundleOf(
            FavoriteWidget.EXTRA_ITEM to widgetItems[position].name
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.widget_item_img, fillInIntent)

        return rv
    }

    override fun getCount(): Int = widgetItems.size

    override fun getViewTypeCount(): Int = 1

    override fun onDestroy() {

    }
}