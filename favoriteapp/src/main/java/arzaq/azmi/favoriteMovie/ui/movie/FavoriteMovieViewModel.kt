package arzaq.azmi.favoriteMovie.ui.movie


import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arzaq.azmi.favoriteMovie.db.DatabaseContract.FilmColumns.Companion.CONTENT_URI
import arzaq.azmi.favoriteMovie.entity.DataModelFilm
import arzaq.azmi.favoriteMovie.helper.MappingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class FavoriteMovieViewModel : ViewModel() {

    private val data = MutableLiveData<ArrayList<DataModelFilm>>()

    internal fun setData(language: String, context: Context) {

        GlobalScope.launch(Dispatchers.Main) {
            val deferredFilms = async(Dispatchers.IO) {
                context.contentResolver?.query(CONTENT_URI, null, "type = ? AND language = ?", arrayOf("movie", language), null)!!
                    .use {
                        MappingHelper.mapCursorToArrayList(it)
                    }

            }
            val films = deferredFilms.await()
            data.postValue(films)
        }
    }

    internal fun getData(): LiveData<ArrayList<DataModelFilm>> {
        return data
    }
}