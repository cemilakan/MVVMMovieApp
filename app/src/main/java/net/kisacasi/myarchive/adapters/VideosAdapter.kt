package net.kisacasi.myarchive.adapters

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.rv_detail_movie_videos.view.*
import net.kisacasi.myarchive.R
import net.kisacasi.myarchive.models.VideosResult


class VideosAdapter: RecyclerView.Adapter<VideosAdapter.VideosViewHolder>() {

    inner class VideosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<VideosResult>() {
        override fun areItemsTheSame(oldItem: VideosResult, newItem: VideosResult): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VideosResult, newItem: VideosResult): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideosViewHolder {

        val view= LayoutInflater.from(parent.context).inflate(R.layout.rv_detail_movie_videos,parent,false)
        return VideosViewHolder(view)

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: VideosViewHolder, position: Int) {
        val video=differ.currentList[position]
        holder.itemView.apply {
            //get youtube cover https://img.youtube.com/vi/-gelen key-/sddefault.jpg
            Glide.with(this).load("https://img.youtube.com/vi/"+video.key+"/sddefault.jpg").into(video_thumbnail)
            video_name.text=video.name
        }
        holder.itemView.video_root.setOnClickListener {
                val intent =Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://"+video.key))
                startActivity(holder.itemView.context,intent,null)
            Log.e("fdokpbfjo","fkdjlfnkğkok")
        }
    }

}