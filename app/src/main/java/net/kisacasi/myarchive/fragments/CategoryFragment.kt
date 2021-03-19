package net.kisacasi.myarchive.fragments


import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_category.*
import net.kisacasi.myarchive.MainActivity
import net.kisacasi.myarchive.R
import net.kisacasi.myarchive.adapters.MovieAdapter
import net.kisacasi.myarchive.models.Genre
import net.kisacasi.myarchive.models.Movie
import net.kisacasi.myarchive.ui.MovieViewModel
import net.kisacasi.myarchive.ui.SheetMaker
import net.kisacasi.myarchive.util.Constants
import net.kisacasi.myarchive.util.Resource


class CategoryFragment : Fragment(R.layout.fragment_category) {
    private lateinit var viewModelCategory: MovieViewModel
    private lateinit var moviesAdapterFirst: MovieAdapter
    private var responseGenre: Genre? = null
    private lateinit var movieResponse: Movie
    private val TAG = "Category Movie Fragment"
    private var responseFromHome=0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelCategory = (activity as MainActivity).viewModel

        val bundle=arguments

        when {
            bundle!!.containsKey("category") -> {
                responseGenre = arguments?.let {
                    it.getSerializable("category") as Genre
                }
                toolbar_title.text = responseGenre?.name
                responseFromHome=0
                setupForFirstViewModel(0)
            }
            bundle.containsKey("movie_similar") -> {
                movieResponse = arguments.let {
                    it?.getSerializable("movie_similar") as Movie
                }
                val title = if(movieResponse.title?.length!! > 18){
                    getString(R.string.kind_of,movieResponse.title?.subSequence(0,17).toString())

                } else{
                    getString(R.string.kind_of,movieResponse.title)
                }
//                title=getString(R.string.kind_of,"d")
                toolbar_title.text=title
                responseFromHome=1
                setupForFirstViewModel(1)
            }
            bundle.containsKey("top_rated") -> {
                toolbar_title.text=getString(R.string.top_rated)
                responseFromHome=2
                setupForFirstViewModel(2)
            }
            bundle.containsKey("most_popular") -> {
                toolbar_title.text=getString(R.string.most_popular)
                responseFromHome=3
                setupForFirstViewModel(3)
            }
        }

