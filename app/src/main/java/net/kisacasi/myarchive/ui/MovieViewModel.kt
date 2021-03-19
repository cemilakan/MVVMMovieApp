package net.kisacasi.myarchive.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.kisacasi.myarchive.models.*
import net.kisacasi.myarchive.repository.MovieRepository
import net.kisacasi.myarchive.util.Resource
import okhttp3.internal.notify
import retrofit2.Response
import java.io.IOException

class MovieViewModel(
    app: Application,
    private val movieRepository: MovieRepository
) : AndroidViewModel(app) {
//) : ViewModel() {
    val popularMovie: MutableLiveData<Resource<MovieResponse>> = MutableLiveData()
    var popularMoviePage = 1
    var popularMovieResponse: MovieResponse? = null

    val topRatedMovie: MutableLiveData<Resource<MovieResponse>> = MutableLiveData()
    var topRatedMoviePage = 1
    var topRatedMovieResponse: MovieResponse? = null

    val searchingMovie: MutableLiveData<Resource<MovieResponse>> = MutableLiveData()
    var searchMoviePage = 1
    var searchingMovieResponse: MovieResponse? = null

    val getByGenreMovie: MutableLiveData<Resource<MovieResponse>> = MutableLiveData()
    var getByGenreMoviePage = 1
    var getByGenreMovieResponse: MovieResponse? = null

    val allGenreList: MutableLiveData<Resource<GenreResponse>> = MutableLiveData()

    val similarMovieList: MutableLiveData<Resource<MovieResponse>> = MutableLiveData()

    val movieDetail: MutableLiveData<Resource<MovieDetail>> = MutableLiveData()

    val movieVideos: MutableLiveData<Resource<MovieVideos>> = MutableLiveData()



    init {
        getPopularMovies()
        getTopRatedMovies()
        getAllGenres()
    }


    fun clearGenre() {
        getByGenreMovie.value = null
        getByGenreMovieResponse = null
    }


    fun clearSearchedMovie() {
        searchMoviePage = 1
        searchingMovieResponse = null
        searchingMovieResponse?.notify()

    }


    fun clearSimilarMovie() {
        similarMovieList.value = null
    }


    fun clearMovieVideos() {
        movieVideos.value = null
    }

    private suspend fun safeGetByGenreMovies(genre: Int){
        getByGenreMovie.postValue(Resource.Loading())
        try {
            if(hasInternetCon()) {
                val response = movieRepository.getMovieByGenre(getByGenreMoviePage, genre)
                getByGenreMovie.postValue(handleByGenreMoviesResponse(response))
            } else {
                //Error Burada Gelir Eğer İnternet Yoksa
                getByGenreMovie.postValue(Resource.Error(0))

            }
        }catch (t: Throwable){
            when(t){
                is IOException -> getByGenreMovie.postValue(Resource.Error(1))
                else -> getByGenreMovie.postValue(Resource.Error(2))
            }
        }

    }

    fun getByGenreMovies(genre: Int) = viewModelScope.launch {
        safeGetByGenreMovies(genre)
    }

    private fun handleByGenreMoviesResponse(response: Response<MovieResponse>): Resource<MovieResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                getByGenreMoviePage++
                if (getByGenreMovieResponse == null) {
                    getByGenreMovieResponse = resultResponse
                } else {
                    val oldArticles = getByGenreMovieResponse?.movies
                    val newArticles = resultResponse.movies
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(getByGenreMovieResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeGetPopularMovies(){
        popularMovie.postValue(Resource.Loading())
        try {
            if(hasInternetCon()) {
                val response = movieRepository.getPopularMovies(popularMoviePage)
                popularMovie.postValue(handlePopularMoviesResponse(response))
            } else {
                popularMovie.postValue(Resource.Error(0))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> popularMovie.postValue(Resource.Error(1))
                else -> popularMovie.postValue(Resource.Error(2))
            }
        }

    }

    fun getPopularMovies() = viewModelScope.launch {
        safeGetPopularMovies()
//        popularMovie.postValue(Resource.Loading())
//        val response = movieRepository.getPopularMovies(popularMoviePage)
//        popularMovie.postValue(handlePopularMoviesResponse(response))
    }

    private fun handlePopularMoviesResponse(response: Response<MovieResponse>): Resource<MovieResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resoultResponse ->
                popularMoviePage++
                if (popularMovieResponse == null) {
                    popularMovieResponse = resoultResponse
                } else {
                    val oldArticles = popularMovieResponse?.movies
                    val newArticles = resoultResponse.movies
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(popularMovieResponse ?: resoultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeGetMovieVideos(movieId: Int){
        movieVideos.postValue(Resource.Loading())
        try {
            if(hasInternetCon()) {
                val response = movieRepository.getMovieVideos(movieId)
                movieVideos.postValue(handleMovieVideosResponse(response))
            } else {
                movieVideos.postValue(Resource.Error(0))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> movieVideos.postValue(Resource.Error(1))
                else -> movieVideos.postValue(Resource.Error(2))
            }
        }

    }

    fun getMovieVideos(movieId: Int) = viewModelScope.launch {
        safeGetMovieVideos(movieId)
//        movieVideos.postValue(Resource.Loading())
//        val response = movieRepository.getMovieVideos(movieId)
//        movieVideos.postValue(handleMovieVideosResponse(response))
    }

    private fun handleMovieVideosResponse(response: Response<MovieVideos>): Resource<MovieVideos> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeGetMovieDetail(movieId: Int){
        movieDetail.postValue(Resource.Loading())
        try {
            if(hasInternetCon()) {
                val response = movieRepository.getMovieDetail(movieId)
                movieDetail.postValue(handleMovieDetailResponse(response))
            } else {
                movieDetail.postValue(Resource.Error(0))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> movieDetail.postValue(Resource.Error(1))
                else -> movieDetail.postValue(Resource.Error(2))
            }
        }

    }

    fun getMovieDetail(movieId: Int) = viewModelScope.launch {
        safeGetMovieDetail(movieId)
//        movieDetail.postValue(Resource.Loading())
//        val response = movieRepository.getMovieDetail(movieId)
//        movieDetail.postValue(handleMovieDetailResponse(response))
    }

    private fun handleMovieDetailResponse(response: Response<MovieDetail>): Resource<MovieDetail> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeGetSimilarMovies(movieId: Int){
        similarMovieList.postValue(Resource.Loading())
        try {
            if(hasInternetCon()) {
                val response = movieRepository.getSimilarMovies(movieId)
                similarMovieList.postValue(handleSimilarMovieResponse(response))
            } else {
                similarMovieList.postValue(Resource.Error(0))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> similarMovieList.postValue(Resource.Error(1))
                else -> similarMovieList.postValue(Resource.Error(2))
            }
        }

    }

    fun getSimilarMovies(movieId: Int) = viewModelScope.launch {
        safeGetSimilarMovies(movieId)
//        similarMovieList.postValue(Resource.Loading())
//        val response = movieRepository.getSimilarMovies(movieId)
//        similarMovieList.postValue(handleSimilarMovieResponse(response))
    }

    private fun handleSimilarMovieResponse(response: Response<MovieResponse>): Resource<MovieResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeGetAllGenres(){
        allGenreList.postValue(Resource.Loading())
        try {
            if(hasInternetCon()) {
                val response = movieRepository.getAllGenres()
                allGenreList.postValue(handleAllGenreResponse(response))
            } else {
                allGenreList.postValue(Resource.Error(0))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> allGenreList.postValue(Resource.Error(1))
                else -> allGenreList.postValue(Resource.Error(2))
            }
        }

    }

    private fun getAllGenres() = viewModelScope.launch {
        safeGetAllGenres()
//        allGenreList.postValue(Resource.Loading())
//        val response = movieRepository.getAllGenres()
//        allGenreList.postValue(handleAllGenreResponse(response))
    }

    private fun handleAllGenreResponse(response: Response<GenreResponse>): Resource<GenreResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeGetTopRatedMovies(){
        topRatedMovie.postValue(Resource.Loading())
        try {
            if(hasInternetCon()) {
                val response = movieRepository.getTopRatedMovies(topRatedMoviePage)
                topRatedMovie.postValue(handleTopRatedMoviesResponse(response))
            } else {
                topRatedMovie.postValue(Resource.Error(0))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> topRatedMovie.postValue(Resource.Error(1))
                else -> topRatedMovie.postValue(Resource.Error(2))
            }
        }

    }

    fun getTopRatedMovies() = viewModelScope.launch {
        safeGetTopRatedMovies()
//        topRatedMovie.postValue(Resource.Loading())
//        val response = movieRepository.getTopRatedMovies(topRatedMoviePage)
//        topRatedMovie.postValue(handleTopRatedMoviesResponse(response))
    }

    private fun handleTopRatedMoviesResponse(response: Response<MovieResponse>): Resource<MovieResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                topRatedMoviePage++
                if (topRatedMovieResponse == null) {
                    topRatedMovieResponse = resultResponse
                } else {
                    val oldArticles = topRatedMovieResponse?.movies
                    val newArticles = resultResponse.movies
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(topRatedMovieResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeGetSearchMoviesModel(query: String){
        searchingMovie.postValue(Resource.Loading())
        try {
            if(hasInternetCon()) {
                val response = movieRepository.getSearchesForMovies(query, searchMoviePage)
                searchingMovie.postValue(handleSearchMoviesResponse(response))
            } else {
                searchingMovie.postValue(Resource.Error(0))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> searchingMovie.postValue(Resource.Error(1))
                else -> searchingMovie.postValue(Resource.Error(2))
            }
        }

    }

    fun getSearchMoviesModel(query: String) = viewModelScope.launch {
        safeGetSearchMoviesModel(query)
//        searchingMovie.postValue(Resource.Loading())
//        val response = movieRepository.getSearchesForMovies(query, searchMoviePage)
//        searchingMovie.postValue(handleSearchMoviesResponse(response))
    }

    private fun handleSearchMoviesResponse(response: Response<MovieResponse>): Resource<MovieResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchMoviePage++
                if (searchingMovieResponse == null) {
                    searchingMovieResponse = resultResponse
                } else {
                    val oldArticles = searchingMovieResponse?.movies
                    val newArticles = resultResponse.movies
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchingMovieResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    //Room Etc.

    fun saveMovie(movie: Movie) = viewModelScope.launch {
        movieRepository.upsert(movie)
    }

    fun getSavedMovies() = movieRepository.getSavedMovies()

    fun findSingleMovie(key: Int) =  movieRepository.getSavedSingleMovie(key)

    fun deleteMovie(movie: Movie) = viewModelScope.launch {
        movieRepository.deleteMovie(movie)
    }

    fun PublicNetControl():Boolean{
        return hasInternetCon()
    }

    private fun hasInternetCon(): Boolean {
        val connectivityManager = getApplication<MovieApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}