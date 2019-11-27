package arzaq.azmi.moviecatalogue.db

import android.net.Uri
import android.provider.BaseColumns

object DatabaseContract {

    const val AUTHORITY = "arzaq.azmi.moviecatalogue"
    const val SCHEME = "content"

    internal class FilmColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "film"
            const val ID = "_id"
            const val NAME = "name"
            const val DESCRIPTION = "description"
            const val IMG = "img"
            const val IMDB = "imdb"
            const val RELEASE_DATE = "release_date"
            const val CATEGORIES = "categories"
            const val LANGUAGE = "language"
            const val TYPE = "type"

            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build()
        }
    }
}