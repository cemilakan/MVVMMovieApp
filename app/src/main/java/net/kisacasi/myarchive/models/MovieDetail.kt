package net.kisacasi.myarchive.models


import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import net.kisacasi.myarchive.db.ConverterCompany
import net.kisacasi.myarchive.db.ConverterCountry
import net.kisacasi.myarchive.db.Converters


data class MovieDetail(
    @SerializedName("adult")
    val adult: Boolean,
    @SerializedName("backdrop_path")
    val backdropPath: String,
    @SerializedName("budget")
    val budget: Int,
    @TypeConverters(Converters::class)
    @SerializedName("genres")
    val genres: List<Genre>,
    @SerializedName("homepage")
    val homepage: String,
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    var Mid: Int,
    @SerializedName("imdb_id")
    val MimdbId: String,
    @SerializedName("original_language")
    val originalLanguage: String,
    @SerializedName("original_title")
    val originalTitle: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("popularity")
    val popularity: Double,
    @SerializedName("poster_path")
    val posterPath: String,
    @TypeConverters(ConverterCompany::class)
    @SerializedName("production_companies")
    val productionCompanies: List<ProductionCompany>,
    @TypeConverters(ConverterCountry::class)
    @SerializedName("production_countries")
    val productionCountries: List<ProductionCountry>,
    @SerializedName("release_date")
    val releaseDate: String,
    @TypeConverters(SpokenLanguage::class)
    @SerializedName("spoken_languages")
    val spokenLanguages: List<SpokenLanguage>,
    @SerializedName("status")
    val status: String,
    @SerializedName("tagline")
    val tagline: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("video")
    val video: Boolean,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int
)