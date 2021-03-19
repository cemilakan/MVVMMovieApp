package net.kisacasi.myarchive.adapters

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.cover_item.view.*
import kotlinx.android.synthetic.main.rv_vertical_movie.view.*
import net.kisacasi.myarchive.R
import net.kisacasi.myarchive.models.Movie
import net.kisacasi.myarchive.util.Constants.Companion.POSTER_BASE_URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MovieAdapter(
    val requestType: Int
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {
    lateinit var view: View

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.Mid == newItem.Mid
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        when (requestType) {
            0 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rv_vertical_movie, parent, false)
            }
            1 -> {
                view =
                    LayoutInflater.from(parent.context).inflate(R.layout.cover_item, parent, false)
            }

        }

        return MovieViewHolder(view)

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = differ.currentList[position]
        var movieDate = movie.releaseDate
        if (Build.VERSION.SDK_INT >= 26 && !movie.releaseDate.isNullOrBlank()) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val localDate = LocalDate.parse(movieDate, formatter)
            movieDate = localDate.year.toString()
        }else{
            movieDate= movie.releaseDate?.subSequence(0,4).toString()
        }

        holder.itemView.apply {
            when (requestType) {
                0 -> {
                    Glide.with(this).load(POSTER_BASE_URL + movie.posterPath)
                        .into(movie_image_poster)
                    movie_title.text = movie.title
                    movie_relase_date.text =movieDate
                    movie_overview.text = movie.overview
                    //animasyon
                    val resId: Int = R.anim.layout_animation_slide_right
                    val animation = AnimationUtils.loadLayoutAnimation(context, resId)
                    holder.itemView.vertical_movie_root_layout.layoutAnimation = animation

                    setOnLongClickListener{
                        onItemClickListenerSecondView?.let {
                            it(movie)
                        }
                        true
                    }

                    setOnClickListener {
                        onItemClickListener?.let {
                            it(movie)
                        }
                    }

                }
                1 -> {
                    Glide.with(this).load(POSTER_BASE_URL + movie.backdropPath)
                        .into(item_cover_picture)
                    tv_cover_title.text = movie.title
                    movie_vote_avarage.text = movie.voteAverage.toString()
                    val resId: Int = R.anim.layout_animation_fall_down
                    val animation = AnimationUtils.loadLayoutAnimation(context, resId)
                    holder.itemView.cover_item_main_layout.layoutAnimation = animation
                    cover_parent.setOnLongClickListener {
                        onItemClickListenerSecondView?.let {
                            it(movie)
                        }
                        true
                    }
                    cover_parent.setOnClickListener {
                        onItemClickListener?.let {
                            it(movie)
                        }
                    }


                }

            }

        }


    }



    private var onItemClickListenerSecondView: ((Movie) -> Unit)? = null

    fun setOnLongClickForBottomSheet(listener: (Movie) -> Unit) {
        onItemClickListenerSecondView = listener
    }

    private var onItemClickListener: ((Movie) -> Unit)? = null

    fun setOnItemClickListener(listener: (Movie) -> Unit) {
        onItemClickListener = listener
    }


}

