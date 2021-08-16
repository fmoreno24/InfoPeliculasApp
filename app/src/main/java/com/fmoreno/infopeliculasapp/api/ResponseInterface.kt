package com.fmoreno.infopeliculasapp.api

import MovieResponse
import com.fmoreno.infopeliculasapp.model.Movie

interface ResponseInterface {
    fun getMovies(movies: MovieResponse?)
    fun getMoviesFail()
}