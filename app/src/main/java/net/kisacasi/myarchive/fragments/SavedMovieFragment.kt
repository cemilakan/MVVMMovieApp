package net.kisacasi.myarchive.fragments


import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_movie.*
import net.kisacasi.myarchive.MainActivity
import net.kisacasi.myarchive.R
import net.kisacasi.myarchive.adapters.MovieAdapter
import net.kisacasi.myarchive.ui.MovieViewModel
import net.kisacasi.myarchive.ui.SheetMaker


class SavedMovieFragment : Fragment(R.layout.fragment_saved_movie) {
    lateinit var viewModel: MovieViewModel
    lateinit var movieAdapter: MovieAdapter
    val TAG = "Saved Movie Fragment"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupForFirstViewModel()

    }
    private fun setupForFirstViewModel() {
        viewModel = (activity as MainActivity).viewModel
        movieAdapter = MovieAdapter(0)
        rvSavedMovie.apply {
            adapter = movieAdapter
            layoutManager = LinearLayoutManager(activity)

        }

        movieAdapter.setOnLongClickForBottomSheet {
            SheetMaker(this.context!!,this.activity!!,it).setOnItemClickListenerSheet {
                viewModel.clearSimilarMovie()
                val bundle = Bundle().apply {
                    putSerializable("movie_similar", it)
                }
                findNavController().navigate(
                    R.id.action_savedMovieFragment_to_categoryFragment,
                    bundle
                )
            }
        }


        movieAdapter.setOnItemClickListener {
            viewModel.clearSimilarMovie()
            viewModel.clearMovieVideos()
            val bundle = Bundle().apply {
                putSerializable("movie", it)
            }
            findNavController().navigate(
                R.id.action_savedMovieFragment_to_movieDetailFragment,
                bundle
            )
        }

        val itemTouchHelperCallback= object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {

            override fun isLongPressDragEnabled(): Boolean {
                return false
            }


            override fun onChildDraw(c: Canvas,recyclerView: RecyclerView,viewHolder: RecyclerView.ViewHolder,dX: Float,dY: Float,actionState: Int,isCurrentlyActive: Boolean) {
                val deleteIcon = resources.getDrawable(R.drawable.ic_delete_swipe,null) //bg icon
                val right= viewHolder.itemView.right // 1080
                val iconWidth = deleteIcon.intrinsicWidth //120
                val iconMargin = (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2//190


                if (dX>0){//swipe right
                    deleteIcon.bounds = Rect(
                        ((dX.toInt()/2)-(iconWidth/2)),
                        viewHolder.itemView.top +iconMargin,
                        ((dX.toInt()/2)+(iconWidth/2)),
                        viewHolder.itemView.bottom -iconMargin
                    )


                }else if(dX<0){//swipe left
                    deleteIcon.bounds = Rect(
                        right+((dX.toInt()/2)-(iconWidth/2)),
                        viewHolder.itemView.top +iconMargin,
                        right+((dX.toInt()/2)+(iconWidth/2)),
                        viewHolder.itemView.bottom -iconMargin
                    )

                }
                deleteIcon.draw(c)

                super.onChildDraw(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)

            }

            override fun onMove( recyclerView: RecyclerView,viewHolder: RecyclerView.ViewHolder,target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position=viewHolder.adapterPosition
                val movie=movieAdapter.differ.currentList[position]

                    viewModel.deleteMovie(movie)
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
                                viewModel.saveMovie(movie)
                            }
                        }.show()
                }


        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvSavedMovie)
        }


        viewModel.getSavedMovies().observe(viewLifecycleOwner, Observer { response ->
                        movieAdapter.differ.submitList(response)
        })
    }




}
