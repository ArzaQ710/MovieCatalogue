package arzaq.azmi.favoriteMovie.ui.tvShow


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arzaq.azmi.favoriteMovie.db.DatabaseContract
import arzaq.azmi.favoriteMovie.entity.DataModelFilm
import arzaq.azmi.favoriteMovie.helper.MappingHelper
import com.androidnetworking.error.ANError
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import org.json.JSONObject
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.Language


class FavoriteTvShowViewModel : ViewModel() {

    private val data = MutableLiveData<ArrayList<DataModelFilm>>()

    internal fun setData(language: String, context: Context) {

        GlobalScope.launch(Dispatchers.Main) {
            val deferredFilms = async(Dispatchers.IO) {
                context.contentResolver?.query(DatabaseContract.FilmColumns.CONTENT_URI, null, "type = ? AND language = ?", arrayOf("tv", language), null)!!
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