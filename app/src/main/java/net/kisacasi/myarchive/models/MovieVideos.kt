package net.kisacasi.myarchive.models


import com.google.gson.annotations.SerializedName

data class MovieVideos(
    @SerializedName("id")
    val id: Int,
    @SerializedName("results")
    val videosResults: List<VideosResult>
)