        toolbarOptions()

    }

    private fun toolbarOptions() {
        toolbar_detail.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_right))
        toolbar_back_img.startAnimation(AnimationUtils.loadAnimation(context, R.anim.back_btn_anim))
        rvCategoriMovie.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_right))

        toolbar_back_img.setOnClickListener {
            findNavController().navigate(
                R.id.homeFragment
            )
        }
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.GONE
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }


    private fun setupForFirstViewModel(whatComes:Int) {
        when(whatComes){
            0 -> {
                viewModelCategory.getByGenreMovies(responseGenre!!.id)
                viewModelCategory.getByGenreMovie.observe(viewLifecycleOwner, Observer { response ->
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

            1 -> {
                viewModelCategory.getSimilarMovies(movieResponse.Mid)
                viewModelCategory.similarMovieList.observe(viewLifecycleOwner, Observer { response ->
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
            2 -> {
                viewModelCategory.topRatedMovie.observe(viewLifecycleOwner, Observer { response ->
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
            })}
            3 -> {
                viewModelCategory.popularMovie.observe(viewLifecycleOwner, Observer { response ->
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
        }


        moviesAdapterFirst = MovieAdapter(0)
        rvCategoriMovie.apply {
            adapter = moviesAdapterFirst
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@CategoryFragment.scrollListener)

        }

        moviesAdapterFirst.setOnLongClickForBottomSheet {
            SheetMaker(this.context!!,this.activity!!,it).setOnItemClickListenerSheet {
                viewModelCategory.clearSimilarMovie()
                val bundle = Bundle().apply {
                    putSerializable("movie_similar", it)
                }
                findNavController().navigate(
                    R.id.action_categoryFragment_self,
                    bundle
                )
            }

        }

        moviesAdapterFirst.setOnItemClickListener {
            viewModelCategory.clearSimilarMovie()
            viewModelCategory.clearMovieVideos()
            val bundle = Bundle().apply {
                putSerializable("movie", it)
            }
            findNavController().navigate(
                R.id.action_categoryFragment_to_movieDetailFragment,
                bundle
            )
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView,viewHolder: RecyclerView.ViewHolder,target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun isLongPressDragEnabled(): Boolean {
                return false
            }

            override fun onChildDraw(c: Canvas,recyclerView: RecyclerView,viewHolder: RecyclerView.ViewHolder,dX: Float,dY: Float,actionState: Int,isCurrentlyActive: Boolean) {
                val background= ColorDrawable(resources.getColor(R.color.colorAccent))
                var trashBinIcon = resources.getDrawable(R.drawable.ic_delete_swipe,null) //bg icon
                val right= viewHolder.itemView.right // 1080
                val iconWidth = trashBinIcon.intrinsicWidth //120
                val iconMargin = (viewHolder.itemView.height - trashBinIcon.intrinsicHeight) / 2//190

                if (dX>0){//swipe right
                    trashBinIcon = resources.getDrawable(R.drawable.ic_delete_swipe,null)
                    trashBinIcon.bounds = Rect(
                        ((dX.toInt()/2)-(iconWidth/2)),
                        viewHolder.itemView.top +iconMargin,
                        ((dX.toInt()/2)+(iconWidth/2)),
                        viewHolder.itemView.bottom -iconMargin
                    )

                }else if(dX<0){//swipe left
                    trashBinIcon = resources.getDrawable(R.drawable.ic_archive_swipe,null) //bg icon
                    trashBinIcon.bounds = Rect(
                        right+((dX.toInt()/2)-(iconWidth/2)),
                        viewHolder.itemView.top +iconMargin,
                        right+((dX.toInt()/2)+(iconWidth/2)),
                        viewHolder.itemView.bottom -iconMargin
                    )

                }
                background.draw(c)
                trashBinIcon.draw(c)


                super.onChildDraw(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val movie = moviesAdapterFirst.differ.currentList[position]
                if (ItemTouchHelper.LEFT==direction){
                    viewModelCategory.saveMovie(movie)
                    Snackbar.make(paginationProgressBar, getString(R.string.saved_succesfully), Snackbar.LENGTH_LONG)
                        .apply {
                            val textView = this.view.findViewById(R.id.snackbar_text) as TextView
                            textView.setTextColor(ContextCompat.getColor(this.context, R.color.colorWhite))
                            textView.textSize= resources.getFloat(R.dimen.snack_text_size)
                            this.setActionTextColor(ContextCompat.getColor(this.context, R.color.colorAccent))
                            this.view.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorPrimaryDark))
                            view.layoutParams = (view.layoutParams as FrameLayout.LayoutParams)
                                .apply {
                                    setMargins(16, topMargin, 16, toolbar_detail.height)
                                }
                            setAction(getString(R.string.undo)) {
                                viewModelCategory.deleteMovie(movie)
                            }
                        }.show()

                }else if(ItemTouchHelper.RIGHT == direction){
                    viewModelCategory.deleteMovie(movie)
                    Snackbar.make(paginationProgressBar, getString(R.string.succesfully_deleted), Snackbar.LENGTH_LONG)
                        .apply {
                            val textView = this.view.findViewById(R.id.snackbar_text) as TextView
                            textView.setTextColor(ContextCompat.getColor(this.context, R.color.colorWhite))
                            textView.textSize= resources.getFloat(R.dimen.snack_text_size)
                            this.setActionTextColor(ContextCompat.getColor(this.context, R.color.colorAccent))
                            this.view.setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorPrimaryDark))
                            view.layoutParams = (view.layoutParams as FrameLayout.LayoutParams)
                                .apply {
                                    setMargins(16, topMargin, 16, toolbar_detail.height)
                                }
                            setAction(getString(R.string.undo)) {
                                viewModelCategory.saveMovie(movie)
                            }
                        }.show()
                }

                moviesAdapterFirst.notifyItemChanged(position)
            }

        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvCategoriMovie)

        }
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
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                when(responseFromHome){
                    0 ->{
                        viewModelCategory.getByGenreMovies(responseGenre!!.id)
                        isScrolling = false
                    }
                    2 ->{
                        viewModelCategory.getTopRatedMovies()
                        isScrolling = false
                    }
                    3 ->{
                        viewModelCategory.getPopularMovies()
                        isScrolling = false
                    }
                }

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
