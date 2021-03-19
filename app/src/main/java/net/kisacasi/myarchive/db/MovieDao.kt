package net.kisacasi.myarchive.db

import androidx.lifecycle.LiveData
import androidx.room.*
import net.kisacasi.myarchive.models.Movie

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun  upsert(movieDetail: Movie): Long

    @Query("SELECT * FROM movies ORDER BY saved_date DESC")
    fun getAllMovies(): LiveData<List<Movie>>

    @Delete
    suspend fun deleteMovie(article: Movie)

    @Query("SELECT * FROM movies WHERE Mid=:key")
    fun findMovieFromDb(key:Int): LiveData<List<Movie>>

//    @Query("SELECT * FROM movies WHERE Mid=:key")
//    fun findMovieFromDb(key:Int): LiveData<List<Movie>>
}