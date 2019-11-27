package arzaq.azmi.favoriteMovie.ui.detail

import android.content.ContentValues
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import arzaq.azmi.favoriteMovie.R
import arzaq.azmi.favoriteMovie.db.DatabaseContract.FilmColumns.Companion.CATEGORIES
import arzaq.azmi.favoriteMovie.db.DatabaseContract.FilmColumns.Companion.CONTENT_URI
import arzaq.azmi.favoriteMovie.db.DatabaseContract.FilmColumns.Companion.DESCRIPTION
import arzaq.azmi.favoriteMovie.db.DatabaseContract.FilmColumns.Companion.ID
import arzaq.azmi.favoriteMovie.db.DatabaseContract.FilmColumns.Companion.IMDB
import arzaq.azmi.favoriteMovie.db.DatabaseContract.FilmColumns.Companion.IMG
import arzaq.azmi.favoriteMovie.db.DatabaseContract.FilmColumns.Companion.LANGUAGE
import arzaq.azmi.favoriteMovie.db.DatabaseContract.FilmColumns.Companion.NAME
import arzaq.azmi.favoriteMovie.db.DatabaseContract.FilmColumns.Companion.RELEASE_DATE
import arzaq.azmi.favoriteMovie.db.DatabaseContract.FilmColumns.Companion.TYPE
import arzaq.azmi.favoriteMovie.entity.DataModelFilm
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_detail.*

class FavoriteDetailFragment : Fragment() {

    private lateinit var menu: Menu
    private lateinit var data: DataModelFilm

    companion object {
        const val EXTRA_DATA = "extra_data"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailProgressBar.visibility = View.VISIBLE

        data = arguments!!.getParcelable(EXTRA_DATA)!!

        setView(data)

        detailProgressBar.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.detail_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val favIcon: Drawable =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_favorite_24dp, null)!!
        val favIconWrap: Drawable = DrawableCompat.wrap(favIcon)

        if (isFavorite(data.id, data.type, data.language)) {
            DrawableCompat.setTint(favIconWrap, Color.RED)
        } else {
            DrawableCompat.setTint(favIconWrap, Color.WHITE)
        }

        menu.getItem(0).icon = favIconWrap
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.fav_icon -> {
                if (isFavorite(data.id, data.type, data.language)) {
                    val result = activity?.contentResolver?.delete(CONTENT_URI, "_id = ? AND type = ? AND language = ?", arrayOf(data.id.toString(), data.type, data.language))

                    if (result != 0) {
                        Toast.makeText(
                            context,
                            R.string.deleted,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            R.string.delete_fail,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val values = ContentValues()

                    values.put(ID, data.id)
                    values.put(NAME, data.name)
                    values.put(DESCRIPTION, data.description)
                    values.put(IMG, data.img)
                    values.put(IMDB, data.imdb)
                    values.put(RELEASE_DATE, data.releaseDate)
                    values.put(CATEGORIES, data.categories)
                    values.put(LANGUAGE, data.language)
                    values.put(TYPE, data.type)

                    val result = activity?.contentResolver?.insert(
                        CONTENT_URI,
                        values
                    )?.lastPathSegment!!.toLong()

                    if (result > 0) {
                        Toast.makeText(
                            context,
                            R.string.added,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            R.string.add_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        onPrepareOptionsMenu(menu)

        return super.onOptionsItemSelected(item)
    }

    private fun isFavorite(id: Int, type: String, language: String): Boolean {
        val query = context?.contentResolver?.query(
            CONTENT_URI,
            null,
            "_id = ? AND type = ? AND language = ?",
            arrayOf(id.toString(), type, language),
            null
        )!!.count
        if (query > 0) {
            return true
        }
        return false
    }

    private fun setView(data: DataModelFilm) {
        Glide.with(this)
            .load(data.img)
            .into(iv_img)

        tv_dtl_name.text = data.name
        tv_dtl_description.text = data.description
        tv_rating.text = data.imdb
        tv_release.text = data.releaseDate
        tv_categories.text = data.categories
    }
}
