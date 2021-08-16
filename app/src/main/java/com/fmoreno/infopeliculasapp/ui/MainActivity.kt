package com.fmoreno.infopeliculasapp.ui


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.Volley
import com.fmoreno.infopeliculasapp.R
import com.fmoreno.infopeliculasapp.adapter.MovieAdapter
import com.fmoreno.infopeliculasapp.adapter.OnItemClickListener
import com.fmoreno.infopeliculasapp.api.Endpoints
import com.fmoreno.infopeliculasapp.api.ResponseInterface
import com.fmoreno.infopeliculasapp.model.Movie
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import java.util.ArrayList
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.TextView
import com.bumptech.glide.Glide
import com.fmoreno.infopeliculasapp.utils.DateUtils
import com.fmoreno.infopeliculasapp.utils.ImageLoadingListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

import MovieResponse
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.crashlytics.FirebaseCrashlytics


class MainActivity : AppCompatActivity(), ResponseInterface, OnItemClickListener, TextWatcher {
    /**
     * Variable globar que determina la conexión a internet
     */
    companion object {
        var isNetworkConnected = false
    }
    private var adapter: MovieAdapter? = null
    private var endpoints: Endpoints? = null

    private var txtSearch: TextInputLayout? = null
    private var progressBar: ProgressBar? = null
    private var recyclerView: RecyclerView? = null
    private var include: View? = null
    private var iv_refresh: ImageView? = null

    private var DialogCustomMovie: AlertDialog? = null

    var mMovies: MutableList<Movie> = listOf<Movie>().toMutableList()
    var movies_all: MutableList<Movie> = listOf<Movie>().toMutableList()

    private var gson: Gson? = null

