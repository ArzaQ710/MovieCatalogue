package arzaq.azmi.moviecatalogue.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import arzaq.azmi.moviecatalogue.db.DatabaseContract.AUTHORITY
import arzaq.azmi.moviecatalogue.db.DatabaseContract.FilmColumns.Companion.CONTENT_URI
import arzaq.azmi.moviecatalogue.db.DatabaseContract.FilmColumns.Companion.TABLE_NAME
import arzaq.azmi.moviecatalogue.db.FavoriteHelper
import java.util.Locale

class FavoriteProvider : ContentProvider() {

    private lateinit var language: String

    companion object {
        private const val FILM = 1
        private const val FILM_ID = 2
        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        private lateinit var favoriteHelper: FavoriteHelper

        init {
            sUriMatcher.addURI(AUTHORITY, TABLE_NAME, FILM)
            sUriMatcher.addURI(AUTHORITY, "$TABLE_NAME/#", FILM_ID)
        }
    }

    override fun onCreate(): Boolean {
        language = Locale.getDefault().language
        favoriteHelper = FavoriteHelper.getInstance(context as Context)
        favoriteHelper.open()
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String,
        selectionArgs: Array<String>, sortOrder: String?
    ): Cursor? {
        Log.d("DEBUG", "Query")
        return when (sUriMatcher.match(uri)) {
            FILM -> favoriteHelper.query(selection, selectionArgs)
            FILM_ID -> favoriteHelper.queryById(uri.lastPathSegment!!.toInt(), selection, language)
            else -> null
        }
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val added: Long = when (FILM) {
            sUriMatcher.match(uri) -> favoriteHelper.insert(values)
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)

        return Uri.parse("$CONTENT_URI/$added")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        TODO("not implemented")
    }

    override fun delete(uri: Uri?, selection: String, selectionArgs: Array<String>): Int {
        val deleted: Int = favoriteHelper.deleteById(selection, selectionArgs)

        context?.contentResolver?.notifyChange(CONTENT_URI, null)

        return deleted
    }
}
