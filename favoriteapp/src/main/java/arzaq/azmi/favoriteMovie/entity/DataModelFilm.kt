package arzaq.azmi.favoriteMovie.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataModelFilm(
    var id: Int = 0,
    var name: String = "",
    var description: String = "",
    var img: String = "",
    var imdb: String = "",
    var releaseDate: String = "",
    var categories: String = "",
    var language: String = "",
    var type: String = ""
) : Parcelable