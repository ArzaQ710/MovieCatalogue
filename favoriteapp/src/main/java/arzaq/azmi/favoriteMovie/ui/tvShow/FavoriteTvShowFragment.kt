package arzaq.azmi.favoriteMovie.ui.tvShow

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
import arzaq.azmi.favoriteMovie.db.DatabaseContract
import arzaq.azmi.favoriteMovie.entity.DataModelFilm
import arzaq.azmi.favoriteMovie.ui.detail.FavoriteDetailFragment
import arzaq.azmi.favoriteMovie.widget.FavoriteWidget
import kotlinx.android.synthetic.main.fragment_tv_show.*
import java.util.Locale

class FavoriteTvShowFragment : Fragment() {

    private lateinit var favoriteTvShowViewModel: FavoriteTvShowViewModel
    private lateinit var adapter: Adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tv_show, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val language = Locale.getDefault().language

        tvShowProgressBar.visibility = View.VISIBLE

        adapter = Adapter()
        adapter.notifyDataSetChanged()

        rv_tv_show_container.layoutManager = LinearLayoutManager(activity)
        rv_tv_show_container.adapter = adapter

        favoriteTvShowViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(FavoriteTvShowViewModel::class.java)

        favoriteTvShowViewModel.setData(language, context as Context)
        favoriteTvShowViewModel.getData().observe(this, Observer { dataItems ->
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
                            R.id.action_navigation_tv_show_to_detail,
                            args
                        )
                    }
                })

                tvShowProgressBar.visibility = View.GONE
            }
        })

        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()

        val handler = Handler(handlerThread.looper)
        val myObserver = object : ContentObserver(handler) {
            override fun onChange(self: Boolean) {
                favoriteTvShowViewModel.setData(language, StaticContext.appContext!!)
                FavoriteWidget().sendRefreshBroadcast(StaticContext.appContext!!)
            }
        }

        activity!!.contentResolver.registerContentObserver(
            DatabaseContract.FilmColumns.CONTENT_URI,
            true,
            myObserver
        )
    }
}
