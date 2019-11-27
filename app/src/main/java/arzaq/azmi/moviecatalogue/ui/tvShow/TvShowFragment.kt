package arzaq.azmi.moviecatalogue.ui.tvShow


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import arzaq.azmi.moviecatalogue.R
import arzaq.azmi.moviecatalogue.adapter.Adapter
import arzaq.azmi.moviecatalogue.entity.DataModelFilm
import arzaq.azmi.moviecatalogue.ui.detail.DetailFragment
import kotlinx.android.synthetic.main.fragment_movie.*
import kotlinx.android.synthetic.main.fragment_tv_show.*
import java.util.Locale


class TvShowFragment : Fragment() {

    private lateinit var tvShowViewModel: TvShowViewModel
    private lateinit var adapter: Adapter
    private lateinit var language: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tv_show, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        language = Locale.getDefault().language

        setProgressBarVisibility(true)

        adapter = Adapter()
        adapter.notifyDataSetChanged()

        rv_tv_show_container.layoutManager = LinearLayoutManager(activity)
        rv_tv_show_container.adapter = adapter

        tvShowViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(TvShowViewModel::class.java)

        tvShowViewModel.setData(language)

        tvShowViewModel.getData().observe(this, Observer { dataItems ->

            if (dataItems != null) {
                adapter.setData(dataItems)

                adapter.setOnItemClickCallback(object : Adapter.OnItemClickCallback {
                    override fun onItemClicked(dataModelFilm: DataModelFilm) {

                        val args = Bundle().apply {
                            putInt(
                                DetailFragment.EXTRA_ID,
                                dataModelFilm.id
                            )
                            putString(
                                DetailFragment.EXTRA_TYPE,
                                "tv"
                            )
                            putString(
                                DetailFragment.EXTRA_LANG,
                                language
                            )
                        }

                        val navController = Navigation.findNavController(view)

                        navController.navigate(
                            R.id.action_navigation_tv_show_to_movie_tv_show_detail,
                            args
                        )
                    }
                })

                setProgressBarVisibility(false)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.search_item)
        val searchView = searchItem?.actionView as SearchView

        searchView.isFocusable = false
        searchView.queryHint = "Search"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {


            override fun onQueryTextSubmit(query: String): Boolean {
                setProgressBarVisibility(true)

                Log.d("QUERY", "submitted")

                tvShowViewModel.setSearchData(language, query)

                Log.d("QUERY", "after search")

                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                Log.d("QUERY", "changed")
                if (query.isEmpty()) {
                    setProgressBarVisibility(true)
                    tvShowViewModel.setData(language)
                }

                return false
            }
        })
    }

    private fun setProgressBarVisibility(status: Boolean) {
        if (status) tvShowProgressBar.visibility = View.VISIBLE else tvShowProgressBar.visibility = View.GONE
    }
}
