package net.kisacasi.myarchive.fragments


import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.details_new.*
import net.kisacasi.myarchive.MainActivity
import net.kisacasi.myarchive.R
import net.kisacasi.myarchive.adapters.CategoryAdapter
import net.kisacasi.myarchive.adapters.MovieAdapter
import net.kisacasi.myarchive.adapters.VideosAdapter
import net.kisacasi.myarchive.models.Movie
import net.kisacasi.myarchive.ui.MovieViewModel
import net.kisacasi.myarchive.ui.SheetMaker
import net.kisacasi.myarchive.util.Constants.Companion.POSTER_BASE_URL
import net.kisacasi.myarchive.util.Resource
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MovieDetailFragment : Fragment(R.layout.details_new) {

    private lateinit var moviesAdapterFirst: MovieAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var videoAdapter:VideosAdapter
    lateinit var viewModel: MovieViewModel
    private var movieResponse: Movie?=null
    private lateinit var  movie: Movie
    private lateinit var categoryList:HashMap<Int,Int>
    private val TAG = "Movie Detail Fragment"
    private var savedCase:Boolean=false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        fillCategoryList()
        cleanViewModel()
        movie_overview_flow.setTextSize(46F)
        movie_overview_flow.color=Color.rgb(171,171,171)

        val bundleMain=arguments
        var movieDate:String= String()
        if (bundleMain!!.containsKey("movie")){
            movie= bundleMain.getSerializable("movie") as Movie
            movieDate = movie.releaseDate.toString()
            if (Build.VERSION.SDK_INT >= 26 && !movie.releaseDate.isNullOrBlank()) {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val localDate = LocalDate.parse(movieDate, formatter)
                movieDate = localDate.year.toString()
            } else{
                movieDate= movie.releaseDate?.subSequence(0,4).toString()
            }
        }

        viewModel.findSingleMovie(movie.Mid).observe(viewLifecycleOwner, Observer {
            if (
                it.any()){movieResponse = it[0]
            }

            if (movieResponse?.Mid == movie.Mid){
                savedCase=true
                isSaved()
            }else{
                savedCase=false
                isSaved()
            }


        })

        item_icon_more.setOnClickListener {
            SheetMaker(this.context!!,this.activity!!,movie).setOnItemClickListenerSheet {
                viewModel.clearSimilarMovie()
                val bundle = Bundle().apply {
                    putSerializable("movie_similar", it)
                }
                findNavController().navigate(
                    R.id.action_movieDetailFragment_to_categoryFragment,
                    bundle
                )
            }
        }

        movie.apply {
            movie_title.text = title
            movie_vote_average.text = voteAverage.toString()
            movie_relase_date.text = movieDate
            val html =HtmlCompat.fromHtml("<html>$overview</html>",HtmlCompat.FROM_HTML_MODE_COMPACT)
            movie_overview_flow.text = html
            Glide.with(this@MovieDetailFragment).load(POSTER_BASE_URL + posterPath)
                .into(movie_poster)
        }

        setupForFirstViewModel()

        setupForCategoryViewModel()
        setupForVideos()
    }

    private fun isSaved(){
        if (savedCase){
            item_icon.setImageResource(R.drawable.bookmark_true)
            item_icon.foreground = ResourcesCompat.getDrawable(resources, R.drawable.checkmark, null)
        }
        else{
            item_icon.setImageResource(R.drawable.bookmark)
            item_icon.foreground = ResourcesCompat.getDrawable(resources, R.drawable.add_plus, null)

        }

        item_icon.setOnClickListener {
            val fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out)
            item_icon.startAnimation(fadeOut)
            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }
                override fun onAnimationEnd(animation: Animation?) {
                    if (savedCase){
                        movieResponse=null
                        savedCase=false
                        item_icon.setImageResource(R.drawable.bookmark)
                        item_icon.foreground = ResourcesCompat.getDrawable(resources, R.drawable.add_plus, null)
                        val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                        item_icon.startAnimation(fadeIn)
                        viewModel.deleteMovie(movie)
                    }else{
                        savedCase=true
                        item_icon.setImageResource(R.drawable.bookmark_true)
                        item_icon.foreground = ResourcesCompat.getDrawable(resources, R.drawable.checkmark, null)
                        val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                        item_icon.startAnimation(fadeIn)
                        viewModel.saveMovie(movie)
                    }
                }
                override fun onAnimationStart(animation: Animation?) {
                }
            })
        }
    }

    @SuppressLint("UseSparseArrays")
    private fun fillCategoryList(){
        categoryList=HashMap<Int,Int>().apply {
            put(28,R.drawable.genre_action)
            put(12,R.drawable.genre_adventure)
            put(16,R.drawable.genre_animation)
            put(878,R.drawable.genre_science_fiction)
            put(35,R.drawable.genre_comedy)
            put(80,R.drawable.genre_crime)
            put(14,R.drawable.genre_fantasy)
            put(36,R.drawable.genre_history)
            put(27,R.drawable.genre_horror)
            put(9648,R.drawable.genre_mystery)
            put(10749,R.drawable.genre_romance)
            put(10752,R.drawable.genre_war)
        }
    }

    private fun setupForCategoryViewModel() {
        categoryAdapter = CategoryAdapter(categoryList,false)

        rvMovieDetailCategories.apply {
            adapter = categoryAdapter
            layoutManager=LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

        }


        categoryAdapter.setOnCategoryItemClickListener {
            viewModel.clearGenre()
            val bundle = Bundle().apply {
                putSerializable("category", it)
            }
            findNavController().navigate(
                R.id.action_movieDetailFragment_to_categoryFragment,
                bundle
            )
        }


        viewModel.getMovieDetail(movie.Mid)
        viewModel.movieDetail.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { genresResponse ->
                        categoryAdapter.differ.submitList(genresResponse.genres)
                    }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        when (message) {
                            0 -> {
                                Toast.makeText(context,getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
                            }
                            1 -> {
                                Toast.makeText(context,getString(R.string.network_failure), Toast.LENGTH_SHORT).show()
                            }
                            2 -> {
                                Toast.makeText(context,getString(R.string.error_conversion), Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Log.e(TAG, "An error occurred: $message")
                            }
                        }
                    }
                }
                is Resource.Loading -> {
                }
            }
        })
    }

    private fun setupForVideos() {
        videoAdapter = VideosAdapter()
        rvMovieVideos.apply {
            adapter = videoAdapter
            layoutManager =LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL, false)
        }


        viewModel.getMovieVideos(movie.Mid)
        viewModel.movieVideos.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { moviesResponse ->
                        if (moviesResponse.videosResults.isEmpty()){
                            movieVideosEmpty()
                        }else{
                            videoAdapter.differ.submitList(moviesResponse.videosResults)
                        }

                    }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        when (message) {
                            0 -> {
                                Toast.makeText(context,getString(R.string.no_internet),Toast.LENGTH_SHORT).show()
                            }
                            1 -> {
                                Toast.makeText(context,getString(R.string.network_failure),Toast.LENGTH_SHORT).show()
                            }
                            2 -> {
                                Toast.makeText(context,getString(R.string.error_conversion),Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Log.e(TAG, "An error occurred: $message")
                            }
                        }
                    }
                }
                is Resource.Loading -> {
                }
            }
        })
    }
    private fun setupForFirstViewModel() {
        moviesAdapterFirst = MovieAdapter(1)
        rvSimilarMovie.apply {
            adapter = moviesAdapterFirst
            layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL, false)
        }

        moviesAdapterFirst.setOnLongClickForBottomSheet {
            SheetMaker(this.context!!,this.activity!!,it).setOnItemClickListenerSheet {
                viewModel.clearSimilarMovie()
                val bundle = Bundle().apply {
                    putSerializable("movie_similar", it)
                }
                findNavController().navigate(
                    R.id.action_movieDetailFragment_to_categoryFragment,
                    bundle
                )
            }

        }

        moviesAdapterFirst.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("movie", it)
            }

            findNavController().navigate(
                R.id.action_movieDetailFragment_self,
                bundle
            )
        }
        viewModel.getSimilarMovies(movie.Mid)
        viewModel.similarMovieList.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { moviesResponse ->

                        if (moviesResponse.movies.isEmpty()){
                            movieSimilarEmpty()
                        }else{
                            moviesAdapterFirst.differ.submitList(moviesResponse.movies)
                        }

                    }
                }
                is Resource.Error -> {

                    response.message?.let { message ->
                        when (message) {
                            0 -> {
                                Toast.makeText(context,getString(R.string.no_internet),Toast.LENGTH_SHORT).show()
                            }
                            1 -> {
                                Toast.makeText(context,getString(R.string.network_failure),Toast.LENGTH_SHORT).show()
                            }
                            2 -> {
                                Toast.makeText(context,getString(R.string.error_conversion),Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Log.e(TAG, "An error occurred: $message")
                            }
                        }
                    }
                }
                is Resource.Loading -> {
                }
            }
        })
    }





    private fun movieVideosEmpty(){
        movie_videos_header.visibility=View.GONE
        line_movie_videos.visibility=View.GONE
        rvMovieVideos.visibility=View.GONE
    }

    private fun movieSimilarEmpty(){
        rvSimilarMovie.visibility=View.GONE
        similar_movie_header_line.visibility=View.GONE
        similar_movie_header.visibility=View.GONE
    }

    private fun cleanViewModel() {
        viewModel.similarMovieList.value=null
    }


}
