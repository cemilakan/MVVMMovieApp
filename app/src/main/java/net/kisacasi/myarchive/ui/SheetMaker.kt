package net.kisacasi.myarchive.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.item_sheet_menu.view.*
import net.kisacasi.myarchive.MainActivity
import net.kisacasi.myarchive.R
import net.kisacasi.myarchive.fragments.CategoryFragment
import net.kisacasi.myarchive.models.Movie
import net.kisacasi.myarchive.util.Constants.Companion.POSTER_BASE_URL


class SheetMaker(
    val context: Context,
    val activity: Activity,
    val movieResp: Movie
) {
    init {
        showBottomSheet(movieResp)

    }



    private fun showBottomSheet(movie: Movie){
        val viewModel = (activity as MainActivity).viewModel

        val mBottomSheetDialog = BottomSheetDialog(context)
        val sheetView: View =activity.layoutInflater.inflate(R.layout.item_sheet_menu, null)
        sheetView.setBackgroundColor(R.drawable.rounded)
        mBottomSheetDialog.setContentView(sheetView)
        mBottomSheetDialog.show()
        sheetView.button_click_later.setOnClickListener {
            viewModel.saveMovie(movie)
            mBottomSheetDialog.dismiss()
        }

        sheetView.button_click_remove.setOnClickListener {
            viewModel.deleteMovie(movie)
            mBottomSheetDialog.dismiss()
        }

        sheetView.button_click_share.setOnClickListener {
            val percentage =(movie.overview!!.length * 30) / 100
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT,movie.originalTitle+"\nFilm Puanı : "+movie.voteAverage+"\nYayın Tarihi : "+movie.releaseDate?.subSequence(0,4)+"\n"+movie.overview.subSequence(0,percentage).toString()+"...")
                putExtra(Intent.EXTRA_TITLE, movie.title)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Film gönder...")
            context.startActivity(shareIntent)


            mBottomSheetDialog.dismiss()
        }

        sheetView.button_click_similar.setOnClickListener {
            onItemClickListenerSheet?.let {
                it(movie)
            }

            mBottomSheetDialog.dismiss()
        }

        sheetView.button_click_description.setOnClickListener {
            mBottomSheetDialog.dismiss()
        }

    }

    private var onItemClickListenerSheet: ((Movie) -> Unit)? = null

    fun setOnItemClickListenerSheet(listener: (Movie) -> Unit) {
        onItemClickListenerSheet = listener
    }
}