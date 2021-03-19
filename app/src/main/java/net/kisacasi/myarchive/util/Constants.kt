package net.kisacasi.myarchive.util

import android.annotation.SuppressLint
import java.util.*
import java.util.Locale.*

class Constants {
    companion object{
        const val BASE_URL="https://api.themoviedb.org/3/"
        const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w342"
        const val API_KEY ="SECRET"
        @SuppressLint("ConstantLocale")
        val API_LANG: String = getDefault().toLanguageTag()
        const val SEARCH_TIME_DELAY=500L
        const val QUERY_PAGE_SIZE=20
    }
}