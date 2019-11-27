package arzaq.azmi.favoriteMovie.ui.movie

import android.content.Context
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import arzaq.azmi.favoriteMovie.R
import arzaq.azmi.favoriteMovie.StaticContext
import arzaq.azmi.favoriteMovie.adapter.Adapter
import arzaq.azmi.favoriteMovie.db.DatabaseContract.FilmColumns.Companion.CONTENT_URI
import arzaq.azmi.favoriteMovie.entity.DataModelFilm
import arzaq.azmi.favoriteMovie.ui.detail.FavoriteDetailFragment
import arzaq.azmi.favoriteMovie.widget.FavoriteWidget
import kotlinx.android.synthetic.main.fragment_movie.*
import java.util.Locale

class FavoriteMovieFragment : Fragment() {

    private lateinit var favoriteMovieViewModel: FavoriteMovieViewModel
    private lateinit var adapter: Adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val language = Locale.getDefault().language

        movieProgressBar.visibility = View.VISIBLE

        adapter = Adapter()
        adapter.notifyDataSetChanged()

        rv_movie_container.layoutManager = LinearLayoutManager(activity)
        rv_movie_container.adapter = adapter

        favoriteMovieViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(FavoriteMovieViewModel::class.java)

        favoriteMovieViewModel.setData(language, context as Context)
        favoriteMovieViewModel.getData().observe(this, Observer { dataItems ->
            if (dataItems != null) {
                adapter.setData(dataItems)

                adapter.setOnItemClickCallback(object : Adapter.OnItemClickCallback {
                    override fun onItemClicked(dataModelFilm: DataModelFilm) {

                        val args = Bundle().apply {
                            putParcelable(
                                FavoriteDetailFragment.EXTRA_DATA,
                                dataModelFilm
                            )
                        }

                        val navController = Navigation.findNavController(view)

                        navController.navigate(
                            R.id.action_navigation_movie_to_detail,
                            args
                        )
                    }
                })

                movieProgressBar.visibility = View.GONE
            }
        })

        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()

        val handler = Handler(handlerThread.looper)
        val myObserver = object : ContentObserver(handler) {
            override fun onChange(self: Boolean) {
                favoriteMovieViewModel.setData(language, StaticContext.appContext!!)
                FavoriteWidget().sendRefreshBroadcast(StaticContext.appContext!!)
            }
        }

        activity!!.contentResolver.registerContentObserver(CONTENT_URI, true, myObserver)
    }

}
