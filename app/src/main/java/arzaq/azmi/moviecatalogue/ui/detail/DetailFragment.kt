package arzaq.azmi.moviecatalogue.ui.detail

import android.content.ContentValues
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import arzaq.azmi.moviecatalogue.R
import arzaq.azmi.moviecatalogue.db.DatabaseContract.FilmColumns.Companion.CATEGORIES
import arzaq.azmi.moviecatalogue.db.DatabaseContract.FilmColumns.Companion.CONTENT_URI
import arzaq.azmi.moviecatalogue.db.DatabaseContract.FilmColumns.Companion.DESCRIPTION
import arzaq.azmi.moviecatalogue.db.DatabaseContract.FilmColumns.Companion.IMDB
import arzaq.azmi.moviecatalogue.db.DatabaseContract.FilmColumns.Companion.IMG
import arzaq.azmi.moviecatalogue.db.DatabaseContract.FilmColumns.Companion.LANGUAGE
import arzaq.azmi.moviecatalogue.db.DatabaseContract.FilmColumns.Companion.NAME
import arzaq.azmi.moviecatalogue.db.DatabaseContract.FilmColumns.Companion.RELEASE_DATE
import arzaq.azmi.moviecatalogue.db.DatabaseContract.FilmColumns.Companion.ID
import arzaq.azmi.moviecatalogue.db.DatabaseContract.FilmColumns.Companion.TYPE
import arzaq.azmi.moviecatalogue.db.FavoriteHelper
import arzaq.azmi.moviecatalogue.entity.DataModelFilm

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : Fragment() {

    private var extraId: Int = 0
    private var extraLanguage: String = ""
    private var extraType: String = ""
    private lateinit var menu: Menu
    private lateinit var data: DataModelFilm
    private lateinit var favoriteHelper: FavoriteHelper
    private lateinit var detailViewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        favoriteHelper = FavoriteHelper.getInstance(activity!!.applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragm
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movieTvShowDetailProgressBar.visibility = View.VISIBLE

        extraId = arguments!!.getInt(EXTRA_ID)
        extraLanguage = arguments!!.getString(EXTRA_LANG)!!
        extraType = arguments!!.getString(EXTRA_TYPE)!!

        detailViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(DetailViewModel::class.java)

        detailViewModel.setData(extraId, extraType, extraLanguage)
        detailViewModel.getData().observe(this, Observer { data ->
            this.data = data
            if (data != null) {
                Glide.with(this)
                    .load(data.img)
                    .into(iv_img)

                tv_dtl_name.text = data.name
                tv_dtl_description.text = data.description
                tv_rating.text = data.imdb
                tv_release.text = data.releaseDate
                tv_categories.text = data.categories
            }

            movieTvShowDetailProgressBar.visibility = View.GONE
        })
    }

    companion object {
        const val EXTRA_ID = "extra_id"
        const val EXTRA_TYPE = "extra_type"
        const val EXTRA_LANG = "extra_lang"
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

        if (isFavorite(extraId, extraType, extraLanguage)) {
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

                    val result = activity?.contentResolver?.insert(CONTENT_URI, values)?.lastPathSegment!!.toLong()

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
        val query = context?.contentResolver?.query(CONTENT_URI, null, "_id = ? AND type = ? AND language = ?", arrayOf(id.toString(), type, language), null)!!.count
        if (query > 0) {
            return true
        }
        return false
    }
}
