package arzaq.azmi.moviecatalogue.ui.tvShow


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arzaq.azmi.moviecatalogue.entity.DataModelFilm
import arzaq.azmi.moviecatalogue.ui.movie.MovieViewModel
import com.androidnetworking.error.ANError
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import org.json.JSONObject
import com.androidnetworking.interfaces.JSONObjectRequestListener


class TvShowViewModel : ViewModel() {

    private val data by lazy {
        MutableLiveData<ArrayList<DataModelFilm>>()
    }

    internal fun setData(language: String) {

        val dataList = ArrayList<DataModelFilm>()

        AndroidNetworking.get("https://api.themoviedb.org/3/tv/popular?api_key=$API_KEY&language=${if (language == "in") "id" else "en"}&page=1")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    val jsonArray = response.getJSONArray("results")

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val dataItems = DataModelFilm()

                        dataItems.name = jsonObject.optString("name")
                        dataItems.description = jsonObject.optString("overview")
                        dataItems.img =
                            "https://image.tmdb.org/t/p/w185${jsonObject.optString("poster_path")}"
                        dataItems.id = jsonObject.optInt("id")

                        dataList.add(dataItems)
                    }

                    data.postValue(dataList)
                }

                override fun onError(error: ANError) {
                    // handle error
                }
            })
    }

    internal fun setSearchData(language: String, query: String) {
        val dataList = ArrayList<DataModelFilm>()
        Log.d("QUERY", "triggered")
        AndroidNetworking.get("https://api.themoviedb.org/3/search/tv?api_key=$API_KEY&language=$language&query=$query")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    val jsonArray = response.getJSONArray("results")

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val dataItems = DataModelFilm()

                        dataItems.name = jsonObject.optString("name")
                        dataItems.description = jsonObject.optString("overview")
                        dataItems.img =
                            "https://image.tmdb.org/t/p/w185${jsonObject.optString("poster_path")}"
                        dataItems.id = jsonObject.optInt("id")

                        dataList.add(dataItems)
                    }
                    Log.d("DATA", "posted")
                    data.postValue(dataList)
                    Log.d("DATA", data.hasObservers().toString())
                }

                override fun onError(error: ANError) {
                    Log.d("ERROR", error.errorDetail)
                }
            })
    }

    internal fun getData(): LiveData<ArrayList<DataModelFilm>> {
        return data
    }

    companion object {
        private const val API_KEY = "5bb04aa026762c71c1affb68fe1bc79d"
    }
}