    var mDatabase = Firebase.database
    val refMovies = mDatabase.getReference("Movies")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialObjects()
        initOperation()
    }

    /**
     * Fabian Moreno
     * Inicializar los objetos
     */
    fun initialObjects() {
        try{
            endpoints = Endpoints(this, this)
            mMovies = ArrayList<Movie>()
            movies_all = ArrayList<Movie>()
            gson = Gson()
        }catch (ex:Exception){
            Log.e("initialObjects", ex.toString());
            FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }


    /**
     * Fabian Moreno
     * Inicializador de views y consulta de datos
     */
    private fun initOperation(){
        try{
            progressBar = findViewById(R.id.progress_circular)
            progressBar?.setVisibility(View.VISIBLE)

            adapter = MovieAdapter(mMovies, this)

            include = findViewById<View>(R.id.include)
            include?.setVisibility(View.GONE)

            iv_refresh = findViewById(R.id.iv_refresh)
            iv_refresh?.setOnClickListener(){
                getNeworkMovies()
            }

            txtSearch = findViewById(R.id.textInputLayoutSearch)
            txtSearch?.getEditText()?.addTextChangedListener(this)

            recyclerView = findViewById(R.id.recyclerViewSearchResults)
            getNeworkMovies()
            recyclerView?.setHasFixedSize(true)
            recyclerView?.setLayoutManager(LinearLayoutManager(this))
            recyclerView?.setAdapter(adapter)
        }catch (ex:Exception){
            Log.e("initOperation", ex.toString());
            FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    /**
     * Metodo para consultar los datos de las peliculas
     */
    fun getNeworkMovies(){
        try{
            endpoints?.requestQueue = Volley.newRequestQueue(this)
            endpoints?.getMovies()
        }catch (ex:Exception){
            Log.e("getNeworkMovies", ex.toString());
            FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    /**
     * Lanzador del dialog(modal) con la descripción de la pelicula
     */
    override fun onItemClick(movie: Movie?) {
        showDialogCustomMovie(movie)
    }

    /**
     * Metodo que se llama despues de realizar la petición api con resultados
     */
    override fun getMovies(movies: MovieResponse?) {
        try{
            val count = movies?.results?.size
            refMovies.setValue(movies)
            if (!isNetworkConnected){
                Toast.makeText(
                    this,
                    R.string.not_connection,
                    Toast.LENGTH_SHORT
                ).show()
                if(count != null){
                    if(count > 0){
                        getMoviesOffline(movies)
                    } else {
                        include?.visibility = if (count > 0) View.GONE else View.VISIBLE
                        txtSearch?.visibility = if (count > 0) View.VISIBLE else View.GONE
                    }
                }

            } else {
                addMoviesAll(movies?.results as List<Movie>)
                //saveData(users)
                updateList(movies?.results as List<Movie>)
                progressBar?.setVisibility(View.GONE)
                if(count != null){
                    include?.visibility = if (count > 0) View.GONE else View.VISIBLE
                    txtSearch?.visibility = if (count > 0) View.VISIBLE else View.GONE
                }
            }

            Log.d("getMovies", movies.toString())
            if (count != null) {
                txtSearch?.visibility = if (count > 0) View.VISIBLE else View.GONE
                if(count <= 0){
                    include?.visibility = if (count > 0) View.GONE else View.VISIBLE
                    iv_refresh?.visibility = View.VISIBLE
                }
            }
        }catch (ex:Exception){
            Log.e("getMovies", ex.toString());
            FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    /**
     * Metodo que se llama si hay una petición fallida en el api
     */
    override fun getMoviesFail() {
        try{
            val count = mMovies.size
            if (!isNetworkConnected){
                if(count > 0){
                    //getMoviesOffline(mMovies)
                } else {
                    include?.visibility = if (count > 0) View.GONE else View.VISIBLE
                    txtSearch?.visibility = if (count > 0) View.VISIBLE else View.GONE
                }
                Toast.makeText(
                    this,
                    R.string.not_connection,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (count != null) {
                txtSearch?.visibility = if (count > 0) View.VISIBLE else View.GONE
                if(count <= 0){
                    include?.visibility = if (count > 0) View.GONE else View.VISIBLE
                    iv_refresh?.visibility = View.VISIBLE
                }
            }
        }catch (ex:Exception){
            Log.e("getMoviesFail", ex.toString());
            FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    /**
     * Metodo utilizado para cargar los datos que tengo en memoria
     */
    fun getMoviesOffline(movies: MovieResponse?) {
        try{
            refMovies.setValue(movies)
            Log.d("getMovies", movies.toString())
            addMoviesAll(movies?.results as List<Movie>)
            //saveData(users)
            updateList(movies.results as List<Movie>)
            progressBar?.setVisibility(View.GONE)
        }catch (ex:Exception){
            Log.e("getMoviesOffline", ex.toString());
            FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        return
    }

    override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
        val string: String = s.toString()
        filterMovie(string)
    }

    override fun afterTextChanged(p0: Editable?) {
        return
    }

    /**
     * Metodo para realiza la busqueda en el listado
     */
    private fun filterMovie(string: String) {
        try{
            val movies: MutableList<Movie> = ArrayList()
            for (movie in movies_all) {
                if (movie.title?.lowercase()?.contains(string.lowercase()) == true) {
                    movies.add(movie)
                }
            }
            val count = movies.size
            include?.visibility = if (count > 0) View.GONE else View.VISIBLE
            iv_refresh?.visibility = View.GONE
            updateList(movies)
        }catch (ex:Exception){
            Log.e("filterMovie", ex.toString());
            FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    /**
     * Actualización de objeto
     */
    fun addMoviesAll(movies: List<Movie>){
        try {
            this.movies_all.clear()
            this.movies_all.addAll(movies)
        } catch (e: Exception) {
            Log.e("addMoviesAll", e.toString());
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    /**
     * Actualización de objeto y vista con el adapter
     */
    private fun updateList(movies: List<Movie>) {
        try{
            this.mMovies.clear()
            this.mMovies.addAll(movies)
            adapter?.notifyDataSetChanged()
        }catch (ex:Exception){
            Log.e("updateList", ex.toString());
            FirebaseCrashlytics.getInstance().recordException(ex)
        }

    }

    /**
     * Dialog - modal utilizado para mostrar la información de la pelicula seleccionada
     */

    fun showDialogCustomMovie(_movie: Movie?) {
        try {
            try {
                if (DialogCustomMovie != null && DialogCustomMovie!!.isShowing) {
                    DialogCustomMovie!!.dismiss()
                    DialogCustomMovie!!.cancel()
                }
            } catch (ex: java.lang.Exception) {
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
            val alertDialogMessage =
                AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
            val mInflater = LayoutInflater.from(this)
            val mDialogView: View = mInflater.inflate(R.layout.dialog_detail_movie, null)
            val textViewTitle = mDialogView.findViewById<TextView>(R.id.textViewTitle)
            val textViewTagline = mDialogView.findViewById<TextView>(R.id.textViewTagline)
            val textViewDescription = mDialogView.findViewById<TextView>(R.id.textViewDescription)
            val textViewVotes = mDialogView.findViewById<TextView>(R.id.textViewVotes)
            val textViewStars = mDialogView.findViewById<TextView>(R.id.textViewStars)
            val textViewDate = mDialogView.findViewById<TextView>(R.id.textViewDate)

            if (_movie != null) {
                setMoviePoster(_movie, mDialogView)
                setMovieBanner(_movie, mDialogView)
                textViewTitle?.setText(_movie.title)
                textViewTagline?.setText("Description")
                textViewDescription?.setText(_movie.overview)
                textViewVotes?.setText(getLikes(_movie))
                textViewStars?.setText(getStars(_movie))
                textViewDate?.setText(getYear(_movie))
            }


            alertDialogMessage.setView(mDialogView)
            alertDialogMessage.setCancelable(true)
            alertDialogMessage.setPositiveButton(
                "Cerrar"
            ) { dialog, which ->
                dialog.cancel()
            }
            DialogCustomMovie = alertDialogMessage.create()
            // create alert dialog
            DialogCustomMovie?.setCanceledOnTouchOutside(false)
            // show it
            DialogCustomMovie?.getWindow()!!
                .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            DialogCustomMovie?.show()
        } catch (ex: java.lang.Exception) {
            FirebaseCrashlytics.getInstance().recordException(ex)
        }
    }

    /**
     * Metodo para obtener el poster
     */
    private fun setMoviePoster(movie: Movie, mDialogView: View) {
        val progressView = mDialogView.findViewById<View>(R.id.moviePosterProgress).apply { visibility = View.VISIBLE }
        val loadingListener = ImageLoadingListener(progressView)
        val posterPath = "http://image.tmdb.org/t/p/w185" + movie.poster_path

        Glide.with(mDialogView)
            .load(posterPath)
            .error(R.drawable.ic_launcher_foreground)
            .fallback(R.drawable.ic_launcher_foreground)
            .listener(loadingListener)
            .into(mDialogView.findViewById(R.id.imageViewPoster))
    }

    /**
     * Metodo para obtener el banner en el detalle de la pelicula(Modal-dialog)
     */
    private fun setMovieBanner(movie: Movie, mDialogView: View) {
        val progressView = mDialogView.findViewById<View>(R.id.moviePosterProgress).apply { visibility = View.VISIBLE }
        val loadingListener = ImageLoadingListener(progressView)
        val posterPath = "http://image.tmdb.org/t/p/w185" + movie.backdrop_path

        Glide.with(mDialogView)
            .load(posterPath)
            .error(R.drawable.ic_launcher_foreground)
            .fallback(R.drawable.ic_launcher_foreground)
            .listener(loadingListener)
            .into(mDialogView.findViewById(R.id.imageViewBanner))
    }

    /**
     * Metodo para obtener los likes de la pelicula
     */
    fun getLikes(movie: Movie): String {
        return when {
            movie.vote_count == 1 -> "1 Like"
            movie.vote_count != null && movie.vote_count > 1 -> movie.vote_count.toString() + " Likes"
            else -> "No Votes"
        }
    }

    /**
     * Metodo para obtener la valoración de la pelicula
     */
    fun getStars(movie: Movie): String {
        return when {
            movie.vote_average != null && movie.vote_average > 0 -> movie.vote_average.toString() + " / 10"
            else -> "No Reviews"
        }
    }

    /**
     * Metodo para obtener el año de la pelicula
     */
    fun getYear(movie: Movie) = DateUtils.getYear(movie.release_date)

}