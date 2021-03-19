package net.kisacasi.myarchive.api

import net.kisacasi.myarchive.models.*
import net.kisacasi.myarchive.util.Constants.Companion.API_KEY
import net.kisacasi.myarchive.util.Constants.Companion.API_LANG
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesAPI {


    @GET("movie/popular")
    suspend fun getPopularMovie(
        @Query("page") page: Int = 1,
        @Query("language") lang: String = API_LANG,
        @Query("api_key") apiKey: String = API_KEY
    ): Response<MovieResponse>

    @GET("search/movie")
    suspend fun getSearchesForMovie(
        @Query("page") page: Int = 1,
        @Query("query") searchQuery: String,
        @Query("language") lang: String = API_LANG,
        @Query("api_key") apiKey: String = API_KEY
    ): Response<MovieResponse>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovie(
        @Query("page") page: Int = 1,
        @Query("language") lang: String = API_LANG,
        @Query("api_key") apiKey: String = API_KEY
    ): Response<MovieResponse>

    @GET("genre/movie/list")
    suspend fun getAllGenre(
        @Query("language") lang: String = API_LANG,
        @Query("api_key") apiKey: String = API_KEY
    ): Response<GenreResponse>

    @GET("movie/{movie_id}/recommendations")
    suspend fun getSmilar(
        @Path("movie_id") id: Int,
        @Query("language") lang: String = API_LANG,
        @Query("api_key") apiKey: String = API_KEY
    ): Response<MovieResponse>


    @GET("movie/{movie_id}")
    suspend fun getDetailMovie(
        @Path("movie_id") id: Int,
        @Query("language") lang: String = API_LANG,
        @Query("api_key") apiKey: String = API_KEY
    ): Response<MovieDetail>

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String = API_KEY
    ): Response<MovieVideos>

    @GET("discover/movie")
    suspend fun getMovieByGenre(
        @Query("page") page: Int = 1,
        @Query("with_genres") genre: Int,
        @Query("language") lang: String = API_LANG,
        @Query("api_key") apiKey: String = API_KEY
    ): Response<MovieResponse>


    /*
    @GET("movie/upcoming")
    fun getUpcomingMovie(
        @Query("page") page: Int,
        @Query("language") lang: String,
        @Query("api_key") apiKey: String
    ): Response<MovieResponse>



    @GET("movie/now_playing")
    fun getNowPlayingMovie(
        @Query("page") page: Int,
        @Query("language") lang: String,
        @Query("api_key") apiKey: String
    ): Response<MovieResponse>

    @GET("movie/{movie_id}/recommendations")
    fun getRecommendationsMovie(
        @Query("page") page: Int,
        @Query("language") lang: String,
        @Query("api_key") apiKey: String
    ): Response<MovieResponse>

    @GET("movie/{movie_id}/similar")
    fun getSimilarMovie(
        @Query("page") page: Int,
        @Query("language") lang: String,
        @Query("api_key") apiKey: String
    ): Response<MovieResponse>


    @GET("movie/{movie_id}/videos")
    fun getVideosMovie(
        @Query("page") page: Int,
        @Query("language") lang: String,
        @Query("api_key") apiKey: String
    ): Response<MovieResponse>

    @GET("movie/{movie_id}/keywords")
    fun getKeywordsMovie(
        @Query("page") page: Int,
        @Query("language") lang: String,
        @Query("api_key") apiKey: String
    ): Response<MovieResponse>

    @GET("movie/{movie_id}/images")
    fun getPhotosMovie(
        @Query("page") page: Int,
        @Query("language") lang: String,
        @Query("api_key") apiKey: String
    ): Response<MovieResponse>

    //oyuncular ve ekip
    @GET("movie/{movie_id}/credits")
    fun getPlayersMovie(
        @Query("page") page: Int,
        @Query("language") lang: String,
        @Query("api_key") apiKey: String
    ): Response<MovieResponse>


    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Query("language") lang: String,
        @Query("api_key") apiKey: String,
        @Path("movie_id") id: Int
    ): Response<MovieDetail>
    */
}