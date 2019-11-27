package arzaq.azmi.favoriteMovie.helper


import android.database.Cursor
import arzaq.azmi.favoriteMovie.db.DatabaseContract
import arzaq.azmi.favoriteMovie.entity.DataModelFilm


object MappingHelper {

    fun mapCursorToArrayList(filmsCursor: Cursor): ArrayList<DataModelFilm> {
        val filmsList = ArrayList<DataModelFilm>()

        filmsCursor.moveToPosition(-1)
        while (filmsCursor.moveToNext()) {
            val id =
                filmsCursor.getInt(filmsCursor.getColumnIndexOrThrow(DatabaseContract.FilmColumns.ID))
            val name =
                filmsCursor.getString(filmsCursor.getColumnIndexOrThrow(DatabaseContract.FilmColumns.NAME))
            val description =
                filmsCursor.getString(filmsCursor.getColumnIndexOrThrow(DatabaseContract.FilmColumns.DESCRIPTION))
            val img =
                filmsCursor.getString(filmsCursor.getColumnIndexOrThrow(DatabaseContract.FilmColumns.IMG))
            val imdb =
                filmsCursor.getString(filmsCursor.getColumnIndexOrThrow(DatabaseContract.FilmColumns.IMDB))
            val releaseDate =
                filmsCursor.getString(filmsCursor.getColumnIndexOrThrow(DatabaseContract.FilmColumns.RELEASE_DATE))
            val categories =
                filmsCursor.getString(filmsCursor.getColumnIndexOrThrow(DatabaseContract.FilmColumns.CATEGORIES))
            val language =
                filmsCursor.getString(filmsCursor.getColumnIndexOrThrow(DatabaseContract.FilmColumns.LANGUAGE))
            val type =
                filmsCursor.getString(filmsCursor.getColumnIndexOrThrow(DatabaseContract.FilmColumns.TYPE))

            filmsList.add(
                DataModelFilm(
                    id,
                    name,
                    description,
                    img,
                    imdb,
                    releaseDate,
                    categories,
                    language,
                    type
                )
            )
        }

        return filmsList
    }
}