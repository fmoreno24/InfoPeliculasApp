package com.fmoreno.infopeliculasapp.api

import MovieResponse
import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Endpoints(responseInterface: ResponseInterface, context: Context) {
    val mContext = context

    private var responseInterface: ResponseInterface = responseInterface

    val URL_BASE = "https://api.themoviedb.org/3"

    val GET_MOVIES = "/movie/popular?api_key=3f4ccf9c8108bb8d03e86f9123add311"

    val gson = Gson()
    var requestQueue = Volley.newRequestQueue(mContext)

    /**
     * Metodo para obtener las peliculas utilizando volley
     */
    fun getMovies(){
        val stringReq = StringRequest(
            Request.Method.POST, URL_BASE + GET_MOVIES,
            Response.Listener<String> { response ->
                var strResp = response.toString()
                responseInterface.getMovies(toListMovie(response))
                Log.d("getMoviesEndPoints", strResp);
            },
            Response.ErrorListener {
                responseInterface.getMoviesFail()
            Log.e("ErrorListener",it.toString())})

        requestQueue?.add(stringReq)
    }

    /**
     * Metodo para convertir el listado de peliculas
     */
    fun toListMovie(string: String?): MovieResponse {
        return gson.fromJson<MovieResponse>(
            string,
            object : TypeToken<MovieResponse?>() {}.type
        )
    }
}