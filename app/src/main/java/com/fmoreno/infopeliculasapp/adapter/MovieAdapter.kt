package com.fmoreno.infopeliculasapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fmoreno.infopeliculasapp.R
import com.fmoreno.infopeliculasapp.model.Movie

class MovieAdapter (movies: MutableList<Movie>, oItemClickListener: OnItemClickListener) : RecyclerView.Adapter<MovieViewHolder>()  {

    private val mMovies: MutableList<Movie> = movies
    private val onItemClickListener: OnItemClickListener? = oItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view: View = LayoutInflater.from(
            parent.context
        ).inflate(R.layout.movie_list_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie: Movie = mMovies.get(position)
        holder.cardMovie?.setOnClickListener { onItemClickListener!!.onItemClick(movie) }
        holder.bindLaunch(mMovies[position])
    }

    override fun getItemCount(): Int {
        return mMovies.size
    }
}