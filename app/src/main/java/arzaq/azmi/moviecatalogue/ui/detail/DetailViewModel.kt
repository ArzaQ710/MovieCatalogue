package arzaq.azmi.moviecatalogue.ui.detail


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arzaq.azmi.moviecatalogue.entity.DataModelFilm
import com.androidnetworking.error.ANError
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import org.json.JSONObject
import com.androidnetworking.interfaces.JSONObjectRequestListener


class DetailViewModel : ViewModel() {

    private val data = MutableLiveData<DataModelFilm>()

    internal fun setData(id: Int, type: String, lang: String) {
        AndroidNetworking.get("https://api.themoviedb.org/3/$type/$id?api_key=$API_KEY&language=${if (lang == "in") "id" else "en"}")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    val dataItems = DataModelFilm()

                    dataItems.name = response.optString(if (type == "movie") "title" else "name")
                    dataItems.description = response.optString("overview")
                    dataItems.img =
                        "https://image.tmdb.org/t/p/w185${response.optString("poster_path")}"
                    dataItems.imdb = response.optString("vote_average")
                    dataItems.releaseDate = response.optString(if (type == "movie") "release_date" else "first_air_date")
                    dataItems.id = response.optInt("id")

                    var categoryStr = ""
                    val categoryArr = response.getJSONArray("genres")
                    for (i in 0 until categoryArr.length()) {
                        val categoryObj = categoryArr.getJSONObject(i)
                        categoryStr += categoryObj.optString("name") + " "
                    }

                    dataItems.categories = categoryStr
                    dataItems.language = lang
                    dataItems.type = type

                    data.postValue(dataItems)
                }

                override fun onError(error: ANError) {
                    // handle error
                }
            })
    }

    internal fun getData(): LiveData<DataModelFilm> {
        return data
    }

    companion object {
        private const val API_KEY = "5bb04aa026762c71c1affb68fe1bc79d"
    }
}