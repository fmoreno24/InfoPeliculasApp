import com.fmoreno.infopeliculasapp.model.Movie

class MovieResponse {
    var page = 0
    var results: List<Movie>? = null
    var total_pages = 0
    var total_results = 0
}