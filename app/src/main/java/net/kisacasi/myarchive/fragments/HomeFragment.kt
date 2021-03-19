package net.kisacasi.myarchive.fragments


import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.error_layout.*
import kotlinx.android.synthetic.main.fragment_home.*
import net.kisacasi.myarchive.MainActivity
import net.kisacasi.myarchive.R
import net.kisacasi.myarchive.adapters.CategoryAdapter
import net.kisacasi.myarchive.adapters.MovieAdapter
import net.kisacasi.myarchive.ui.MovieViewModel
import net.kisacasi.myarchive.ui.SheetMaker
import net.kisacasi.myarchive.util.Constants
import net.kisacasi.myarchive.util.Resource


class HomeFragment : Fragment(R.layout.fragment_home) {

    lateinit var viewModel: MovieViewModel
    lateinit var moviesAdapterFirst: MovieAdapter
    lateinit var moviesAdapterSecond: MovieAdapter
    lateinit var categoryAdapter: CategoryAdapter
    lateinit var categoryList:HashMap<Int,Int>
    val TAG = "Movies Home Fragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        relativeLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_right))
        rl_categories.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_right))
        rl_most_popular.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_right))
        viewModelFirstFragmentSettings()
        viewAllSettings()
    }

    private fun viewAllSettings(){
        top_rated_view_all.setOnClickListener {
            val bundle = Bundle().apply {
                putBoolean("top_rated", true)
            }
            findNavController().navigate(
                R.id.action_homeFragment_to_categoryFragment,
                bundle
            )
        }
        most_popular_view_all.setOnClickListener {
            val bundle = Bundle().apply {
                putBoolean("most_popular", true)
            }
            findNavController().navigate(
                R.id.action_homeFragment_to_categoryFragment,
                bundle
            )
        }
    }

    private fun viewModelFirstFragmentSettings(){
        fillCategoryList()
        setupForFirstViewModel()
        setupForSecondViewModel()
        setupForCategoryViewModel()
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
        categoryAdapter = CategoryAdapter(categoryList,true)

        rvHomeCategories.apply {
            adapter = categoryAdapter
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)

        }


        categoryAdapter.setOnCategoryItemClickListener {
            viewModel.clearGenre()
            val bundle = Bundle().apply {
                putSerializable("category", it)
            }
            findNavController().navigate(
                R.id.action_homeFragment_to_categoryFragment,
                bundle
            )
        }


        viewModel.allGenreList.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { genresResponse ->
                        categoryAdapter.differ.submitList(genresResponse.genres)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
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
                    showProgressBar()
                }
            }
        })
    }




    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.GONE
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }


    private fun setupForFirstViewModel() {
        moviesAdapterFirst = MovieAdapter(0)
        rvHomeMoviePopular.apply {
            adapter = moviesAdapterFirst
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@HomeFragment.scrollListener)
        }

        moviesAdapterFirst.setOnItemClickListener {
            viewModel.clearSimilarMovie()
            viewModel.clearMovieVideos()
            val bundle = Bundle().apply {
                putSerializable("movie", it)
            }
            findNavController().navigate(
                R.id.action_homeFragment_to_movieDetailFragment,
                bundle
            )
        }
        moviesAdapterFirst.setOnLongClickForBottomSheet {
            SheetMaker(this.context!!,this.activity!!,it).setOnItemClickListenerSheet {
                viewModel.clearSimilarMovie()
//                viewModel.clearGenre()
                val bundle = Bundle().apply {
                    putSerializable("movie_similar", it)
                }
                findNavController().navigate(
                    R.id.action_homeFragment_to_categoryFragment,
                    bundle
                )
            }
        }


        viewModel.popularMovie.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { moviesResponse ->
                        moviesAdapterFirst.differ.submitList(moviesResponse.movies)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
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
                    showProgressBar()
                }
            }
        })
    }

    private fun setupForSecondViewModel() {
        moviesAdapterSecond = MovieAdapter(1)
        rvHomeMovieTopRated.apply {
            adapter = moviesAdapterSecond
            layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL, false)
        }
        rvHomeMovieTopRated.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount

                val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
                val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
                val isNotAtBeginning = firstVisibleItemPosition >= 0
                val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
                val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling


                if (shouldPaginate) {
                    viewModel.getTopRatedMovies()
                    isScrolling = false
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }
        })


        moviesAdapterSecond.setOnItemClickListener {
            viewModel.clearSimilarMovie()
            viewModel.clearMovieVideos()
            val bundle = Bundle().apply {
                putSerializable("movie", it)
            }
            findNavController().navigate(
                R.id.action_homeFragment_to_movieDetailFragment,
                bundle
            )
        }
        moviesAdapterSecond.setOnLongClickForBottomSheet {
            SheetMaker(this.context!!,this.activity!!,it).setOnItemClickListenerSheet {
                viewModel.clearSimilarMovie()
//                viewModel.clearGenre()
                val bundle = Bundle().apply {
                    putSerializable("movie_similar", it)
                }
                findNavController().navigate(
                    R.id.action_homeFragment_to_categoryFragment,
                    bundle
                )
            }


        }
        viewModel.topRatedMovie.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { moviesResponse ->
                        moviesAdapterSecond.differ.submitList(moviesResponse.movies)

                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
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
                    showProgressBar()
                }
            }
        })
    }


    var isLoading = false
    var isLastPage = false
    var isScrolling = false



    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                viewModel.getPopularMovies()
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

    }



}
