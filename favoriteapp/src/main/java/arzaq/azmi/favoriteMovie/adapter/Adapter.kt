package arzaq.azmi.favoriteMovie.adapter

import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import arzaq.azmi.favoriteMovie.R
import arzaq.azmi.favoriteMovie.entity.DataModelFilm
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_item.view.*

class Adapter : RecyclerView.Adapter<Adapter.ViewHolder>() {

    private val data = ArrayList<DataModelFilm>()

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(data[holder.adapterPosition]) }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(dataModelFilm: DataModelFilm) {
            with(itemView) {
                Glide.with(itemView.context)
                    .load(dataModelFilm.img)
                    .into(this.img_movie_photo)

                tv_name.text = dataModelFilm.name
                tv_movie_description.text = dataModelFilm.description
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(dataModelFilm: DataModelFilm)
    }

    fun setData(films: ArrayList<DataModelFilm>) {
        data.clear()
        data.addAll(films)
        notifyDataSetChanged()
    }
}