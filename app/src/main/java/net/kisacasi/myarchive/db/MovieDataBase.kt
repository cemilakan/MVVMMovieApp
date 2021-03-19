package net.kisacasi.myarchive.db

import android.content.Context
import androidx.room.*
import net.kisacasi.myarchive.models.Movie

@Database(
    entities = [Movie::class],
    version = 1
)
//@TypeConverters(Converters::class,ConverterCompany::class, ConverterCountry::class,ConverterLanguage::class)
//@TypeConverters(DataConverter::class)
abstract class MovieDataBase : RoomDatabase() {

    abstract fun getMovieDao(): MovieDao

    companion object {
        @Volatile
        private var instance: MovieDataBase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also {
                instance = it
            }

        }
        private fun createDatabase(context: Context)
                = Room.databaseBuilder(
                context.applicationContext,
                MovieDataBase::class.java,
                "movie_db.db"
        ).build()
    }
}