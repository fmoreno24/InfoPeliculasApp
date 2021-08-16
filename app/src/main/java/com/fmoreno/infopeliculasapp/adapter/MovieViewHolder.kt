package com.fmoreno.infopeliculasapp.adapter

import android.content.res.Resources
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fmoreno.infopeliculasapp.R
import com.fmoreno.infopeliculasapp.model.Movie
import com.fmoreno.infopeliculasapp.utils.ImageLoadingListener

class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var cardMovie: CardView? = itemView.findViewById(R.id.cardMovie)
    fun bindLaunch(movie: Movie) {
        val resources = itemView.resources
        setMoviePoster(movie)
        bindNameAndDate(movie, resources)
    }

    private fun setMoviePoster(movie: Movie) {
        val progressView = itemView.findViewById<View>(R.id.moviePosterProgress).apply { visibility = View.VISIBLE }
        val loadingListener = ImageLoadingListener(progressView)
        val posterPath = "http://image.tmdb.org/t/p/w185" + movie.poster_path

        Glide.with(itemView)
            .load(posterPath)
            .error(R.drawable.ic_launcher_foreground)
            .fallback(R.drawable.ic_launcher_foreground)
            .listener(loadingListener)
            .into(itemView.findViewById(R.id.moviePoster))
    }

    private fun bindNameAndDate(movie: Movie, resources: Resources) {
        itemView.findViewById<TextView>(R.id.title).text = movie.title
    }
}