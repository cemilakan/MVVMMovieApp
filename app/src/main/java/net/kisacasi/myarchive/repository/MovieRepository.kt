package net.kisacasi.myarchive.repository

import net.kisacasi.myarchive.api.RetrofitInstance
import net.kisacasi.myarchive.models.Movie
import net.kisacasi.myarchive.db.MovieDataBase

class MovieRepository(
    val db: MovieDataBase
) {
    suspend fun getPopularMovies(page: Int)=
        RetrofitInstance.api.getPopularMovie(page)

    suspend fun getSimilarMovies(movieId:Int)=
        RetrofitInstance.api.getSmilar(movieId)

    suspend fun getMovieDetail(movieId:Int)=
        RetrofitInstance.api.getDetailMovie(movieId)

    suspend fun getMovieVideos(movieId:Int)=
        RetrofitInstance.api.getMovieVideos(movieId)

    suspend fun getAllGenres()=
        RetrofitInstance.api.getAllGenre()

    suspend fun getSearchesForMovies(searQuery: String,pageNumber:Int)=
        RetrofitInstance.api.getSearchesForMovie(pageNumber,searQuery)

    suspend fun getTopRatedMovies(page: Int)=
        RetrofitInstance.api.getTopRatedMovie(page)

    suspend fun getMovieByGenre(page: Int,genre:Int)=
        RetrofitInstance.api.getMovieByGenre(page,genre)

    suspend fun upsert(movie:Movie)=db.getMovieDao().upsert(movie)

    fun getSavedMovies()=db.getMovieDao().getAllMovies()

    fun getSavedSingleMovie(key:Int)=db.getMovieDao().findMovieFromDb(key)

    suspend fun deleteMovie(movie: Movie)= db.getMovieDao().deleteMovie(movie)

}