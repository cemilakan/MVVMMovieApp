package net.kisacasi.myarchive.fragments


import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.kisacasi.myarchive.MainActivity
import net.kisacasi.myarchive.R
import net.kisacasi.myarchive.adapters.MovieAdapter
import net.kisacasi.myarchive.ui.MovieViewModel
import net.kisacasi.myarchive.ui.SheetMaker
import net.kisacasi.myarchive.util.Constants
import net.kisacasi.myarchive.util.Constants.Companion.SEARCH_TIME_DELAY
import net.kisacasi.myarchive.util.Resource

class SearchFragment : Fragment(R.layout.fragment_search) {

    lateinit var viewModelFirst: MovieViewModel
    lateinit var moviesAdapterFirst: MovieAdapter
    private val TAG = "Search Movie Fragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupForFirstViewModel()
        toolbarOptions()

        etSearch.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_in_right))
    }

    private fun toolbarOptions(){
        toolbar_detail.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_in_right))
        toolbar_back_img.startAnimation(AnimationUtils.loadAnimation(context,R.anim.back_btn_anim))
        toolbar_title.text=getString(R.string.search_movies)
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


    private fun setupForFirstViewModel() {
        viewModelFirst = (activity as MainActivity).viewModel
        moviesAdapterFirst = MovieAdapter(0)
        rvSearchMovie.apply {
            adapter = moviesAdapterFirst
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchFragment.scrollListener)
        }

        moviesAdapterFirst.setOnLongClickForBottomSheet {
            SheetMaker(this.context!!,this.activity!!,it).setOnItemClickListenerSheet {
                viewModelFirst.clearSimilarMovie()
//                viewModelFirst.clearGenre()
                val bundle = Bundle().apply {
                    putSerializable("movie_similar", it)
                }
                findNavController().navigate(
                    R.id.action_searchFragment_to_categoryFragment,
                    bundle
                )
            }
        }

        moviesAdapterFirst.setOnItemClickListener {
            viewModelFirst.clearSimilarMovie()
            viewModelFirst.clearMovieVideos()

            val bundle = Bundle().apply {
                putSerializable("movie", it)
            }
            findNavController().navigate(
                R.id.action_searchFragment_to_movieDetailFragment,
                bundle
            )
        }
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun isLongPressDragEnabled(): Boolean {
                return false
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                var swipeIcon = resources.getDrawable(R.drawable.ic_delete_swipe,null) //bg icon
                val right= viewHolder.itemView.right // 1080
                val iconWidth = swipeIcon.intrinsicWidth //120
                val iconMargin = (viewHolder.itemView.height - swipeIcon.intrinsicHeight) / 2//190


                if (dX>0){//swipe right
                    swipeIcon = resources.getDrawable(R.drawable.ic_delete_swipe,null)
                    swipeIcon.bounds = Rect(
                        ((dX.toInt()/2)-(iconWidth/2)),
                        viewHolder.itemView.top +iconMargin,
                        ((dX.toInt()/2)+(iconWidth/2)),
                        viewHolder.itemView.bottom -iconMargin
                    )

                }else if(dX<0){//swipe left
                    swipeIcon = resources.getDrawable(R.drawable.ic_archive_swipe,null) //bg icon
                    swipeIcon.bounds = Rect(
                        right+((dX.toInt()/2)-(iconWidth/2)),
                        viewHolder.itemView.top +iconMargin,
                        right+((dX.toInt()/2)+(iconWidth/2)),
                        viewHolder.itemView.bottom -iconMargin
                    )

                }
                swipeIcon.draw(c)


                super.onChildDraw(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val movie = moviesAdapterFirst.differ.currentList[position]
                if (ItemTouchHelper.LEFT==direction){
                    viewModelFirst.saveMovie(movie)
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
                                viewModelFirst.deleteMovie(movie)
                            }
                        }.show()
                }else{
                    viewModelFirst.deleteMovie(movie)
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
                                viewModelFirst.saveMovie(movie)
                            }
                        }.show()
                }
                moviesAdapterFirst.notifyItemChanged(position)
            }

        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvSearchMovie)

        }
        var job: Job? = null

        etSearch.addTextChangedListener { editable ->
            viewModelFirst.clearSearchedMovie()
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModelFirst.getSearchMoviesModel(editable.toString())
                    }
                }
            }
        }

        viewModelFirst.searchingMovie.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        moviesAdapterFirst.differ.submitList(newsResponse.movies.toList())
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
                viewModelFirst.getSearchMoviesModel(etSearch.text.toString())
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
